package net.soko;

import org.jetbrains.annotations.NotNull;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

/**
 * This class is the main class of the GenbankExplorer program used to explore Genbank Flat Files.
 * <p> More detailed information about how the Command Line Interface works can be found in the {@link CommandLine} documentation.
 * or on the <a href="https://picocli.info/">picocli website</a>.
 * <p> Makes use of the {@link GenbankParser} class to parse the Genbank Flat Files.
 *
 * <p> <strong> Example usage</strong>:
 * <pre>
 *     {@code
 *     java -jar genbank_explorer.jar path/to/directory -a -o output.txt
 *     }
 *     </pre>
 *     <p> The program can be run with the following parameters:
 *     <ul>
 *         <li> Index 0: The directory with the Genbank files to explore. </li>
 *         <li> <strong>-a, --authors</strong>: Display all authors in listed files. </li>
 *         <li> <strong>-p, --publications</strong>: Display all publications in listed files. </li>
 *         <li> <strong>-ba, --by-author</strong>: Enter an author to display all publications by that author. <p><em>Needs an exact match.</li>
 *         <li> <strong>-bp, --by-publication</strong>: Enter a publication to display all authors of that publication. <p><em>Can parse partial names.</li>
 *         <li> <strong>-o, --output</strong>: The output file to write the results to. <p><em>Will create a new file in directory in case file is not found.</li>
 *         <li> <strong>-h, --help</strong>: Display the help menu. </li>
 *         </ul>
 * <p><strong>  Limitations</strong> : The program does not support looking for multiple authors or publications at the same time.
 * @see GenbankParser
 * @see CommandLine
 */
@Command(name = "genbank_explorer", mixinStandardHelpOptions = true, version = "genbank_explorer 1.0",
        description = "Explore Genbank files")
public class GenbankExplorer implements Callable<Integer> {


    /**
     * Mandatory CL parameter: The directory with the Genbank files to explore.
     * <p> If the directory is not found or is not a directory, an error is printed to the console and the program exits.
     */
    @Parameters(index = "0", description = "The directory with the Genbank files to explore.")
    private File directory;

    /**
     * Exclusive CL parameters: The user can choose to display all authors in listed files, all publications in listed files,
     * enter an author to display all publications by that author, or enter a publication to display all authors of that publication.
     * <p> If the user does not enter any of these parameters, an error is printed to the console and the program exits.
     */
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

    /**
     * Optional CL parameter: Write to a file instead of std-out.
     */
    @Option(names = {"-o", "--output"}, description = "Write to a file instead of std-out.")
    private File output;

    /**
     * Main method of the program.
     * <p> Parses the command line arguments and runs the program.
     * @throws Exception if unable to compute a result as per {@link Callable#call()}
     */
    public Integer call() throws Exception{
        // Get all files in the directory and check if the directory exists / are valid
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
            // Check if the file is a Genbank Flat File
            if (file.getName().endsWith(".gbff")) {
                entries.addAll(GenbankParser.parseGenbankFile(file));
            } else {
                System.err.println("File " + file + " is not a Genbank Flat File");
            }
        }

        // Give results based on the CL exclusive parameters.
        if (exclusive.authors) {
            // HashSet to store all unique authors from entries and ArrayList to sort them alphabetically.
            HashSet<String> authors = new HashSet<>();
            for (GenbankEntry entry : entries) {
                for (GenbankReference reference : entry.getReferences()) {
                    authors.addAll(reference.getAuthors());
                }
            }
            ArrayList<String> authorsList = new ArrayList<>(authors);
            authorsList.sort(String::compareTo);
            String outputString = output == null ? "Authors found:" : "Writing to file " + output;
            System.out.println(outputString);
            // Write to file if output is not null, otherwise print to std-out. This pattern is used for all other CL parameters as well.
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
            String outputString = output == null ? "Publications found:" : "Writing to file " + output;
            System.out.println(outputString);
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
                String outputString = output == null ? "Publications by " + exclusive.byAuthor + ":" : "Writing to file " + output;
                System.out.println(outputString);
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
                String outputString = output == null ? "Authors of " + exclusive.byPublication + ":" : "Writing to file " + output;
                System.out.println(outputString);
                for (String author : authorsList) {
                    if (output != null) {
                        System.out.println("Output written to file: " + output.getName());
                        writeToFile(output, author);
                    } else {
                        System.out.println(author);
                    }
                }
            }
        } else {
            System.err.println("No options selected");
            return 1;
        }
        return 0;
    }

    /**
     * Writes a string to a file, if the file does not exist, it will be created.
     *
     * @param file The file to write to.
     * @param text The text to write.
     */
    public static void writeToFile(@NotNull File file, String text) {
        text += System.lineSeparator();
        try {
            if (!file.isFile()) {
                Files.writeString(file.toPath(), text, StandardOpenOption.CREATE);
            } else {
                Files.writeString(file.toPath(), text, StandardOpenOption.APPEND);
            }
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
