# Eindopdracht 2023 SamSokolov

This project is a program that is able to read genbank flat file (.gbff) files and store the data in a list. The program allows users to view the genes associated with a particular person or publication and also makes it possible to search for an author in the list. It also allows navigation between an author and a publication and the ability to select an author or publication to write to a file with all associated data. The program is a command line tool and is designed to run on Unix machines (linux & mac).

## Installation

The program uses gradle and the build can be found in `./build/libs/eindopdracht-1.0-SNAPSHOT.jar`.
In case a build isn't present you can build the program yourself by cloning the repository and running ./gradlew build.

## Usage
The tool will always need an input directory, it can handle both .gz files and .gbff files. 
All files in the directory must be of either of these types. 

The tool has the following optional arguments:

- `a` or `-authors`: Display all authors in listed files.
- `p` or `-publications`: Display all publications in listed files.
- `ba` or `-by-author`: Enter an author to display all publications by that author. Needs an exact match.
- `bp` or `-by-publication`: Enter a publication to display all authors of that publication. Can parse partial names.
- `o` or `-output`: The output file to write the results to. Will create a new file in directory in case file is not found.
- `h` or `-help`: Display the help menu.

![image](https://user-images.githubusercontent.com/90578942/215222603-5d6a7686-299e-4c9a-9a13-98afa9b555a0.png)


The program is broken up into 4 classes:

- `GenbankExplorer`: Acts as the main class and handles the command line arguments.
- `GenbankParser`: Takes in the file and stores the information in two objects: `GenbankEntry` and `GenbankReference`.
- `GenbankEntry`: Stores information such as locus, etc.
- `GenbankReference`: Stores information such as author and journal.

The `GenbankEntry` objects are stored in a list for use with the commands.

![image](https://user-images.githubusercontent.com/90578942/215222542-134fae4e-f9c8-4049-8747-326af34bd940.png)

Testing files for this project with which this program is known to work are stored under ./src/main/resources/genbank/*

![image](https://user-images.githubusercontent.com/90578942/215222518-2db9f793-d284-4afc-b71b-9b65ccd30293.png)

These commands were run from within IntelliJ under 'gradle/run' and are known to work.

![image](https://user-images.githubusercontent.com/90578942/215222454-ef54e729-d5c9-4a3f-b7d8-02a2c6f879c1.png)

Listing authors
`run --args="./src/main/resources/genbank/ -a"`

Listing genomes related to author
`run --args="./src/main/resources/genbank/ -ag Thayer,N."`

etc.

![image](https://user-images.githubusercontent.com/90578942/215222483-6eb6572e-fe5d-4c28-85e5-7e79ff6d7ac7.png)

## Extra Information
The program is documented according to Javadoc standards and is self documented where needed by means of clear and concise identifiers/variable names.
Logic is explained inside code where needed and if reoccuring stated as such.

## Future Improvements

In the future, the program can be improved to serialize and deserialize the parsed objects (store the results) in order to reduce processing power when dealing with large .gbff files.
