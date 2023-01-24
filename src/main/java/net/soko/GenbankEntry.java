package net.soko;

import java.util.ArrayList;
import java.util.List;

public class GenbankEntry {
    private String accession = "unknown";
    private String locus = "unknown";
    private String definition = "unknown";
    private List<GenbankReference> references = new ArrayList<>();

    public String getAccession() {
        return accession;
    }

    public void setAccession(String accession) {
        this.accession = accession;
    }

    public String getLocus() {
        return locus;
    }

    public void setLocus(String locus) {
        this.locus = locus;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public List<GenbankReference> getReferences() {
        return references;
    }

    public void setReferences(List<GenbankReference> references) {
        this.references = references;
    }

}
