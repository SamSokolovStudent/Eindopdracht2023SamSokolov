package net.soko;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Genbank entry. It contains the accession number, locus, definition and a list of {@link GenbankReference} objects.
 * <p> <strong>Example usage:</strong>
 * <pre>
 * {@code
 *     GenbankEntry entry = new GenbankEntry();
 *     entry.setAccession("NC_000001.1");
 *     entry.setLocus("NC_000001");
 *     entry.setDefinition("Homo sapiens chromosome 1, complete genome.");
 *     GenbankReference reference = new GenbankReference();
 *     ... set the reference fields ...
 *     entry.setReferences(List.of(reference));
 * }
 * <p> <strong>Limitations:</strong>  Due to record classes being immutable and this class having mutable fields, this class is not a record class.
 * @see GenbankReference
 */
public class GenbankEntry {
    /**
     * The accession number of this entry.
     */
    private String accession = "unknown";
    /**
     * The locus of this entry.
     */
    private String locus = "unknown";
    /**
     * The definition of this entry.
     */
    private String definition = "unknown";
    /**
     * The list of references of this entry.
     */
    private List<GenbankReference> references = new ArrayList<>();

    /*
    Getters and setters for the fields.
    Unused methods are retained, possibly for future use.
     */

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public List<GenbankReference> getReferences() {
        return references;
    }

    @SuppressWarnings("unused")
    public void setReferences(List<GenbankReference> references) {
        this.references = references;
    }

}
