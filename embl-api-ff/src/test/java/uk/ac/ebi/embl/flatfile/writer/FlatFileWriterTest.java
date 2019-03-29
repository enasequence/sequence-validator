package uk.ac.ebi.embl.flatfile.writer;

import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class FlatFileWriterTest {

    @Test
    public void writeBlock() throws IOException {
        StringWriter strWriter = new StringWriter();
        String comment =
                "The assembly fSpaAur1.1 is based on ~56x PacBio Sequel data, ~62x coverage " +
                        "Illumina HiSeqX data from a 10X Genomics Chromium library generated at the Wellcome Sanger Institute," +
                        " as well as ~71x coverage HiSeqX data from a Hi-C library prepared by Arima Genomics. " +
                        "An initial PacBio assembly was made using Falcon-unzip, and retained haplotigs were " +
                        "identified using purge_haplotigs. The primary contigs were then scaffolded using the " +
                        "10X data with scaff10x, then scaffolded further using the Hi-C data with SALSA2. " +
                        "Polishing and gap-filling of both the primary scaffolds and haplotigs was performed " +
                        "using the PacBio reads and Arrow, followed by two rounds of Illumina polishing using " +
                        "the 10X data and freebayes. Finally, the assembly was manually improved using gEVAL to " +
                        "correct mis-joins and improve concordance with the raw data. Chromosomes are named according " +
                        "to synteny with the GCA_003309015.1 assembly of Sparus aurata." ;

        FlatFileWriter.writeBlock(strWriter, "", "", comment, WrapType.EMBL_WRAP, WrapChar.WRAP_CHAR_BREAK);

        String output = strWriter.toString();
        if(output.endsWith("\n")) {
            assertEquals(output.length()-1, output.lastIndexOf("\n"));
            output = output.substring(0,output.length()-1);
        }
        String expectedComment = "The assembly fSpaAur1.1 is based on ~56x PacBio Sequel data, ~62x coverage\n" +
                "Illumina HiSeqX data from a 10X Genomics Chromium library generated at the\n" +
                "Wellcome Sanger Institute, as well as ~71x coverage HiSeqX data from a Hi-C\n" +
                "library prepared by Arima Genomics. An initial PacBio assembly was made using\n" +
                "Falcon-unzip, and retained haplotigs were identified using purge_haplotigs. The\n" +
                "primary contigs were then scaffolded using the 10X data with scaff10x, then\n" +
                "scaffolded further using the Hi-C data with SALSA2. Polishing and gap-filling of\n" +
                "both the primary scaffolds and haplotigs was performed using the PacBio reads\n" +
                "and Arrow, followed by two rounds of Illumina polishing using the 10X data and\n" +
                "freebayes. Finally, the assembly was manually improved using gEVAL to correct\n" +
                "mis-joins and improve concordance with the raw data. Chromosomes are named\n" +
                "according to synteny with the GCA_003309015.1 assembly of Sparus aurata.";
        assertEquals(expectedComment, output);
    }

    @Test
    public void writeCCSingleLine() throws IOException {
        StringWriter strWriter = new StringWriter();
        String comment =
                "The assembly fSpaAur1.1 is based on ~56x PacBio Sequel data, ~62x coverage " ;

        FlatFileWriter.writeBlock(strWriter, "", "", comment, WrapType.EMBL_WRAP, WrapChar.WRAP_CHAR_BREAK);

        String output = strWriter.toString();
        if(output.endsWith("\n")) {
            assertEquals(output.length()-1, output.lastIndexOf("\n"));
            output = output.substring(0,output.length()-1);
        }
        String expectedComment = "The assembly fSpaAur1.1 is based on ~56x PacBio Sequel data, ~62x coverage ";
        assertEquals(expectedComment, output);

        //no space or break
        comment = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                +"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                +"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                +"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        strWriter = new StringWriter();
        FlatFileWriter.writeBlock(strWriter, "", "", comment, WrapType.EMBL_WRAP, WrapChar.WRAP_CHAR_BREAK);
        output = strWriter.toString();
        if(output.endsWith("\n")) {
            assertEquals(output.length()-1, output.lastIndexOf("\n"));
            output = output.substring(0,output.length()-1);
        }
        assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", output);

        //null not allowed, empty string
        comment = "";
        strWriter = new StringWriter();
        FlatFileWriter.writeBlock(strWriter, "", "", comment, WrapType.EMBL_WRAP, WrapChar.WRAP_CHAR_BREAK);
        output = strWriter.toString();
        if(output.endsWith("\n")) {
            assertEquals(output.length()-1, output.lastIndexOf("\n"));
            output = output.substring(0,output.length()-1);
        }
        assertEquals("", output);
    }


}