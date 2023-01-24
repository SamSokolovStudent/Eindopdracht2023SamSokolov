package net.soko;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class GenbankParser {

    public static ArrayList<GenbankEntry> parseGenbankFile(File file) {
        ArrayList<GenbankEntry> entries = new ArrayList<>();
        GenbankEntry currentEntry = null;
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
                    if (currentEntry != null) {
                        entries.add(currentEntry);
                    }
                    currentEntry = new GenbankEntry();
                    currentEntry.setLocus(line);
                } else if (line.startsWith("ACCESSION") && currentEntry != null) {
                    currentEntry.setAccession(line);
                } else if (line.startsWith("DEFINITION") && currentEntry != null) {
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
                    currentReference.setJournals(sb.toString().strip());
                } else if (line.startsWith("PUBMED") && currentReference != null) {
                    currentReference.setPubmedId(Integer.parseInt(line.substring(7).strip()));
                }
                if (scanner.hasNextLine()) {
                    line = scanner.nextLine();
                } else {
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
        entries.add(currentEntry);
        return entries;
    }
}
