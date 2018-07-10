/*******************************************************************************
 * Copyright 2012 EMBL-EBI, Hinxton outstation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package uk.ac.ebi.embl.api.translation;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.feature.CdsFeature;
import uk.ac.ebi.embl.api.entry.feature.PeptideFeature;
import uk.ac.ebi.embl.api.entry.feature.SourceFeature;
import uk.ac.ebi.embl.api.entry.location.CompoundLocation;
import uk.ac.ebi.embl.api.entry.location.Location;
import uk.ac.ebi.embl.api.entry.qualifier.CodonQualifier;
import uk.ac.ebi.embl.api.entry.qualifier.Qualifier;
import uk.ac.ebi.embl.api.entry.qualifier.TranslExceptQualifier;
import uk.ac.ebi.embl.api.entry.sequence.SequenceFactory;
import uk.ac.ebi.embl.api.validation.*;
import uk.ac.ebi.embl.api.validation.check.sequence.SequenceBasesCheck;
import uk.ac.ebi.embl.api.validation.helper.taxon.TaxonHelper;
import uk.ac.ebi.ena.taxonomy.taxon.Taxon;

import java.util.*;

/**
 * Translates a bases to an amino acid sequences. The bases are encoded using
 * lower case single letter JCBN abbreviations and the amino acids are encoded
 * using upper case single letter JCBN abbreviations.
 */
public class Translator extends AbstractTranslator
{
	private Map<Integer, TranslationException> translationExceptionMap = new HashMap<>();
	private int codonStart = 1;
	private boolean nonTranslating = false;//feature has /pseudo
	private boolean exception = false;
	private boolean rightPartial = false;//3' partial
	private boolean leftPartial = false;//5' partial
	private boolean peptideFeature = false;

	private boolean fixDegenerateStartCodon = false;//understand this using testcase
	private boolean fixMissingStartCodon = false;//missing startCodon , make it 5' partial
	private boolean fixStartCodonOffset = false;//codon start!= 1 , make it 5' partial
	private boolean fixRightPartialStopCodon = false;//remove 3' partial
	private boolean fixRightPartialCodon = false;//make 3' partial, only one stopCodon but >0 bases after stopCodon
	private boolean fixMultipleOfThree = false;// make 3' and 5' partial
	private boolean fixInternalStopCodon = false;// make feature as pseudo if there are internal stop codons
	//private boolean deleteNotMultipleOfThree = false;//delete this feature
	private Set<String> fixes;

	public Translator() {
		fixes = new HashSet<>();
	}

	public Set<String> getFixes() {
		return fixes;
	}

	public void setFixInternalStopCodon(boolean fixInternalStopCodon) {
		this.fixInternalStopCodon = fixInternalStopCodon;
	}

	public void setFixMultipleOfThree(boolean fixMultipleOfThree) {
		this.fixMultipleOfThree = fixMultipleOfThree;
	}

	public void setPeptideFeature(boolean peptideFeature) {
		this.peptideFeature = peptideFeature;
	}

	public boolean isPeptideFeature() {
		return peptideFeature;
	}


	public void setCodonStart(int startCodon)
	{
		this.codonStart = startCodon;
	}

	public void setLeftPartial(boolean leftPartial)
	{
		this.leftPartial = leftPartial;
	}

	public void setRightPartial(boolean rightPartial)
	{
		this.rightPartial = rightPartial;
	}

	public boolean isRightPartial()
	{
		return rightPartial;
	}

	public boolean isLeftPartial()
	{
		return leftPartial;
	}

	public void setFixStartCodonOffset(boolean fixStartCodonOffset) {
		this.fixStartCodonOffset = fixStartCodonOffset;
	}

	public void setNonTranslating(boolean nonTranslating)
	{
		this.nonTranslating = nonTranslating;
	}

	public void setException(boolean exception)
	{
		this.exception = exception;
	}

	/**
	 * If true then a degenerate start codon is translated to M using a
	 * translation exception.
	 */
	public void setFixDegenarateStartCodon(boolean fixDegenerateStartCodon)
	{
		this.fixDegenerateStartCodon = fixDegenerateStartCodon;
	}

	/** If true then a feature with no start codon is made 5' partial. */
	public void setFixMissingStartCodon(boolean fixMissingStartCodon)
	{
		this.fixMissingStartCodon = fixMissingStartCodon;
	}

	/**
	 * If true then 3' partiality is removed when a stop codon is found at the
	 * 3' end.
	 */
	public void setFixRightPartialStopCodon(boolean fixRightPartialStopCodon)
	{
		this.fixRightPartialStopCodon = fixRightPartialStopCodon;
	}

	/**
	 * If true then a partial codon is removed after a stop codon at the 3' end.
	 */
	public void setFixRightPartialCodon(boolean fixRightPartialCodon)
	{
		this.fixRightPartialCodon = fixRightPartialCodon;
	}

	private class TranslationException
	{
		Character aminoAcid;
		Integer beginPosition;
		Integer endPosition;
	}

	public void addTranslationException(Integer beginPosition,
			Integer endPosition, Character aminoAcid)
	{
		TranslationException translationException = new TranslationException();
		translationException.beginPosition = beginPosition;
		translationException.endPosition = endPosition;
		translationException.aminoAcid = aminoAcid;
		translationExceptionMap.put(beginPosition, translationException);
	}

	public void addCodonException(String codon, Character aminoAcid)
	{
		codonTranslator.addCodonException(codon, aminoAcid);
	}

	private String extendCodon(String codon)
	{
		int bases = codon.length();
		for (int i = 0; i < 3 - bases; ++i)
		{
			codon = codon + "n";
		}
		return codon;
	}

	private boolean applyTranslationException(Codon codon)
	{
		TranslationException translationException = translationExceptionMap
				.get(codon.getPos());
		Character aminoAcid = null;
		if (translationException != null)
		{
			aminoAcid = translationException.aminoAcid;
		}
		codon.setTranslationException(aminoAcid != null);
		if (codon.isTranslationException())
		{
			codon.setAminoAcid(aminoAcid);
		}
		return codon.isTranslationException();
	}

	private void translateStartCodon(Codon codon) throws ValidationException
	{
		codonTranslator.translateStartCodon(codon);
		if (!applyTranslationException(codon) && fixDegenerateStartCodon
				&& !leftPartial && !codon.getAminoAcid().equals('M')
				&& codonTranslator.isDegenerateStartCodon(codon))
		{
			codon.setAminoAcid('M');
			codon.setTranslationException(true);
		}
	}

	private void translateOtherCodon(Codon codon) throws ValidationException
	{
		codonTranslator.translateOtherCodon(codon);
		applyTranslationException(codon);
	}

	public void translateCodons(byte[] sequence,
			TranslationResult translationResult) throws ValidationException
	{
		int countX = 0;
		int bases = sequence.length;
		Vector<Codon> codons = new Vector<Codon>(bases / 3);
		// Complete codons.
		int i = codonStart - 1;
		for (; i + 3 <= bases; i += 3)
		{
			Codon codon = new Codon();
			codon.setCodon(new String(Arrays.copyOfRange(sequence, i, i + 3)));
			codon.setPos(i + 1);
			if ((i == codonStart - 1) && !leftPartial)
			{
				translateStartCodon(codon);
			} else
			{
				translateOtherCodon(codon);
			}
			codons.add(codon);
			// Added code to check CDS translations have more than 50% of X
			if (codon.getAminoAcid().equals('X'))
			{
				countX++;
			}
		}
		if (countX > (codons.size() / 2))
		{
			ValidationException.throwError("Translator-20");
		}
		int trailingBases = bases - i;
		if (trailingBases > 0)
		{
			Codon codon = new Codon();
			codon.setCodon(extendCodon(new String(Arrays.copyOfRange(sequence,
					i, sequence.length))));
			codon.setPos(i + 1);
			if ((i == codonStart - 1) && !leftPartial)
			{
				translateStartCodon(codon);
			} else
			{
				translateOtherCodon(codon);
			}

			// Discard partial codon translations X.
			if (!codon.getAminoAcid().equals('X'))
			{
				trailingBases = 0;
				codons.add(codon);
			}

		}
		translationResult.setCodons(codons);
		if (trailingBases > 0)
		{
			translationResult
					.setTrailingBases(new String(Arrays.copyOfRange(sequence,
							sequence.length - trailingBases, sequence.length)));
		} else
		{
			translationResult.setTrailingBases(new String());
		}
	}

	private Integer getEntryTranslationTable(Integer featureTranslationTable,
			TaxonHelper taxHelper, Entry entry,
			ValidationResult validationResult) throws ValidationException
	{
		Integer translationTable = null;

		SourceFeature sourceFeature = entry.getPrimarySourceFeature();
		if (sourceFeature != null)
		{
			Taxon taxon = null;
			if (sourceFeature.getTaxon().getTaxId() != null)
			{
				taxon = taxHelper.getTaxonById(sourceFeature.getTaxon()
						.getTaxId());
			} else if (sourceFeature.getTaxon().getScientificName() != null)
			{
				taxon = taxHelper.getTaxonByScientificName(sourceFeature
						.getTaxon().getScientificName());
			}

			// Classified organism
			if (taxon != null)
			{
				String organelle = sourceFeature
						.getSingleQualifierValue("organelle");
				if (StringUtils.equals(organelle, "mitochondrion")
						|| StringUtils.equals(organelle,
								"mitochondrion:kinetoplast"))
				{
					return taxon.getMitochondrialGeneticCode();
				} else if (StringUtils.contains(organelle, "plastid"))
				{
					return TranslationTable.PLASTID_TRANSLATION_TABLE;
				} else
				{
					return taxon.getGeneticCode();
				}
			} else
			{
				if (featureTranslationTable != null)
					return featureTranslationTable;
				String organelle = sourceFeature
						.getSingleQualifierValue("organelle");
				if (StringUtils.equals(organelle, "mitochondrion"))
				{
					return 2;
				}
				if (StringUtils.equals(organelle, "mitochondrion:kinetoplast"))
				{
					return 4;
				} else if (StringUtils.contains(organelle, "plastid"))
				{
					return TranslationTable.PLASTID_TRANSLATION_TABLE;
				} else
				{
					return TranslationTable.DEFAULT_TRANSLATION_TABLE;
				}

			}
		}

		return translationTable;

	}

	public ExtendedResult<TranslationResult> translate(byte[] sequence,
			Origin origin)
	{

		TranslationResult translationResult = new TranslationResult();
		ExtendedResult<TranslationResult> extendedResult = new ExtendedResult<>(translationResult);

		if (sequence == null)
		{
			try
			{
				ValidationException.throwError("Translator-19");
			} catch (ValidationException ex)
			{
				extendedResult.append(ex.getValidationMessage());
			}
			return extendedResult;
		}
		SequenceFactory sequenceFactory = new SequenceFactory();
		SequenceBasesCheck basesCheck = new SequenceBasesCheck();
		if (origin != null)
		{
			basesCheck.setOrigin(origin);
		}
		extendedResult.append(basesCheck.check(sequenceFactory
				.createSequenceByte(sequence)));
		if (extendedResult.count(Severity.ERROR) > 0)
		{
			return extendedResult;
		}
		try
		{
			validateCodonStart(sequence.length, translationResult);
			sequence = validateTranslationExceptions(sequence);
			validateCodons(sequence.length, translationResult);
			translateCodons(sequence, translationResult);
			if (translationResult.getCodons().size() == 0)
			{
				if (exception || nonTranslating)
				{
					// no conceptual translation
					translationResult.setConceptualTranslationCodons(0);
					return extendedResult;
				} else
				{
					// no translation
					ValidationException.throwError("Translator-1");
				}
			}
			validateTranslation(translationResult);
		} catch (ValidationException ex)
		{
			extendedResult.append(ex.getValidationMessage());
		}
		return extendedResult;
	}

	private void validateCodonStart(int bases, TranslationResult translationResult) throws ValidationException
	{
		if (codonStart < 1 || codonStart > 3)
		{
			ValidationException.throwError("Translator-2", codonStart);
		}
		if (codonStart != 1)
		{
			if (!leftPartial && !nonTranslating)
			{
				if(fixStartCodonOffset) {
					leftPartial = true;
					translationResult.setFixedLeftPartial(true);
					fixes.add("FixMadeLeftPartialCodonStartOffset");
				} else {
					ValidationException.throwError("Translator-3", codonStart);
				}
			}
		}
		if (bases < 3)
		{
			if (codonStart != 1)
			{
				// Currently, all cds features with less than 3 bases have codon
				// start 1. These entries are BN000810, AY950706, AY950707,
				// DQ539670, DQ519928, DQ785858,DQ785863, EF466144, EF466157,
				// EF466202.
				ValidationException.throwError("Translator-4");
			}
		}
	}

	private byte[] validateTranslationExceptions(byte[] sequence)
			throws ValidationException
	{
		int bases = sequence.length;
		Iterator<Integer> itr = translationExceptionMap.keySet().iterator();
		while (itr.hasNext())
		{
			TranslationException translationException = translationExceptionMap
					.get(itr.next());
			int beginPos = translationException.beginPosition;
			int endPos;
			if (translationException.endPosition == null)
			{
				endPos = beginPos;
			} else
			{
				endPos = translationException.endPosition;
			}

			Character aminoAcid = translationException.aminoAcid;
			if (beginPos < codonStart)
			{
				// Translation exception outside frame on the 5' end.
				ValidationException.throwError("Translator-4");
			}
			if (beginPos > bases)
			{
				// Translation exception outside frame on the 3' end.
				ValidationException.throwError("Translator-6");
			}
			if (endPos < beginPos)
			{
				// Invalid translation exception range.
				ValidationException.throwError("Translator-7");
			}
			if (!(endPos == beginPos + 2)
					&& !(endPos == beginPos + 1 && endPos == bases && aminoAcid
							.equals('*'))
					&& !(endPos == beginPos && endPos == bases && aminoAcid
							.equals('*')))
			{
				// Translation exception must span 3 bases or be a partial stop
				// codon at 3' end.
				ValidationException.throwError("Translator-8");
			}
			if (endPos > bases)
			{
				// Translation exception outside frame on the 3' end.
				ValidationException.throwError("Translator-6");
			}
			int translationExceptionCodonStart = beginPos % 3;
			if (translationExceptionCodonStart == 0)
			{
				translationExceptionCodonStart = 3;
			}
			if (translationExceptionCodonStart != codonStart)
			{
				// Translation exception is in different frame.
				ValidationException.throwError("Translator-9", beginPos,
						translationExceptionCodonStart, codonStart);
			}
			// Extend 3' partial stop codon.
			if (endPos == beginPos + 1 && aminoAcid.equals('*'))
			{
				sequence = Arrays.copyOf(sequence, bases + 1);
				sequence[bases] = 'n';
			} else if (endPos == beginPos && aminoAcid.equals('*'))
			{
				sequence = Arrays.copyOf(sequence, bases + 2);
				sequence[bases] = 'n';
				sequence[bases + 1] = 'n';
			}
		}
		return sequence;
	}

	private void validateCodons(int bases, TranslationResult translationResult)
			throws ValidationException
	{
		if (bases < 3)
		{
			// All cds features with 1 base are 3' partial and nonTranslating.
			// There is only one such entry: BN000810.
			// All cds features with 2 bases are 3' partial and
			// translate to 'M'. These entries are: AY950706, AY950707,
			// DQ539670, DQ519928, DQ785858,DQ785863, EF466144, EF466157,
			// EF466202.
			translationResult.setTranslationBaseCount(bases);
			if (!leftPartial && !rightPartial)
			{
				// CDS features with less than 3 bases must be 3' or 5' partial.
				ValidationException.throwError("Translator-10");
			}
		} else if ((bases - codonStart + 1) % 3 != 0)
		{
			int length = bases - codonStart + 1;
			translationResult.setTranslationLength(length);

			// The current implementation allows 3' partial non-
			// translated codons after the stop codon if the
			// feature is 5'partial. An example CDS entry is AAA67861.
			// non mod 3 peptide features other than Cds dont throw errors as
			// there are more complex checks in
			// PeptideFeatureCheck
			if (!peptideFeature && !leftPartial && !rightPartial
					&& !nonTranslating && !exception)
			{
				if(fixMultipleOfThree){
					leftPartial = true;
					rightPartial = true;
					translationResult.setFixedRightPartial(true);
					translationResult.setFixedLeftPartial(true);
					fixes.add("FixNonMultipleOfThree");
				} else {
					// CDS feature length must be a multiple of 3. Consider 5' or 3'
					// partial location.
					ValidationException.throwError("Translator-11");
				}
			}
		}
	}

	private void validateTranslation(TranslationResult translationResult)
			throws ValidationException
	{
		int trailingStopCodons = 0;
		int internalStopCodons = 0;
		Vector<Codon> codons = translationResult.getCodons();
		int i = codons.size();
		// Count the number of trailing stop codons.
		while (i > 0 && codons.get(i - 1).getAminoAcid().equals('*'))
		{
			--i;
			++trailingStopCodons;
		}
		int conceptualTranslationCodons = codons.size() - trailingStopCodons;
		translationResult
				.setConceptualTranslationCodons(conceptualTranslationCodons);
		if (conceptualTranslationCodons == 0)
		{
			validateStopCodonOnly(translationResult);
			validateTrailingStopCodons(trailingStopCodons, translationResult);
		} else
		{
			// Count the number of internal stop codons.
			while (i > 0)
			{
				if (codons.get(i - 1).getAminoAcid().equals('*'))
				{
					++internalStopCodons;
				}
				--i;
			}
			boolean conceptualTranslation = true;
			if (!validateInternalStopCodons(internalStopCodons, translationResult))
			{
				conceptualTranslation = false;
			}
			if (!validateStartCodon(translationResult))
			{
				conceptualTranslation = false;
			}
			if (!validateTrailingStopCodons(trailingStopCodons,
					translationResult))
			{
				conceptualTranslation = false;
			}
			if (!conceptualTranslation)
			{
				translationResult.setConceptualTranslationCodons(0);
			}
		}
	}

	private void validateStopCodonOnly(TranslationResult translationResult)
			throws ValidationException
	{
		if (exception || nonTranslating)
		{
			return; // no conceptual translation
		}
		if (!(translationResult.getCodons().size() == 1
				&& translationResult.getTrailingBases().length() == 0
				&& leftPartial))
		{
			// CDS feature can have a single stop codon only
			// if it has 3 bases and is 5' partial.
			ValidationException.throwError("Translator-12");
		}
	}

	private boolean validateTrailingStopCodons(int trailingStopCodons,
			TranslationResult translationResult) throws ValidationException
	{
		if (!exception)
		{
			if (trailingStopCodons > 1)
			{
				if (nonTranslating)
				{
					return false; // no conceptual translation
				} else
				{
					// More than one stop codon at the 3' end.
					ValidationException.throwError("Translator-13");
				}
			}
			if (trailingStopCodons == 1 && rightPartial)
			{
				if (nonTranslating)
				{
					return false; // no conceptual translation
				} else
				{
					if (fixRightPartialStopCodon)
					{
						translationResult.setFixedRightPartial(true);
						rightPartial = false;
						fixes.add("FixRemovedThreePartial");
					} else
					{
						// Stop codon found at 3' partial end.
						//Translator-14=Stop codon found at 3' partial end of the protein coding feature translation. Consider removing 3' partial location.
						ValidationException.throwError("Translator-14");
					}
				}
			}
			if (trailingStopCodons == 0 && !rightPartial)
			{
				if (nonTranslating)
				{
					return false; // no conceptual translation
				} else if (!peptideFeature)
				{// peptide features are allowed to not have stop codons
					// No stop codon at the 3' end.
					if(fixRightPartialCodon) {
						rightPartial = true;
						translationResult.setFixedRightPartial(true);
						fixes.add("FixMadeThreePartialNoStopCodon");
					} else {
						ValidationException.throwError("Translator-15");
					}
				}
			}
			if (trailingStopCodons == 1
					&& translationResult.getTrailingBases().length() > 0)
			{
				if (nonTranslating)
				{
					return false; // no conceptual translation
				} else
				{
					if (fixRightPartialCodon)
					{
						translationResult.setFixedRightPartial(true);
						rightPartial = true;
						fixes.add("FixMadeThreePartialBasesAfterStop");
					} else
					{
						// A partial codon appears after the stop codon.
						ValidationException.throwError("Translator-16");
					}
				}
			}
		}
		return true;
	}

	private boolean validateInternalStopCodons(int internalStopCodons, TranslationResult translationResult)
			throws ValidationException
	{
		if (internalStopCodons > 0)
		{
			if (exception || nonTranslating)
			{
				return false; // no conceptual translation
			} else
			{
				if(fixInternalStopCodon) {
					translationResult.setFixedPseudo(true);
					nonTranslating = true;
					fixes.add("FixMadePseudo");
					return false;
				} else {
					// The protein translation contains internal stop condons.
					ValidationException.throwError("Translator-17");
				}
			}
		}
		return true;
	}

	private boolean validateStartCodon(TranslationResult translationResult)
			throws ValidationException
	{
		if (!leftPartial && !exception && !peptideFeature)
		{
			if (!translationResult.getCodons().get(0).getAminoAcid()
					.equals('M')
					&& !leftPartial)
			{
				if (nonTranslating)
				{
					return false; // no conceptual translation
				} else
				{
					if (fixMissingStartCodon)
					{
						translationResult.setFixedLeftPartial(true);
						leftPartial = true;
						fixes.add("FixMadeFivePartialMissingStart");
					} else
					{
						// The protein translation does not start with a
						// methionine.
						ValidationException.throwError("Translator-18");
					}
				}
			}
		}
		return true;
	}

	public boolean equalsTranslation(String expectedTranslation,
			String conceptualTranslation)
	{
		if (expectedTranslation.length() < conceptualTranslation.length())
		{
			return false;
		}
		for (int i = 0; i < conceptualTranslation.length(); i++)
		{
			if (expectedTranslation.charAt(i) != conceptualTranslation
					.charAt(i) && expectedTranslation.charAt(i) != 'X')
			{
				return false;
			}
		}
		// Ignore trailing X.
		if (expectedTranslation.length() > conceptualTranslation.length())
		{
			for (int i = conceptualTranslation.length(); i < expectedTranslation
					.length(); i++)
			{
				if (expectedTranslation.charAt(i) != 'X')
				{
					return false;
				}
			}
		}
		return true;
	}

}
