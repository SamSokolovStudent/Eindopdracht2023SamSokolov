package net.soko;

import java.util.HashSet;
import java.util.Set;

public class GenbankReference {
    private Set<String> authors = new HashSet<>();
    private String title = "unknown";
    private String journals = "unknown";
    private int pubmedId;

    public Set<String> addAuthor(String author) {
        this.authors.add(author);
        return this.authors;
    }

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

    public String getJournals() {
        return journals;
    }

    public void setJournals(String journals) {
        this.journals = journals;
    }

    public int getPubmedId() {
        return pubmedId;
    }

    public void setPubmedId(int pubmedId) {
        this.pubmedId = pubmedId;
    }
}
