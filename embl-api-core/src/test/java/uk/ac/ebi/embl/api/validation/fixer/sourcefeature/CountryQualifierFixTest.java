package uk.ac.ebi.embl.api.validation.fixer.sourcefeature;

import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.embl.api.entry.feature.Feature;
import uk.ac.ebi.embl.api.entry.feature.FeatureFactory;
import uk.ac.ebi.embl.api.entry.qualifier.Qualifier;
import uk.ac.ebi.embl.api.storage.DataRow;
import uk.ac.ebi.embl.api.storage.DataSet;
import uk.ac.ebi.embl.api.validation.Severity;
import uk.ac.ebi.embl.api.validation.ValidationMessageManager;
import uk.ac.ebi.embl.api.validation.ValidationResult;

import static org.junit.Assert.*;

public class CountryQualifierFixTest {

    private CountryQualifierFix check;
    public FeatureFactory featureFactory;

    @Before
    public void setUp()
    {
        ValidationMessageManager.addBundle(ValidationMessageManager.STANDARD_FIXER_BUNDLE);

        featureFactory = new FeatureFactory();

        DataRow feature_regex_groups_row1 = new DataRow(
                "country",
                "1",
                "FALSE",
                "Afghanistan,Albania,Algeria,American Samoa,Andorra,Angola,Anguilla,Antarctica,Antigua and Barbuda,Arctic Ocean,Argentina,Armenia,Aruba,Ashmore and Cartier Islands,Atlantic Ocean,Australia,Austria,Azerbaijan,Bahamas,Bahrain,Baker Island,Bangladesh,Barbados,Bassas da India,Belarus,Belgium,Belgian Congo,Belize,Benin,Bermuda,Bhutan,Bolivia,Bosnia and Herzegovina,Borneo,Botswana,Bouvet Island,Brazil,British Virgin Islands,British Guiana,Brunei,Bulgaria,Burkina Faso,Burma,Burundi,Cambodia,Cameroon,Canada,Cape Verde,Cayman Islands,Central African Republic,Chad,Chile,China,Christmas Island,Clipperton Island,Cocos Islands,Colombia,Comoros,Cook Islands,Coral Sea Islands,Costa Rica,Cote d'Ivoire,Croatia,Cuba,Cyprus,Czech Republic,Czechoslovakia,Democratic Republic of the Congo,Denmark,Djibouti,Dominica,Dominican Republic,East Timor,Ecuador,Egypt,El Salvador,Equatorial Guinea,Eritrea,Estonia,Ethiopia,Europa Island,Falkland Islands (Islas Malvinas),Faroe Islands,Fiji,Finland,Former Yugoslav Republic of Macedonia,France,French Guiana,French Polynesia,French Southern and Antarctic Lands,Gabon,Gambia,Gaza Strip,Georgia,Germany,Ghana,Gibraltar,Glorioso Islands,Greece,Greenland,Grenada,Guadeloupe,Guam,Guatemala,Guernsey,Guinea,Guinea-Bissau,Guyana,Haiti,Heard Island and McDonald Islands,Honduras,Hong Kong,Howland Island,Hungary,Iceland,India,Indian Ocean,Indonesia,Iran,Iraq,Ireland,Isle of Man,Israel,Italy,Jamaica,Jan Mayen,Japan,Jarvis Island,Jersey,Johnston Atoll,Jordan,Juan de Nova Island,Kazakhstan,Kenya,Kerguelen Archipelago,Kingman Reef,Kiribati,Kosovo,Kuwait,Kyrgyzstan,Laos,Latvia,Lebanon,Lesotho,Liberia,Libya,Liechtenstein,Lithuania,Luxembourg,Macau,Macedonia,Madagascar,Malawi,Malaysia,Maldives,Mali,Malta,Marshall Islands,Martinique,Mauritania,Mauritius,Mayotte,Mediterranean Sea,Mexico,Micronesia,Midway Islands,Moldova,Monaco,Mongolia,Montenegro,Montserrat,Morocco,Mozambique,Myanmar,Namibia,Nauru,Navassa Island,Nepal,Netherlands,Netherlands Antilles,New Caledonia,New Zealand,Nicaragua,Niger,Nigeria,Niue,Norfolk Island,North Korea,Northern Mariana Islands,North Sea,Norway,Oman,Pacific Ocean,Pakistan,Palau,Palmyra Atoll,Panama,Papua New Guinea,Paracel Islands,Paraguay,Peru,Philippines,Pitcairn Islands,Poland,Portugal,Puerto Rico,Qatar,Republic of the Congo,Reunion,Romania,Ross Sea,Russia,Rwanda,Saint Helena,Saint Kitts and Nevis,Saint Lucia,Saint Pierre and Miquelon,Saint Vincent and the Grenadines,Samoa,San Marino,Sao Tome and Principe,Saudi Arabia,Senegal,Serbia,Serbia and Montenegro,Seychelles,Siam,Sierra Leone,Singapore,Slovakia,Slovenia,Solomon Islands,Somalia,South Africa,South Georgia and the South Sandwich Islands,South Korea,Southern Ocean,Spain,Spratly Islands,Sri Lanka,Sudan,Suriname,Svalbard,Swaziland,Sweden,Switzerland,Syria,Taiwan,Tajikistan,Tanzania,Tasman Sea,Thailand,Togo,Tokelau,Tonga,Trinidad and Tobago,Tromelin Island,Tunisia,Turkey,Turkmenistan,Turks and Caicos Islands,Tuvalu,USA,USSR,Uganda,Ukraine,United Arab Emirates,United Kingdom,Uruguay,Uzbekistan,Vanuatu,Venezuela,Viet Nam,Virgin Islands,Wake Island,Wallis and Futuna,West Bank,Western Sahara,Yemen,Yugoslavia,Zaire,Zambia,Zimbabwe,Sint Maarten,Curacao,Line Islands,South Sudan");
        DataRow feature_regex_groups_row2 = new DataRow(
                "satellite",
                "1",
                "TRUE",
                "microsatellite,minisatellite,satellite");
        DataSet feature_regex_groups_set = new DataSet();

        feature_regex_groups_set.addRow(feature_regex_groups_row1);
        feature_regex_groups_set.addRow(feature_regex_groups_row2);
        check = new CountryQualifierFix(feature_regex_groups_set);
        check.setPopulated();
    }

    @Test
    public void testCheck_NoFeature()
    {
        assertTrue(check.check(null).isValid());
    }

    @Test
    public void testCheck_noQualifiers()
    {
        assertTrue(check.check(featureFactory.createFeature("source")).getMessages().size() == 0);
    }

    @Test
    public void testCheckNoCountryQualifier()
    {
        Feature feature = featureFactory.createFeature("source");
        feature.addQualifier(Qualifier.ORGANISM_QUALIFIER_NAME);
        assertTrue(check.check(feature).getMessages().size() == 0);
    }

    @Test
    public void testCheckCountryQualifierWithValidValue()
    {
        Feature feature = featureFactory.createFeature("source");
        feature.addQualifier(Qualifier.COUNTRY_QUALIFIER_NAME, "Brazil");
        assertEquals(1, feature.getQualifiers(Qualifier.COUNTRY_QUALIFIER_NAME).size());
        assertEquals(0, check.check(feature).getMessages().size());
    }

    @Test
    public void testCheckCountryQualifierWithInvalidValue()
    {
        Feature feature = featureFactory.createFeature("source");
        feature.addQualifier(Qualifier.COUNTRY_QUALIFIER_NAME, "Switz");
        assertEquals(0, feature.getQualifiers(Qualifier.NOTE_QUALIFIER_NAME).size());
        ValidationResult result = check.check(feature);
        //note has been added
        assertEquals(1, feature.getQualifiers(Qualifier.NOTE_QUALIFIER_NAME).size());
        assertEquals("Switz", feature.getQualifiers(Qualifier.NOTE_QUALIFIER_NAME).get(0).getValue());
        assertEquals(1, result.getMessages().size());
        //Country has been removed
        assertEquals(1, result.count("CountryQualifierFix_1", Severity.FIX));
        assertEquals(0, feature.getQualifiers(Qualifier.COUNTRY_QUALIFIER_NAME).size());
    }

}