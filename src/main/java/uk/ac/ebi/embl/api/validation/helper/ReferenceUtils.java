package uk.ac.ebi.embl.api.validation.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import uk.ac.ebi.embl.api.entry.reference.*;
import uk.ac.ebi.embl.api.validation.ValidationEngineException;
import uk.ac.ebi.embl.api.validation.dao.model.SubmissionAccount;
import uk.ac.ebi.embl.api.validation.dao.model.SubmissionContact;
import uk.ac.ebi.embl.api.validation.dao.model.SubmitterReference;
import uk.ac.ebi.embl.flatfile.FlatFileUtils;
import uk.ac.ebi.embl.flatfile.reader.embl.EmblPersonMatcher;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class ReferenceUtils {
    /**
     * build <Reference> object from
     * - Webin-CLI: the authors and address information directly passed from manifest file
     * - Processing pipelines: the authors and address information fetched from analysis.xml(we add manifest information into analysis.xml when the submitter makes a submission)
     * Note: first_name won't be abbreviated for this case , in other hand, when we fetch contact information from db we do abbreviate it
     */
    public Reference getSubmitterReferenceFromManifest(String authors, String address, Date date, String submissionAccountId) throws ValidationEngineException {
        Publication publication = getPublication(address, date);
        if (doAddAuthorsToConsortium(submissionAccountId)) {
            publication.setConsortium(authors);
        } else {
            List<Person> authorList = getAuthors(authors);
            if (authors.isEmpty()) {
                throw new ValidationEngineException("Authors value is invalid:" + authors);
            }
            publication.addAuthors(authorList);
        }
        Reference ref = new ReferenceFactory().createReference();
        ref.setPublication(publication);
        ref.setAuthorExists(true);
        ref.setLocationExists(true);
        ref.setReferenceNumber(1);
        return ref;
    }
    private  Publication getPublication(String address, Date date) {
        Submission submission = (new ReferenceFactory()).createSubmission();
        submission.setDay(date == null ? Calendar.getInstance().getTime(): date);
        if(address != null) {
            submission.setSubmitterAddress(address);
        }
        return submission;
    }

    private List<Person> getAuthors(String block) {
        List<Person> authors = new ArrayList<>();
        block = FlatFileUtils.remove(block, ';');
        for (String author : FlatFileUtils.split(block, ",")) {
            EmblPersonMatcher personMatcher = new EmblPersonMatcher(null);
            if (!personMatcher.match(author)) {
                authors.add(new ReferenceFactory().createPerson( author, null));
            }
            else {
                authors.add(personMatcher.getPerson());
            }
        }
        return authors;
    }

    boolean doAddAuthorsToConsortium(String submissionAccountId) {
        return submissionAccountId != null && submissionAccountId.equalsIgnoreCase("Webin-55551");
    }

    public Reference constructSubmitterReference(SubmitterReference submitterReference) throws ValidationEngineException, UnsupportedEncodingException {
        Publication publication = new Publication();
        ReferenceFactory referenceFactory = new ReferenceFactory();
        Reference reference = referenceFactory.createReference();
        HashSet<String> consortium = new HashSet<>();
        String pubConsortium = "";

        for (SubmissionContact submissionContact : submitterReference.getSubmissionContacts()) {
            String pConsrtium = submissionContact.getConsortium();
            consortium.add(pConsrtium);

            if (pConsrtium == null)//ignore first_name,middle_name and last_name ,if consortium is given : WAP-126
            {
                Person person = null;

                person = referenceFactory.createPerson(
                        EntryUtils.concat(" ", WordUtils.capitalizeFully(
                                EntryUtils.convertNonAsciiStringtoAsciiString(submissionContact.getSurname()), '-', ' '),
                                EntryUtils.convertNonAsciiStringtoAsciiString(submissionContact.getMiddleInitials())),
                        getFirstName(EntryUtils.convertNonAsciiStringtoAsciiString(submissionContact.getFirstName())));

                publication.addAuthor(person);
                reference.setAuthorExists(true);
            }
        }
        Submission submission = referenceFactory.createSubmission(publication);
        submission.setSubmitterAddress(getAddressFromSubmissionAccount(submitterReference.getSubmissionAccount()));
        submission.setDay(submitterReference.getFirstCreated());
        publication = submission;
        reference.setPublication(publication);
        reference.setLocationExists(true);
        reference.setReferenceNumber(1);
        for (String refCons : consortium) {
            if (refCons != null)
                pubConsortium += refCons + ", ";
        }
        if (pubConsortium.endsWith(", ")) {
            pubConsortium = pubConsortium.substring(0, pubConsortium.length() - 2);
        }
        if (reference.getPublication() == null)
            return null;
        reference.getPublication().setConsortium(pubConsortium);

        return reference;
    }

    public String getAddressFromSubmissionAccount(SubmissionAccount subAccount) throws ValidationEngineException {
        if(subAccount == null) {
            return null;
        }
        try {
            if (subAccount.isBroker()) {
                return EntryUtils.concat(", ",
                        EntryUtils.convertNonAsciiStringtoAsciiString(subAccount.getBrokerName()),
                        EntryUtils.convertNonAsciiStringtoAsciiString(subAccount.getAddress()),
                        EntryUtils.convertNonAsciiStringtoAsciiString(subAccount.getCountry()));
            } else {
                return EntryUtils.concat(", ",
                        EntryUtils.convertNonAsciiStringtoAsciiString(subAccount.getCenterName()),
                        EntryUtils.convertNonAsciiStringtoAsciiString(subAccount.getLaboratoryName()),
                        EntryUtils.convertNonAsciiStringtoAsciiString(subAccount.getAddress()),
                        EntryUtils.convertNonAsciiStringtoAsciiString(subAccount.getCountry()));
            }
        } catch (UnsupportedEncodingException e) {
            throw new ValidationEngineException(e);
        }
    }

    private String getFirstName(String firstName)
    {
        if(firstName==null)
            return null;
        StringBuilder nameBuilder= new StringBuilder();
        if(StringUtils.containsNone(firstName,"@")&&StringUtils.contains(firstName, "-"))
        {
            String[] names=StringUtils.split(firstName,"-");
            List<String> fnames=Arrays.asList(names);
            int i=0;
            for(String n: fnames)
            {
                i++;
                nameBuilder.append(WordUtils.initials(n).toUpperCase());
                if(i==fnames.size())
                    nameBuilder.append(".");
                else
                    nameBuilder.append(".-");
            }
        }
        else
        {
            nameBuilder.append(firstName.toUpperCase().charAt(0));
            nameBuilder.append(".");
        }
        return nameBuilder.toString();
    }
}
