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
package uk.ac.ebi.embl.api.validation.check.feature;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import uk.ac.ebi.embl.api.entry.feature.Feature;
import uk.ac.ebi.embl.api.entry.qualifier.Qualifier;
import uk.ac.ebi.embl.api.validation.ValidationResult;
import uk.ac.ebi.embl.api.validation.ValidationScope;
import uk.ac.ebi.embl.api.validation.annotation.Description;
import uk.ac.ebi.embl.api.validation.annotation.ExcludeScope;

@Description("Feature qualifier \"{0}\" has invalid date i.e.{1}" +
		"Feature Qualifier \"{0}\" value must not be future date i.e {1}.")
@ExcludeScope(validationScope = {ValidationScope.NCBI, ValidationScope.NCBI_MASTER})
public class CollectionDateQualifierCheck extends FeatureValidationCheck 
{

	private final static String DATE_FORMAT_ERROR = "CollectionDateQualifierCheck_1";
	private final static String FUTURE_DATE_ERROR = "CollectionDateQualifierCheck_2";

	private final static Pattern INSDC_DATE_FORMAT_PATTERN_1 = Pattern.compile("^(\\d{2})-((Jan)|(Feb)|(Mar)|(Apr)|(May)|(Jun)|(Jul)|(Aug)|(Sep)|(Oct)|(Nov)|(Dec))-(\\d{4})$"); // "DD-Mmm-YYYY"
	private final static Pattern INSDC_DATE_FORMAT_PATTERN_2 = Pattern.compile("^((Jan)|(Feb)|(Mar)|(Apr)|(May)|(Jun)|(Jul)|(Aug)|(Sep)|(Oct)|(Nov)|(Dec))-(\\d{4})$"); // "Mmm-YYYY"

	private final static Pattern ISO_DATE_FORMAT_PATTERN_1 = Pattern.compile("^(\\d{4})$"); // YYYY
	private final static Pattern ISO_DATE_FORMAT_PATTERN_2 = Pattern.compile("^(\\d{4})-(\\d{2})$"); // YYYY-MM
	private final static Pattern ISO_DATE_FORMAT_PATTERN_3 = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})$"); // YYYY-MM-DD
	private final static Pattern ISO_DATE_FORMAT_PATTERN_4 = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2})Z$"); // YYYY-MM-DDThhZ
	private final static Pattern ISO_DATE_FORMAT_PATTERN_5 = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2})Z$"); // YYYY-MM-DDThh:mmZ
	private final static Pattern ISO_DATE_FORMAT_PATTERN_6 = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})Z$"); // YYYY-MM-DDThh:mm:ssZ

	
	private final static SimpleDateFormat INSDC_DATE_FORMAT_1 = new SimpleDateFormat("dd-MMM-yyyy"); // dd-MMM-yyyy
	private final static SimpleDateFormat INSDC_DATE_FORMAT_2 = new SimpleDateFormat("MMM-yyyy"); // MMM-yyyy

	private final static SimpleDateFormat ISO_DATE_FORMAT_1 = new SimpleDateFormat("yyyy"); // yyyy
	private final static SimpleDateFormat ISO_DATE_FORMAT_2 = new SimpleDateFormat("yyyy-MM"); // yyyy-MM
	private final static SimpleDateFormat ISO_DATE_FORMAT_3 = new SimpleDateFormat("yyyy-MM-dd"); // yyyy-MM-dd
	private final static SimpleDateFormat ISO_DATE_FORMAT_4 = new SimpleDateFormat("yyyy-MM-dd'T'HH'Z'"); // yyyy-MM-ddThhZ
	private final static SimpleDateFormat ISO_DATE_FORMAT_5 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // yyyy-MM-ddThh:mmZ
	private final static SimpleDateFormat ISO_DATE_FORMAT_6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // yyyy-MM-ddThh:mmZ

	private final static Pattern DATE_RANGE_PATTERN = Pattern.compile("^([^/]+)/([^/]+)$");
	
	private final static Date currentDate = Calendar.getInstance().getTime();
	
	public ValidationResult check(Feature feature) 
	{
		result = new ValidationResult();
		if(feature==null)
		{
			return result;
		}
		
		List<Qualifier> collectionDateQualifiers = feature
				.getQualifiers(Qualifier.COLLECTION_DATE_QUALIFIER_NAME);

		if (collectionDateQualifiers.size() == 0) 
		{
			return result;
		}

		for (Qualifier collectionQualifier : collectionDateQualifiers) 
		{
			String value = collectionQualifier.getValue();
			if (isValueEmpty(value)) {
				reportError(collectionQualifier.getOrigin(),
						DATE_FORMAT_ERROR, collectionQualifier.getName(), "");
			}

			value = StringUtils.deleteWhitespace(value);
			collectionQualifier.setValue(value);
			try {
				if (!isValidDate(value)) {
					reportError(collectionQualifier.getOrigin(), DATE_FORMAT_ERROR, collectionQualifier.getName(), value);
				}
			} catch (IllegalArgumentException e) {
				reportError(collectionQualifier.getOrigin(), FUTURE_DATE_ERROR, collectionQualifier.getName(), collectionQualifier.getValue());
			}
		}
		return result;
	}

	public boolean isValid(final String value) {
		try {
			return !isValueEmpty(value) && isValidDate(StringUtils.deleteWhitespace(value));
		} catch (Exception e) {
			return false;
		}
	}

	private boolean isValueEmpty(final String value) {
		return value == null || value.isEmpty() || StringUtils.deleteWhitespace(value).isEmpty();
	}

	private boolean isValidDate(String value) {

		Matcher m = DATE_RANGE_PATTERN.matcher(value);

		if (m.matches()) {
			CheckDateResult fromResult = checkDate(m.group(1));
			CheckDateResult toResult = checkDate(m.group(2));

			if (fromResult == null || toResult == null) {
				return false; // invalid date format
			} else {
				if (isFutureDate(fromResult.date) || isFutureDate(toResult.date)) { //from_date or to_date after current date
					throw new IllegalArgumentException();
				}
				if (fromResult.pattern != toResult.pattern) { // Different range date formats.
					return false;
				}
				// From date in a range is larger than to date.
				return fromResult.date.compareTo(toResult.date) <= 0;
			}
		} else {
			CheckDateResult dateResult = checkDate(value);
			if (dateResult == null ) {
				return false;// invalid date format
			} else if(isFutureDate(dateResult.date)) {
				throw new IllegalArgumentException();
			}
		}
		return true;
	}

	private class CheckDateResult
	{
		public CheckDateResult( Pattern pattern, Date date) {
			this.pattern = pattern;
			this.date = date;
		}
		
		public Pattern pattern;
		public Date date;
	}

	private CheckDateResult checkDate( String value)
	{
		try {
			Date collectionDate;
			if (INSDC_DATE_FORMAT_PATTERN_1.matcher(value).matches() && (collectionDate = INSDC_DATE_FORMAT_1.parse(value)) != null)
			{
				return new CheckDateResult(INSDC_DATE_FORMAT_PATTERN_1, collectionDate);
			}
			else if (INSDC_DATE_FORMAT_PATTERN_2.matcher(value).matches() && (collectionDate = INSDC_DATE_FORMAT_2.parse(value)) != null)
			{
				return new CheckDateResult(INSDC_DATE_FORMAT_PATTERN_2, collectionDate);
			}
			else if (ISO_DATE_FORMAT_PATTERN_1.matcher(value).matches() && (collectionDate = ISO_DATE_FORMAT_1.parse(value)) != null)
			{
				return new CheckDateResult(ISO_DATE_FORMAT_PATTERN_1, collectionDate);
			}
			else if (ISO_DATE_FORMAT_PATTERN_2.matcher(value).matches() && (collectionDate = ISO_DATE_FORMAT_2.parse(value)) != null)
			{
				return new CheckDateResult(ISO_DATE_FORMAT_PATTERN_2, collectionDate);
			}
			else if (ISO_DATE_FORMAT_PATTERN_3.matcher(value).matches() && (collectionDate = ISO_DATE_FORMAT_3.parse(value)) != null)
			{
				return new CheckDateResult(ISO_DATE_FORMAT_PATTERN_3, collectionDate);
			}
			else if (ISO_DATE_FORMAT_PATTERN_4.matcher(value).matches() && (collectionDate = ISO_DATE_FORMAT_4.parse(value)) != null)
			{
				return new CheckDateResult(ISO_DATE_FORMAT_PATTERN_4, collectionDate);
			}
			else if (ISO_DATE_FORMAT_PATTERN_5.matcher(value).matches() && (collectionDate = ISO_DATE_FORMAT_5.parse(value)) != null)
			{
				return new CheckDateResult(ISO_DATE_FORMAT_PATTERN_5, collectionDate);
			}
			else if (ISO_DATE_FORMAT_PATTERN_6.matcher(value).matches() && (collectionDate = ISO_DATE_FORMAT_6.parse(value)) != null)
			{
				return new CheckDateResult(ISO_DATE_FORMAT_PATTERN_6, collectionDate);
			}
		} 
		catch (ParseException ignored)
		{
		}
		return null;
	}

	private boolean isFutureDate(Date collectionDate) {
		return collectionDate.compareTo(currentDate) > 0;
	}
}
