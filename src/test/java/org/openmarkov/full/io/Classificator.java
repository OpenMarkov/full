package org.openmarkov.full.io;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.ProbNetWriter;
import org.openmarkov.core.model.network.CycleLength;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.StringWithProperties;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;
import org.openmarkov.io.probmodel.strings.XMLAttributes;
import org.openmarkov.io.probmodel.writer.PGMXWriter_0_2;
import org.openmarkov.io.probmodel.writer.PGMXWriter_0_5;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

public class Classificator extends PGMXReader_0_2 {

/*
    public static void main(String args[]) {
        try {
            Classificator classificator = new Classificator(args);
            classificator.testConversionBetweenVersions();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // Attributes
    private static final String defaultPpathToTestFiles="/home/manuel/Redes/OriginalNetworks";
    private static final String defaultPathToNewFiles = "/home/manuel/Redes/NewNetworks";

    private final String V0_2 = "0.2.0";
    private final String V0_5 = "0.5.0";
    private final String V0_7 = "0.7.0";

    private String pathToTestFiles;
    private String pathToNewFiles;

    private String[] networksOnly0_5 = {

    };

    // Constructor
    */
/** This class performs several operations with files in PGMX format.
     * @param paths Optional String[] parameter. paths[0] = path to files; paths[1] = path to new files. *//*

    public Classificator(String[] paths) throws IOException {
        setPaths(paths);
        File fileToPathToTestFiles = new File(pathToNewFiles);
        if (!fileToPathToTestFiles.exists() || !fileToPathToTestFiles.isDirectory()) {
            throw new IOException("No test path.");
        } else {
            File testNodeFile = new File(pathToTestFiles);
            StringFilter filter = new PGMXFiles();
            List<StringFilter> filters = new ArrayList < StringFilter>(1);
            filters.add(new PGMXFiles());
            for (FileIterator iterator = new FileIterator(testNodeFile, filters); iterator.hasNext(); ) {
                File originalFile = iterator.next();
                String originalFileName = originalFile.getAbsolutePath();
                String version = null;
                try {
                    version = getVersion(originalFile);
                } catch (ParserException e) {
                    writeExceptionInfo("Can not read file: " + originalFileName, null, e);
                    continue;
                }
                PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
                InputStream networkStream = getClass().getClassLoader().getResourceAsStream(originalFileName);
                ProbNetInfo probNetInfo = null;
                try {
                    probNetInfo = pgmxReader.loadProbNetInfo(originalFileName, networkStream);
                } catch (ParserException e) {
                    e.printStackTrace();
                }
                ProbNet originalProbNet = probNetInfo.getProbNet();
                List<EvidenceCase> originalEvidenceCases = probNetInfo.getEvidence();

                // Test begins here

                try {

                    // Write and read probNetInfo in versions 0.2 and 0.7.

                    String pathToNewFile0_2 = getNewPath(originalFileName, V0_2);
                    if (!(version.matches(V0_5) && only0_5(pathToNewFile0_2))) {
                        // Write 0.2
                        ProbNetWriter writer02 = new PGMXWriter_0_2();
                        writer02.writeProbNet(pathToNewFile0_2, originalProbNet, originalEvidenceCases);

                        // Read 0.2
                        InputStream networkStream02_bis = getClass().getClassLoader().getResourceAsStream(originalFileName);
                        ProbNetInfo probNetInfo02_bis = null;
                        try {
                            probNetInfo02_bis = pgmxReader.loadProbNetInfo(pathToNewFile0_2, networkStream02_bis);
                        } catch (ParserException e) {
                            e.printStackTrace();
                        }
                    }

                    ProbNet originalProbNet = probNetInfo.getProbNet();
                    List<EvidenceCase> originalEvidenceCases = probNetInfo.getEvidence();

                    PGMXReader_0_2 pgmxReader0_2bis = new PGMXReader_0_2();
//                    ProbNetInfo probNetInfo02 =

                    String pathToNewFile0_5 = getNewPath(originalFile.getAbsolutePath(), V0_5);
                    ProbNetWriter writer05 = new PGMXWriter_0_5();
                    writer02.writeProbNet(pathToNewFile0_5, originalProbNet, originalEvidenceCases);


                    // Read the probNetInfo recently written in both versions.
                    // Compare the contents with the original probNetInfo.
                    // Report differences for each network and write message
                } catch (ParserException e) {
                    writeExceptionInfo("Can not read: " + originalFile.getAbsolutePath(), version, e);
                } catch (WriterException e) {
                    writeExceptionInfo("Can not write: " + originalFile.getAbsolutePath(), version, e);
                }
                // Test ends here
            }
        }
    }

    private boolean compareEvidenceCases(List<EvidenceCase> evidenceCases1, List<EvidenceCase> evidenceCases2) {
        boolean equals = true;
        // TODO
        return equals;
    }

    private boolean compareEvidence(EvidenceCase ev1, EvidenceCase ev2) {
        boolean equals = true;
        // TODO
        return equals;
    }

    */
/**
     *
     * @param pr1
     * @param pr2
     * @return True if networks are equal
     *//*

    private boolean compareNetworks(ProbNet pr1, ProbNet pr2) {
        boolean equals = true;
        equals &= compareMiscelanea(pr1, pr2);
        equals &= compareConstraints(pr1, pr2);
        equals &= compareVariables(pr1, pr2);
        equals &= compareLinks(pr1, pr2);
        equals &= comparePotentials(pr1, pr2);
        return equals;
    }

    private boolean compareMiscelanea(ProbNet pr1, ProbNet pr2) {
        boolean equals = true;
        equals &= compareStrings(pr1.getName(), pr2.getName());
        equals &= compareStrings(pr1.getComment(), pr2.getComment());
        equals &= compareCycleLength(pr1.getCycleLength(), pr2.getCycleLength());
        equals &= compareHashMapsStrings(pr1.additionalProperties, pr2.additionalProperties);
        equals &= compareAgents(pr1.getAgents(), pr2.getAgents());
        return equals;
    }

    private boolean compareAgents(List<StringWithProperties> agents1, List<StringWithProperties> agents2) {
        boolean equals = true;
        equals &= (agents1 == null && agents2 == null) || (agents1 != null && agents2 != null && agents1.size() == agents2.size());
        if (equals && agents1 != null) {
            int size = agents1.size();
            // Asumption that contents are in the same order in both lists
            for (int i = 0; i < size; i++) {
                StringWithProperties stringWithProperties1 = agents1.get(i);
                StringWithProperties stringWithProperties2 = agents2.get(i);

            }
        }
        return equals;
    }

    */
/**
     * Compares two HashMaps key = String,value = String
     * @param map1
     * @param map2
     * @return
     *//*

    private boolean compareHashMapsStrings(HashMap<String,String> map1, HashMap<String,String> map2) {
        boolean equals = true;
        equals &= (map1 == null && map2 == null) || (map1 != null && map2 != null);
        if (equals && map1 != null) {
            Set<String> set1 = map1.keySet();
            Set<String> set2 = map1.keySet();
            equals &= set1.size() == set2.size();
            if (equals) {
                for (String key : set1) {
                    if (set2.contains(key)) {
                        String string1 = map1.get(key);
                        String string2 = map2.get(key);
                        equals &= string1 != null ? string1.matches(string2) : string2 == null;
                    } else {
                        equals = false;
                    }
                    if (!equals) break;
                }
            }
        }
        return equals;
    }

    private boolean compareCycleLength(CycleLength cycleLength1, CycleLength cycleLength2) {
        boolean equals = true;
        equals &= (cycleLength1 == null && cycleLength2 == null) || (cycleLength1 != null && cycleLength2 != null);
        equals &= cycleLength1 != null && cycleLength1.getUnit() == cycleLength2.getUnit();
        equals &= cycleLength1 != null && cycleLength1.getValue() == cycleLength2.getValue();
        return equals;
    }

    private boolean compareConstraints(ProbNet pr1, ProbNet pr2) {
        boolean equals = true;
        // TODO
        return equals;
    }

    private boolean compareVariables(ProbNet pr1, ProbNet pr2) {
        boolean equals = true;
        // TODO
        return equals;
    }

    private boolean compareLinks(ProbNet pr1, ProbNet pr2) {
        boolean equals = true;
        // TODO
        return equals;
    }

    private boolean comparePotentials(ProbNet pr1, ProbNet pr2) {
        boolean equals = true;
        // TODO
        return equals;
    }

    */
/**
     * Compare two strings that can be null
     * @param name1
     * @param name2
     * @return
     *//*

    private boolean compareStrings(String name1, String name2) {
        boolean equals = true;
        equals &= (name1 == null && name2 == null) || (name1 != null && name2 != null);
        equals &= name1 != null && name1.matches(name2);
        return equals;
    }

    */
/**
     * Checks if a string is contained in an array of strings
     * @param netName
     * @return
     *//*

    private boolean only0_5(String netName) {
        boolean only0_5 = false;
        for (int i = 0; i < networksOnly0_5.length && !only0_5; i++) {
            only0_5 = networksOnly0_5[i].matches(netName);
        }
        return only0_5;
    }

    private void writeExceptionInfo(String message, String version, Exception e) {
        System.err.println(message);
        if (version != null) {
            System.err.println("Version: " + version);
        }
        System.err.println(e.getMessage());
        System.err.println(e.getStackTrace());
    }

    private String getNewPath(String absolutePathOld, String version) {
        return pathToNewFiles + File.separator + version + File.separator + absolutePathOld.substring(pathToTestFiles.length());
    }

    // Methods

    */
/**
     * Read networks from directory 'pathToTestFiles' and writes them in 0.2 and 0.7 version in
     * 'pathToNewFiles/0.2' and 'pathToNewFiles/0.7'.
     *//*

    public void testConversionBetweenVersions() throws IOException {
        String pathToNewFiles02 = pathToNewFiles + "/0_2";
        String pathToNewFiles07 = pathToNewFiles + "/0_7";
        cleanTestFoldersTree(pathToTestFiles, pathToNewFiles02, pathToNewFiles07);
        createTestFolders(pathToNewFiles02, pathToNewFiles07);

        File testNetsDirectory = new File(pathToTestFiles);
        File[] testNetsFiles = testNetsDirectory.listFiles();
        int numCharsPathToNewFiles = pathToNewFiles.length();
        for (File testFile : testNetsFiles) {
            if (testFile.isDirectory()) {
                // TODO
            } else {
                PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
                ProbNetInfo probNetInfo = null;
                InputStream networkStream = getClass().getClassLoader().getResourceAsStream(testFile.getAbsolutePath());
                try {
                    probNetInfo = pgmxReader.loadProbNetInfo(testFile.getAbsolutePath(), networkStream);
                } catch (ParserException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    */
/** List recursively files in PGMX version and writes its directory, version and name. *//*

    public void writeTreeFiles(String pathToFiles) throws Exception {
        File directory = new File(pathToFiles);
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                String canonicalPath = file.getCanonicalPath();

                try {
                    FileInputStream stream = new FileInputStream(canonicalPath);
                    // Get root element.
                    SAXBuilder builder = new SAXBuilder();
                    builder.setJDOMFactory( new LocatedJDOMFactory() );
                    Document document = null;
                    try {
                        document = builder.build(stream);
                    } catch ( JDOMException e ) {
                        throw new ParserException( "Can not parse XML document " + canonicalPath + ":" + e.getMessage() );
                    } catch ( IOException e ) {
                        throw new ParserException( "Error trying to open " + canonicalPath + ".\n" + e.getMessage() );
                    }
                    Element root = document.getRootElement();
                    String strVersion = root.getAttributeValue( XMLAttributes.FORMAT_VERSION.toString() );
                    System.out.print(strVersion + " ");
                    System.out.println(canonicalPath);
                }
                catch (FileNotFoundException e ) {
                    System.err.println("The " + canonicalPath + " does not exists.");
                }
            } else if (file.isDirectory()) {
                String absolutePat = file.getAbsolutePath();
                System.out.println();
                System.out.println(file.getAbsolutePath());
                for (int i = 0; i < absolutePat.length(); i++) {
                    System.out.print("-");
                }
                System.out.println();
            }
        }
    }

    private void replicateOriginFolderStructureInOtherFolder(File originFolder, File newFolder) {
        File[] subOriginFiles = originFolder.listFiles();
        for (File subOriginFolderFile : subOriginFiles) {
            if (subOriginFolderFile.isDirectory()) {
                String newSubFolderString = newFolder.getAbsolutePath() + originFolder.getAbsolutePath().substring((int)originFolder.length());
                File newSubFolder = new File(newSubFolderString);
                newSubFolder.mkdir();
                replicateOriginFolderStructureInOtherFolder(subOriginFolderFile, newSubFolder);
            }
        }
    }

    */
/** Replicates from pathToTestFiles a tree of new files in pathToNewFiles/0.2 and pathToNewFiles/0.7. *//*

    private void createTestFolders(String pathToTestFiles, String pathToNewFiles) {
        String pathToNewFiles02 = pathToNewFiles + "/0_2";
        String pathToNewFiles07 = pathToNewFiles + "/0_7";
        File newFiles02 = new File(pathToNewFiles02);
        File newFiles07 = new File(pathToNewFiles07);
        File originalFiles = new File(pathToTestFiles);
        try {
            // Remove folders from previous tests
            removeContentsFolder(newFiles02);
            removeContentsFolder(newFiles07);
            // Create new folders
            newFiles02.createNewFile();
            replicateOriginFolderStructureInOtherFolder(originalFiles, newFiles02);
            newFiles07.createNewFile();
            replicateOriginFolderStructureInOtherFolder(originalFiles, newFiles07);
        } catch (IOException e) {
            System.err.println("Can not create new folder.\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    */
/**
     * Creates folders to write files for testing. If the folders exist from previous tests, removes contents.
     * @param pathToNewFiles
     * @param pathToNewFiles02
     * @param pathToNewFiles07
     * @throws IOException
     *//*

    private void cleanTestFoldersTree(String pathToNewFiles, String pathToNewFiles02, String pathToNewFiles07) throws IOException {
        // Create test directories if they do not exists
        File newFiles = new File(pathToNewFiles);
        newFiles.mkdir(); // Create path; if file already exists do nothing.

        // Create test folders for 0.2 and 0.7 and remove previous contents
        File newFiles02 = new File(pathToNewFiles02);
        newFiles02.mkdir();
        removeContentsFolder(newFiles02);

        File newFiles07 = new File(pathToNewFiles07);
        newFiles07.mkdir();
        removeContentsFolder(newFiles07);
    }

    */
/**
     * Remove recursively all the files and folders of 'folder'
     * @param folder
     *//*

    private void removeContentsFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    removeContentsFolder(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    */
/**
     * Gets the version of a PGMX file
     * @param pgmxFile
     * @return
     *//*

    private String getVersion(File pgmxFile) throws ParserException {
        PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
        String absolutePath = pgmxFile.getAbsolutePath();
        InputStream networkStream = getClass().getClassLoader().getResourceAsStream(absolutePath);
        return pgmxReader.getVersion(absolutePath, networkStream);
    }

    */
/**
     * Sets the values of the variables pathToTestFiles and pathToNewFiles
     * @param paths
     * @return
     *//*

    private void setPaths(String[] paths) {
        pathToTestFiles = null;
        if (paths.length >=1 ) {
            pathToTestFiles = paths[0];
        } else {
            pathToTestFiles = defaultPpathToTestFiles;
        }
        if (paths.length >= 2) {
            pathToNewFiles = paths[1];
        } else {
            pathToNewFiles = defaultPathToNewFiles;
        }
    }

    // Auxiliar clases and interfaces

    private interface Next {
        File next();
        boolean hasNext();
    }

    private interface StringFilter {
        boolean matchCondition(String string);
    }

    private class PGMXFiles implements StringFilter {
        private final String PGMX_FILES = ".PGMX";

        @Override
        public boolean matchCondition(String string) {
            return string.toUpperCase().endsWith(PGMX_FILES);
        }
    }

    private class FileIterator implements Next {

        // Attributes
        private List<File[]> subNodes;
        private List<Integer> subNodesIndexes;
        private boolean hasNext;
        private File next;

        private List<StringFilter> filters;

        // Constructor
        */
/**
         * Constructor
         * @param rootFolder
         *//*

        public FileIterator(File rootFolder, List<StringFilter> filters) {
            this.filters = filters;
            hasNext = false;
            next = null;

            if (rootFolder.isDirectory()) {
                subNodes = new ArrayList<File[]>();
                File[] rootChildren = rootFolder.listFiles();
                subNodes.add(rootChildren);

                subNodesIndexes = new ArrayList<Integer>();
                subNodesIndexes.add(-1);

                next = lookForNext();
            } else {
                hasNext = matches(rootFolder);
                next = hasNext ? rootFolder : null;
            }
        }

        public boolean hasNext() {
            return hasNext;
        }

        public File next() {
            File aux = next;
            next = next == null ? null : lookForNext();
            return aux;
        }

        */
/**
         * Looks for next file in the tree and updates <code>hasNext</code>
         * @return Next File element
         *//*

        private File lookForNext() {
            int treeDepth = subNodes == null ? 0 : subNodes.size();
            if (treeDepth == 0) {
                hasNext = false;
                next = null;
            } else {
                int treeDepthMinusOne = treeDepth - 1;
                File[] deepestNodes = subNodes.get(treeDepthMinusOne);
                int deepestIndexesNode = subNodesIndexes.get(treeDepthMinusOne);
                subNodesIndexes.set(treeDepthMinusOne, ++deepestIndexesNode);
                if (deepestNodes == null || deepestNodes.length == 0 || (deepestIndexesNode + 1) > deepestNodes.length) {// Empty sub folder or finished folder
                    subNodes.remove(treeDepthMinusOne);
                    subNodesIndexes.remove(treeDepthMinusOne);
                    next = lookForNext();
                } else {
                    File lastFile = deepestNodes[deepestIndexesNode];
                    if (lastFile.isDirectory()) {
                        subNodes.add(lastFile.listFiles());
                        subNodesIndexes.add(-1);
                        next = lookForNext();
                    } else {
                        hasNext = true;
                        next = lastFile;
                    }
                }
            }
            next = (next == null) ? next : matches(next) ? next : lookForNext();
            return next;
        }

        private boolean matches(File file) {
            boolean match = true;
            for (StringFilter filter : filters) {
                match &= filter.matchCondition(file.getName());
            }
            return match;
        }

    }
*/

}
