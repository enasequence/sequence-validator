/*
 * Copyright 2018 EMBL - European Bioinformatics Institute
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package uk.ac.ebi.embl.api.validation.dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import uk.ac.ebi.embl.api.entry.ContigSequenceInfo;
import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.feature.SourceFeature;
import uk.ac.ebi.embl.api.entry.qualifier.Qualifier;
import uk.ac.ebi.embl.api.validation.cvtable.cv_fqual_value_fix_table;

public interface EntryDAOUtils
{
	public String getPrimaryAcc(String analysisId, 
			                    String objectName,
			                    int assemblyLevel) throws SQLException;

	public String getDataclass(String analysisId, 
			                   String objectName,
			                   int assemblyLevel) throws SQLException;

	public byte[] getSequence(String primaryAcc)throws SQLException,IOException;
	
	public byte[] getSubSequence(String accession,Long beginPosition,Long length) throws SQLException, IOException;
	

	public boolean isValueExists(String tableName, String constraintKey,
			String constraintValue) throws SQLException;

	public cv_fqual_value_fix_table get_cv_fqual_value_fix() throws SQLException;

	boolean isEntryExists(String accession) throws SQLException;

	Long getSequenceLength(String accession) throws SQLException;

	ContigSequenceInfo getSequenceInfoBasedOnEntryName(String entry_name,
												String analysisID,
												int assemblyLevel)	throws SQLException;

	ArrayList<Qualifier> getChromosomeQualifiers(String analysisId,
			String submitterAccession,SourceFeature source) throws SQLException;

	public boolean isAssemblyLevelExists(String analysisId,int assembly_level) throws SQLException;
	
	public boolean isAssemblyEntryUpdate(String analysisId,String entry_name,int assembly_level) throws SQLException;
	
	public boolean isAssemblyLevelObjectNameExists(String analysis_id,String entry_name,int assemblyLevel) throws SQLException;
	
	public boolean isProjectValid(String project) throws SQLException;
	
	public HashSet<String> getProjectLocutagPrefix(String project)throws SQLException;
	
	Entry getMasterEntry(String analysisId) throws SQLException;

	public String isEcnumberValid(String ecNumber) throws SQLException;

	Entry getEntryInfo(String primaryAcc) throws SQLException;
	
	public String getAccessionDataclass(String prefix) throws SQLException;
	
	public String getDbcode(String prefix) throws SQLException;
	
	public String getAssemblyEntryAccession(String remoteAccession,String assemblyId) throws SQLException;
	
	public boolean isAssemblyUpdateSupported(String analysisId) throws SQLException;
	public boolean isChromosomeValid(String analysisId, String chromosomeName) throws SQLException;
	public String associate_unlocalised_list_acc (String objectName, int assembly_level,String analysisId) throws SQLException;
	
}
