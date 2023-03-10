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

    /**
     * Adds an author to the set of authors.
     *
     * @param author The author to add.
     */
    public void addAuthor(String author) {
        this.authors.add(author);
    }

    /*
    Getters and setters for the fields.
    Unused methods are retained, possibly for future use.
     */

    public Set<String> getAuthors() {
        return authors;
    }

    @SuppressWarnings("unused")
    public void setAuthors(Set<String> authors) {
        this.authors = authors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @SuppressWarnings("unused")
    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

    @SuppressWarnings("unused")
    public int getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(int pubmedId) {
        this.pubmedId = pubmedId;
    }

}
