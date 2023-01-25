package net.soko;

import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a Genbank reference. It contains the authors, title, journals and PubMed ID.
 * <p> <strong>Example usage:</strong>
 * <pre>
 *     {@code
 *     GenbankReference reference = new GenbankReference();
 *     reference.addAuthor("Smith");
 *     reference.addAuthor("Jones");
 *     reference.setTitle("The genome of Homo sapiens");
 *     reference.setJournals("Nature");
 *     reference.setPubmedId(12345);
 */
public class GenbankReference {
    /**
     * The authors of this reference.
     * A set is used to avoid duplicates.
     */
    private Set<String> authors = new HashSet<>();
    /**
     * The title of this reference.
     */
    private String title = "unknown";
    /**
     * The journal of this reference.
     */
    private String journal = "unknown";
    /**
     * The PubMed ID of this reference.
     */
    private int pubmedId;
    private String locus = "unknown";

    /**
     * Adds an author to the set of authors.
     * @param author The author to add.
     * @return The set of authors.
     */
    public Set<String> addAuthor(String author) {
        this.authors.add(author);
        return this.authors;
    }

    public String authorAndLocus() {
        return String.join(", ", this.authors) + " (" + this.locus + ")";
    }

    /*
    Getters and setters for the fields.
     */
    public Set<String> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLocus(String locus) {
        this.locus = locus;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    public int getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(int pubmedId) {
        this.pubmedId = pubmedId;
    }

    public String getLocus() {
        return locus;
    }
}
