package uk.ac.ebi.embl.api.validation.submission;

import static org.junit.Assert.assertTrue;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.ac.ebi.embl.api.validation.ValidationEngineException;
import uk.ac.ebi.embl.api.validation.file.SubmissionValidationTest;
import uk.ac.ebi.embl.api.validation.helper.FlatFileComparatorException;
import uk.ac.ebi.embl.api.validation.submission.SubmissionFile.FileType;

public class SubmissionValidationPlanTest extends SubmissionValidationTest 
{
    SubmissionOptions options =null;
    @Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void init()
	{
       options = new SubmissionOptions();
       options.isRemote = true;
	   options.assemblyInfoEntry =Optional.of(getAssemblyinfoEntry());
	   options.source = Optional.of(getSource());
    }
	
	@Test
	public void testGenomeSubmissionwithFastaFlatfile() throws ValidationEngineException, FlatFileComparatorException
	{
		options.context = Optional.of(Context.genome);
		SubmissionFiles submissionFiles = new SubmissionFiles();
		submissionFiles.addFile(initSubmissionFixedTestFile("valid_genome_fasta.txt", FileType.FASTA));
		submissionFiles.addFile(initSubmissionFixedTestFile("valid_genome_flatfile.txt", FileType.FLATFILE));
		options.submissionFiles = Optional.of(submissionFiles);
		options.reportDir = Optional.of(initSubmissionTestFile("valid_genome_fasta.txt", FileType.FASTA).getFile().getParent());
		SubmissionValidationPlan plan = new SubmissionValidationPlan(options);
		plan.execute();
        assertTrue(compareOutputFiles(initSubmissionFixedTestFile("valid_genome_fasta.txt", FileType.FASTA).getFile()));
        assertTrue(compareOutputFiles(initSubmissionFixedTestFile("valid_genome_flatfile.txt", FileType.FASTA).getFile()));
	}
	
	@Test
	public void testGenomeSubmissionwithFastaChromosomeListsequenceless() throws ValidationEngineException, FlatFileComparatorException
	{
		options.context = Optional.of(Context.genome);
		SubmissionFiles submissionFiles = new SubmissionFiles();
		submissionFiles.addFile(initSubmissionFixedTestFile("valid_genome_fasta.txt", FileType.FASTA));
		submissionFiles.addFile(initSubmissionFixedTestFile("chromosome_list_sequenceless.txt", FileType.CHROMOSOME_LIST));
		options.submissionFiles = Optional.of(submissionFiles);
		options.reportDir = Optional.of(initSubmissionTestFile("valid_genome_fasta.txt", FileType.FASTA).getFile().getParent());
		SubmissionValidationPlan plan = new SubmissionValidationPlan(options);
		thrown.expect(ValidationEngineException.class);
		thrown.expectMessage("Sequenceless chromosomes are not allowed in assembly : IWGSC_CSS_6DL_SCAFF_3330719,IWGSC_CSS_6DL_SCAFF_3330717,IWGSC_CSS_6DL_SCAFF_3330716");
		plan.execute();
	}
	
	@Test
	public void testGenomeSubmissionwitFastawithValidChromosomeList() throws ValidationEngineException, FlatFileComparatorException
	{
		options.context = Optional.of(Context.genome);
		SubmissionFiles submissionFiles = new SubmissionFiles();
		submissionFiles.addFile(initSubmissionFixedTestFile("valid_genome_fasta_chromosome.txt", FileType.FASTA));
		submissionFiles.addFile(initSubmissionFixedTestFile("chromosome_list.txt", FileType.CHROMOSOME_LIST));
		options.submissionFiles = Optional.of(submissionFiles);
		options.reportDir = Optional.of(initSubmissionTestFile("valid_genome_fasta_chromosome.txt", FileType.FASTA).getFile().getParent());
		SubmissionValidationPlan plan = new SubmissionValidationPlan(options);
		plan.execute();
		assertTrue(compareOutputFiles(initSubmissionFixedTestFile("valid_genome_fasta_chromosome.txt", FileType.FASTA).getFile()));
	}
	
	@Test
	public void testGenomeSubmissionwithFastaAGP() throws FlatFileComparatorException, ValidationEngineException
	{
		options.context = Optional.of(Context.genome);
		SubmissionFiles submissionFiles = new SubmissionFiles();
		submissionFiles.addFile(initSubmissionFixedTestFile("valid_flatfileforAgp.txt", FileType.FLATFILE));
		submissionFiles.addFile(initSubmissionFixedTestFile("valid_flatfileagp.txt", FileType.AGP));
		options.submissionFiles = Optional.of(submissionFiles);
		options.reportDir = Optional.of(initSubmissionTestFile("valid_flatfileforAgp.txt", FileType.FLATFILE).getFile().getParent());
		SubmissionValidationPlan plan = new SubmissionValidationPlan(options);
		plan.execute();
		assertTrue(compareOutputFiles(initSubmissionFixedTestFile("valid_flatfileforAgp.txt", FileType.FLATFILE).getFile()));
		assertTrue(compareOutputFiles(initSubmissionFixedTestFile("valid_flatfileagp.txt", FileType.FLATFILE).getFile()));
		assertTrue(compareOutputFiles(initSubmissionFixedTestFile("valid_flatfileagp.txt.sequence", FileType.FLATFILE).getFile()));
	}
	
	@Test
	public void testValidTranscriptomSubmission()
	{
		
	}

	@Test
	public void testInvalidTranscriptomSubmission()
	{
		
	}

}
