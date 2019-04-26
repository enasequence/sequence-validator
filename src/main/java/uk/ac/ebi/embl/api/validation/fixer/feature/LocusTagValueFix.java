package uk.ac.ebi.embl.api.validation.fixer.feature;

import java.util.List;

import uk.ac.ebi.embl.api.entry.feature.Feature;
import uk.ac.ebi.embl.api.entry.qualifier.Qualifier;
import uk.ac.ebi.embl.api.validation.Severity;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.embl.api.validation.ValidationScope;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.annotation.ExcludeScope;
import uk.ac.ebi.embl.api.validation.check.feature.FeatureValidationCheck;
import uk.ac.ebi.embl.api.validation.helper.Utils;

@Description("\"locus_tag\" qualifier value has been changed from \"{0}\" to \"{1}\"")
@ExcludeScope(validationScope = {ValidationScope.NCBI})
public class LocusTagValueFix extends FeatureValidationCheck
{
	private static final String LocusTagPrefixFix_ID = "LocusTagValueFix_1";
	
	public LocusTagValueFix()
	{
	}
	
	public ValidationResult check(Feature feature)
	{
		result = new ValidationResult();
		if (feature == null)
		{
			return result;
		}
		
		List<Qualifier> locusTagQualifiers = feature.getQualifiers(Qualifier.LOCUS_TAG_QUALIFIER_NAME);
		if (locusTagQualifiers == null)
			return result;
		
		for(Qualifier qualifier:locusTagQualifiers)
		{
			String locusTagValue=qualifier.getValue();
			if(locusTagValue != null && !Utils.isAllUpperCase(locusTagValue)) {
				qualifier.setValue(locusTagValue.toUpperCase());
				reportMessage(Severity.FIX, feature.getOrigin(), LocusTagPrefixFix_ID, locusTagValue, qualifier.getValue());
			}
		}
		
		return result;
		
	}
}
