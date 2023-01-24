package net.soko;

import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "genbank_explorer", mixinStandardHelpOptions = true, version = "genbank_explorer 1.0",
        description = "Explore Genbank files")
public class GenbankExplorer implements Callable<Integer> {


    @Parameters(index = "0", description = "The directory with the Genbank files to explore.")
    private File directory;


    @ArgGroup(exclusive = true, multiplicity = "1")
    Exclusive exclusive;

    static class Exclusive {
        // Display all authors in listed files
        @Option(names = {"-a", "--authors"}, description = "Display all authors in listed files.", required = true)
        private boolean authors;

        // Display all publications in listed files
        @Option(names = {"-p", "--publications"}, description = "Display all publications in listed files.", required = true)
        private boolean publications;

        // Enter an author to display all publications by that author
        @Option(names = {"-ba", "--by-author"}, description = "Enter an author to display all publications by that author.", required = true)
        private String byAuthor;

        // Enter a publication to display all authors of that publication
        @Option(names = {"-bp", "--by-publication"}, description = "Enter a publication to display all authors of that publication. Works with partial names", required = true)
        private String byPublication;
    }

    // Write to a file instead of std-out.
    @Option(names = {"-o", "--output"}, description = "Write to a file instead of std-out.")
    private File output;

    public Integer call() throws Exception {
        if (!directory.exists()) {
            System.err.println("Directory " + directory + " does not exist");
            return 1;
        }
        File[] contents = directory.listFiles();
        if (contents == null) {
            System.err.println("Directory " + directory + " is not a directory");
            return 1;
        }
        ArrayList<GenbankEntry> entries = new ArrayList<>();
        for (File file : contents) {
            if (file.getName().endsWith(".gbff")) {
                System.out.println(file);
                entries.addAll(GenbankParser.parseGenbankFile(file));
            }
        }

        if (exclusive.authors) {
            HashSet<String> authors = new HashSet<>();
            for (GenbankEntry entry : entries) {
                for (GenbankReference reference : entry.getReferences()) {
                    authors.addAll(reference.getAuthors());
                }
            }
            ArrayList<String> authorsList = new ArrayList<>(authors);
            authorsList.sort(String::compareTo);
            System.out.println("Authors found:");
            for (String author : authorsList) {
                if (output != null) {
                    writeToFile(output, author);
                } else {
                    System.out.println(author);
                }
            }
        } else if (exclusive.publications) {
            HashSet<String> publications = new HashSet<>();
            for (GenbankEntry entry : entries) {
                for (GenbankReference reference : entry.getReferences()) {
                    publications.add(reference.getTitle());
                }
            }
            ArrayList<String> publicationsList = new ArrayList<>(publications);
            publicationsList.sort(String::compareTo);
            System.out.println("Publications found:");
            for (String publication : publicationsList) {
                if (output != null) {
                    writeToFile(output, publication);
                } else {
                    System.out.println(publication);
                }
            }
        } else if (exclusive.byAuthor != null) {
            HashSet<String> publications = new HashSet<>();
            for (GenbankEntry entry : entries) {
                for (GenbankReference reference : entry.getReferences()) {
                    if (reference.getAuthors().contains(exclusive.byAuthor)) {
                        publications.add(reference.getTitle());
                    }
                }
            }
            if (publications.isEmpty()) {
                System.out.println("No publications found for " + exclusive.byAuthor);
                System.out.println("Please type an exact match for the author's name. For example, \"Reilly,L.P.\" instead of \"Reilly\".");
            } else {
                ArrayList<String> publicationsList = new ArrayList<>(publications);
                publicationsList.sort(String::compareTo);
                System.out.println("Publications by " + exclusive.byAuthor + ":");
                for (String publication : publicationsList) {
                    if (output != null) {
                        writeToFile(output, publication);
                    } else {
                        System.out.println(publication);
                    }
                }
            }
        } else if (exclusive.byPublication != null) {
            HashSet<String> authors = new HashSet<>();
            out:
            for (GenbankEntry entry : entries) {
                for (GenbankReference reference : entry.getReferences()) {
                    if (reference.getTitle().contains(exclusive.byPublication)) {
                        authors.addAll(reference.getAuthors());
                        // Atari
                        break out;
                    }
                }
            }
            if (authors.isEmpty()) {
                System.out.println("No authors found for " + exclusive.byPublication);
            } else {
                ArrayList<String> authorsList = new ArrayList<>(authors);
                authorsList.sort(String::compareTo);
                System.out.println("Authors of " + exclusive.byPublication + ":");
                for (String author : authorsList) {
                    if (output != null) {
                        writeToFile(output, author);
                    } else {
                        System.out.println(author);
                    }
                }
            }
        }
        return 0;
    }

    public static void writeToFile(File file, String text) {
        try {
            System.out.println("File created: " + file.getName());
            Files.writeString(file.toPath(), text, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
        int exitCode = new CommandLine(new GenbankExplorer()).execute(args);
        System.exit(exitCode);
    }
}
