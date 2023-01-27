package net.soko;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

/**
 * This parser class is used to parse 'Genbank Flat Files' or 'gbff' and extract the relevant information for the command line explorer.
 * <p> The Genbank Flat File format, and its relevant fields are described <a href="https://www.ncbi.nlm.nih.gov/Sitemap/samplerecord.html"> here </a>.
 * <p> Each {@link GenbankEntry} object represents a single entry in the Genbank Flat File and contains information such as
 * the accession number, locus, definition and multiple {@link GenbankReference} objects for every reference.
 *
 * <p> <strong> Example usage</strong> :
 * <pre>
 *         {@code
 *         File file = new File("path/to/file.gbff");
 *         ArrayList<GenbankEntry> entries = GenbankParser.parseGenbankFile(file);
 *         }
 *         </pre>
 * </p>
 * @see GenbankExplorer
 * @see GenbankEntry
 * @see GenbankReference
 */
public class GenbankParser {

    /**
     * Parser method for a Genbank File that parses the file per entry and returns an ArrayList of {@link GenbankEntry} objects.
     * Each entry can have several references, which are stored in a {@link GenbankReference} object.
     * <p>
     * If the file is not found, a {@link FileNotFoundException} is thrown.
     * <br>
     *
     * @param file the Genbank file to parse.
     * @return an ArrayList of {@link GenbankEntry} objects.
     * @see GenbankEntry
     * @see GenbankReference
     */
    public static ArrayList<GenbankEntry> parseGenbankFile(File file) {
        ArrayList<GenbankEntry> entries = new ArrayList<>();
        // The current entry is used to store the current entry being parsed.
        GenbankEntry currentEntry = null;
        // The current reference is used to store the current reference being parsed. Multiple references can be stored in a single entry.
        GenbankReference currentReference = null;
        try {
            Scanner scanner = new Scanner(file);
            String line = null;
            if (scanner.hasNextLine()) {
                line = scanner.nextLine();
            }
            while (line != null) {
                line = line.strip();
                if (line.startsWith("LOCUS")) {
                    /* When encountering a new entry, indicated by the "LOCUS" tag, the current entry is added to the list of entries.
                    and a new entry is created.*/
                    if (currentEntry != null) {
                        entries.add(currentEntry);
                    }
                    currentEntry = new GenbankEntry();
                    String[] parts = line.split("\\s+");
                    currentEntry.setLocus(parts[1]);
                } else if (line.startsWith("ACCESSION") && currentEntry != null) {
                    currentEntry.setAccession(line);
                } else if (line.startsWith("DEFINITION") && currentEntry != null) {
                    /* Definition can be multiple lines, so a StringBuilder is used to concatenate the lines,
                     starting with 12 space indents.
                     The tag is removed from the first line, and the rest of the lines are concatenated.
                     This pattern is used for other fields as well but isn't able to be abstracted into a method due to high complexity of the formatting.
                     */
                    StringBuilder sb = new StringBuilder(line.substring(10));
                    while (scanner.hasNextLine() && (line = scanner.nextLine()).startsWith("            ")) {
                        sb.append(line.strip());
                        sb.append(" ");
                    }
                    currentEntry.setDefinition(sb.toString().strip());
                } else if (line.startsWith("REFERENCE") && currentEntry != null) {
                    if (currentReference != null) {
                        currentEntry.getReferences().add(currentReference);
                    }
                    currentReference = new GenbankReference();
                } else if (line.startsWith("AUTHORS") && currentReference != null) {
                    StringBuilder sb = new StringBuilder(line.substring(8));
                    while (scanner.hasNextLine() && (line = scanner.nextLine()).startsWith("            ")) {
                        sb.append(line.strip());
                        sb.append(" ");
                    }
                    // Split the authors by comma and 'and' and add them to the reference, whitespaces are accounted for in case names contain the phrase 'and'.
                    String[] authors = sb.toString().split("(,\\s)|(\\sand\\s)");
                    for (String author : authors) {
                        currentReference.addAuthor(author.strip());
                    }
                    continue;
                } else if (line.startsWith("TITLE") && currentReference != null) {
                    StringBuilder sb = new StringBuilder(line.substring(5));
                    while (scanner.hasNextLine() && (line = scanner.nextLine()).startsWith("            ")) {
                        sb.append(line.strip());
                        sb.append(" ");
                    }
                    currentReference.setTitle(sb.toString().strip());
                } else if (line.startsWith("JOURNAL") && currentReference != null) {
                    StringBuilder sb = new StringBuilder(line.substring(7));
                    while (scanner.hasNextLine() && (line = scanner.nextLine()).startsWith("            ")) {
                        sb.append(line);
                        sb.append(" ");
                    }
                    currentReference.setJournal(sb.toString().strip());
                } else if (line.startsWith("PUBMED") && currentReference != null) {
                    currentReference.setPubmedId(Integer.parseInt(line.substring(7).strip()));
                }
                if (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                } else {
                    // If there are no more lines, set the line to null to exit the loop.
                    line = null;
                }
            }
            // Add reference to current entry
            if (currentReference != null) {
                currentEntry.getReferences().add(currentReference);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        // Add the last entry to the list
        entries.add(currentEntry);
        return entries;
    }

    /**
     * Method to unzip a Genbank file in case it is compressed.
     * <p>
     *     The method creates a temporary file to store the uncompressed file which is deleted on exit,
     *     it is called in the {@link GenbankExplorer#call()} method whenever the file ends with ".gz".
     *     The method is based on an example found on <a href="https://www.digitalocean.com/community/tutorials/java-gzip-example-compress-decompress-file">Digital Ocean</a>
     *
     * @param file The file to be uncompressed.
     * @return The uncompressed file.
     * @see GenbankExplorer#call()
     */
    public static File gUnzip(File file)  {
        	File newFile = null;
        	try {
        		newFile = File.createTempFile("genbank", ".gbff");
        		newFile.deleteOnExit();
        		GZIPInputStream gzipInputStream = new GZIPInputStream(new FileInputStream(file));
        		FileOutputStream fileOutputStream = new FileOutputStream(newFile);
        		byte[] buffer = new byte[1024];
        		int len;
        		while ((len = gzipInputStream.read(buffer)) != -1) {
        			fileOutputStream.write(buffer, 0, len);
        		}
        		gzipInputStream.close();
        		fileOutputStream.close();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        	return newFile;
    }
}
