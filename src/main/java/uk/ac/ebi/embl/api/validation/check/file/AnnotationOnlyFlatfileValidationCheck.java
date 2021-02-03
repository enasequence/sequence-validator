package uk.ac.ebi.embl.api.validation.check.file;

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.sequence.Sequence;
import uk.ac.ebi.embl.api.entry.sequence.SequenceFactory;
import uk.ac.ebi.embl.api.validation.ValidationEngineException;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.embl.api.validation.fixer.entry.EntryNameFix;
import uk.ac.ebi.embl.api.validation.plan.EmblEntryValidationPlan;
import uk.ac.ebi.embl.api.validation.submission.Context;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile;
import uk.ac.ebi.embl.api.validation.submission.SubmissionOptions;
import uk.ac.ebi.embl.flatfile.reader.EntryReader;
import uk.ac.ebi.embl.flatfile.reader.embl.EmblEntryReader;
import uk.ac.ebi.embl.flatfile.reader.embl.EmblEntryReader.Format;
import uk.ac.ebi.embl.flatfile.reader.genbank.GenbankEntryReader;
import uk.ac.ebi.embl.flatfile.writer.embl.EmblEntryWriter;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentMap;

public class AnnotationOnlyFlatfileValidationCheck extends FileValidationCheck 
{
	public AnnotationOnlyFlatfileValidationCheck(SubmissionOptions options) 
	{
		super(options);
	}

	@Override
	public ValidationResult check(SubmissionFile submissionFile) throws ValidationEngineException
	{
		ValidationResult validationResult = new ValidationResult();
		EmblEntryValidationPlan validationPlan;
		try(BufferedReader fileReader= getBufferedReader(submissionFile.getFile());PrintWriter fixedFileWriter=getFixedFileWriter(submissionFile))
		{
			clearReportFile(getReportFile(submissionFile));
			boolean isGenbankFile = isGenbank(submissionFile.getFile());

			if(!isGenbankFile && !validateFileFormat(submissionFile.getFile(), uk.ac.ebi.embl.api.validation.submission.SubmissionFile.FileType.ANNOTATION_ONLY_FLATFILE))
			{
				addErrorAndReport(validationResult,submissionFile, "InvalidFileFormat","flatfile");
				return validationResult;
			}
			Format format = options.context.get()==Context.genome?Format.ASSEMBLY_FILE_FORMAT:Format.EMBL_FORMAT;
			EntryReader entryReader = isGenbankFile?new GenbankEntryReader(fileReader):
					new EmblEntryReader(fileReader,format,submissionFile.getFile().getName());
			ValidationResult parseResult = entryReader.read();
			validationResult.append(parseResult);
			while(entryReader.isEntry())
			{
				if(!parseResult.isValid())
				{
					getReporter().writeToFile(getReportFile(submissionFile), parseResult);
					addMessageKeys(parseResult.getMessages());
				}

				Entry entry = entryReader.getEntry();
				entry.setSubmitterAccession(EntryNameFix.getFixedEntryName(entry.getSubmitterAccession()));
				entry.setDataClass(getDataclass(entry.getSubmitterAccession()));
				if(entry.getSubmitterAccession()!=null&&getSequenceDB()!=null)
				{
					ConcurrentMap map = getSequenceDB().hashMap("map").createOrOpen();
					if(map.get(entry.getSubmitterAccession().toUpperCase())!=null)
					{
						if(entry.getSequence()==null)
						{
							SequenceFactory sequenceFactory =new SequenceFactory();
							entry.setSequence(sequenceFactory.createSequence());
						}
						entry.getSequence().setSequence(ByteBuffer.wrap(((String)map.get(entry.getSubmitterAccession().toUpperCase())).getBytes()));

					}
				}

    			getOptions().getEntryValidationPlanProperty().validationScope.set(getValidationScope(entry.getSubmitterAccession()));

				Sequence.Topology chrListToplogy = getTopology(entry.getSubmitterAccession());
				if (chrListToplogy != null) {
					if (entry.getSequence().getTopology() != null
							&& entry.getSequence().getTopology() != chrListToplogy) {
						addErrorAndReport(validationResult, submissionFile, "TopologyMismatch",
								entry.getSequence().getTopology().name(), chrListToplogy.name());
						return validationResult;
					}
					entry.getSequence().setTopology(chrListToplogy);
				}

				getOptions().getEntryValidationPlanProperty().fileType.set(uk.ac.ebi.embl.api.validation.FileType.EMBL);
				validationPlan=new EmblEntryValidationPlan(getOptions().getEntryValidationPlanProperty());
				appendHeader(entry);
				ValidationResult planResult=validationPlan.execute(entry);
				validationResult.append(planResult);
				if(!planResult.isValid())
				{
					getReporter().writeToFile(getReportFile(submissionFile), planResult);
					addMessageKeys(planResult.getMessages());
				}
				else
				{
					if(fixedFileWriter!=null)
						new EmblEntryWriter(entry).write(fixedFileWriter);
				}
				parseResult = entryReader.read();
				validationResult.append(parseResult);
			}

		} catch(ValidationEngineException vee) {
			if(getSequenceDB()!=null)
				getSequenceDB().close();
			throw vee;
		}
		catch(Exception e)
		{
			closeDB(getSequenceDB());
			throw new ValidationEngineException(e.getMessage(), e);
		}
		return validationResult;
	}

	@Override
	public ValidationResult check() throws ValidationEngineException
	{
		throw new UnsupportedOperationException();
	}

}
