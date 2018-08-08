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

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.reference.Reference;
import uk.ac.ebi.embl.api.validation.ValidationEngineException;

public interface EraproDAOUtils
{
	Reference getSubmitterReference(String analysisId) throws SQLException,UnsupportedEncodingException;
	public List<String> isAssemblyDuplicate(String analysisId) throws SQLException;
	AssemblySubmissionInfo getAssemblySubmissionInfo(String analysisId) throws SQLException;
	public List<String> isSampleHasDifferentProjects(String analysisId) throws SQLException;
	public Entry getMasterEntry(String analysisId) throws SQLException; 
	public class AssemblySubmissionInfo
	{
		String studyId;
		String projectId;
		String biosampleId;
		String sampleId;
		String submissionAccountId;
		java.sql.Date begindate;
		java.sql.Date enddate;
		
		public String getStudyId() {
			return studyId;
		}
		public void setStudyId(String studyId) {
			this.studyId = studyId;
		}
		public String getProjectId() {
			return projectId;
		}
		public void setProjectId(String projectId) {
			this.projectId = projectId;
		}
		public String getBiosampleId() {
			return biosampleId;
		}
		public void setBiosampleId(String biosampleId) {
			this.biosampleId = biosampleId;
		}
		public String getSampleId() {
			return sampleId;
		}
		public void setSampleId(String sampleId) {
			this.sampleId = sampleId;
		}
		public String getSubmissionAccountId() {
			return submissionAccountId;
		}
		public void setSubmissionAccountId(String submissionAccountId) {
			this.submissionAccountId = submissionAccountId;
		}
		public java.sql.Date getBegindate() {
			return begindate;
		}
		public void setBegindate(java.sql.Date begindate) {
			this.begindate = begindate;
		}
		public java.sql.Date getEnddate() {
			return enddate;
		}
		public void setEnddate(java.sql.Date enddate) {
			this.enddate = enddate;
		}
	}
}
