package uk.ac.ebi.embl.api.validation.dao;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import uk.ac.ebi.embl.api.contant.AnalysisType;
import uk.ac.ebi.embl.api.entry.Entry;
import uk.ac.ebi.embl.api.entry.feature.SourceFeature;
import uk.ac.ebi.embl.api.entry.reference.Reference;
import uk.ac.ebi.embl.api.validation.ValidationEngineException;
import uk.ac.ebi.embl.api.validation.dao.model.Analysis;

public interface EraproDAOUtils
{
	Reference getSubmitterReference(String analysisId) throws SQLException,ValidationEngineException;
	List<String> isAssemblyDuplicate(String analysisId) throws SQLException;
	AssemblySubmissionInfo getAssemblySubmissionInfo(String analysisId) throws SQLException;
	List<String> isSampleHasDifferentProjects(String analysisId) throws SQLException;
	Entry getMasterEntry(String analysisId, AnalysisType analysisType) throws SQLException, ValidationEngineException;
	Reference getReference(Entry entry , String analysisId, AnalysisType analysisType) throws SQLException, ValidationEngineException;
	String getTemplateId(String analysisId) throws SQLException;
	Set<String> getLocusTags(String projectId) throws SQLException;
	SourceFeature getSourceFeature(String sampleId) throws Exception;
    boolean isProjectValid(String text) throws SQLException;
	boolean isIgnoreErrors(String submissionAccountId, String context, String name) throws SQLException;
	Analysis getAnalysis(String analysisId) throws SQLException;

	class AssemblySubmissionInfo
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
