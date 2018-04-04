package uk.ac.ebi.embl.flatfile.reader.genomeassembly;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.embl.api.entry.genomeassembly.AssemblyInfoEntry;
import uk.ac.ebi.embl.api.validation.ValidationResult;

public class AssemblyInfoReader extends GCSEntryReader
{
	private final String MESSAGE_KEY_INVALID_FORMAT_ERROR = "invalidlineFormat";
	private final String MESSAGE_KEY_INVALID_VALUE_ERROR = "invalidfieldValue";
	private final String MESSAGE_KEY_INVALID_FIELD_ERROR = "invalidfieldName";

	AssemblyInfoEntry assemblyInfoEntry = null;
	public AssemblyInfoReader(File file)
	{
		super();
		this.file=file;
	}
	@Override
	public ValidationResult read() throws NumberFormatException, IOException
	{
		assemblyInfoEntry= new AssemblyInfoEntry();
		int lineNumber = 1;
		try(BufferedReader reader = getBufferedReader(file))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				line = line.trim();
				if (line.isEmpty()) // Skip empty lines
				{
					continue;
				}
				
				String[] fields = line.trim().split("\\s+",2);
				int numberOfColumns = fields.length;
				if (numberOfColumns != 2 )
				{
					error(lineNumber,MESSAGE_KEY_INVALID_FORMAT_ERROR,line);
				}
				else
				{
					String field= StringUtils.deleteWhitespace(fields[0].toUpperCase());
					field= field.replaceAll("_","").replaceAll("-","").replaceAll("\\.","");			
					String fieldValue= fields[1];
					switch(field)
					{
					case "ASSEMBLYNAME":
						assemblyInfoEntry.setName(fieldValue);
						break;
					case "COVERAGE":
						assemblyInfoEntry.setCoverage(fieldValue);
						break;
					case "PROGRAM":
						assemblyInfoEntry.setProgram(fieldValue);
						break;
					case "PLATFORM":
						assemblyInfoEntry.setPlatform(fieldValue);
						break;
					case "MINGAPLENGTH":
						if(isInteger(fieldValue))
						  assemblyInfoEntry.setMinGapLength(new Integer(fieldValue));
						else
						  error(lineNumber,MESSAGE_KEY_INVALID_VALUE_ERROR,fields[0],fieldValue);
						break;
					case "MOLECULETYPE":
						if(isValidMoltype(fieldValue))
							assemblyInfoEntry.setMoleculeType(fieldValue);
						else
						    error(lineNumber,MESSAGE_KEY_INVALID_VALUE_ERROR,fields[0],fieldValue);
						break;
					case "SAMPLE":
						 assemblyInfoEntry.setSampleId(fieldValue);
						 break;
					case "STUDY":
					  	 assemblyInfoEntry.setStudyId(fieldValue);
					  	 break;
					default :
						error(lineNumber,MESSAGE_KEY_INVALID_FIELD_ERROR,line);
						break;
					}
					
				}
				lineNumber++;

			}
		}
		return validationResult;
	}

	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}

	public static boolean isValidMoltype(String molType) {
		
		if (molType == null) {
			return false;
		}

		molType = StringUtils.deleteWhitespace(molType).toUpperCase();
		if (!molType.equals("GENOMICDNA") && !molType.equals("GENOMICRNA") && !molType.equals("VIRALCRNA"))
			return false;

		return true;

	}

	@Override
	public ValidationResult skip() throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getEntry()
	{
		return assemblyInfoEntry;
	}

	@Override
	public boolean isEntry()
	{
		return validationResult.isValid();
	}

}
