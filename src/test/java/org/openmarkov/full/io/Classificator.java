package org.openmarkov.full.io;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;
import org.openmarkov.io.probmodel.strings.XMLAttributes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class Classificator extends PGMXReader_0_2 {

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

    // Constructor
    /** This class performs several operations with files in PGMX format.
     * @param paths Optional String[] parameter. paths[0] = path to files; paths[1] = path to new files. */
    public Classificator(String[] paths) throws Exception {
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
        File fileToPathToTestFiles = new File(pathToNewFiles);
        if (!fileToPathToTestFiles.exists() || !fileToPathToTestFiles.isDirectory()) {
            throw new Exception("No test path.");
        } else {
            File testNodeFile = new File(pathToTestFiles);
            StringFilter filter = new PGMXFiles();
            List<StringFilter> filters = new ArrayList<StringFilter>(1);
            filters.add(new PGMXFiles());
            for (FileIterator iterator = new FileIterator(testNodeFile, filters); iterator.hasNext(); ) {
                File file = iterator.next();
                String version = getVersion(file);
                PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
                InputStream networkStream = getClass().getClassLoader().getResourceAsStream(file.getAbsolutePath());
                ProbNetInfo probNetInfo = pgmxReader.loadProbNetInfo(file.getAbsolutePath(), networkStream);
                ProbNet originalProbNet = probNetInfo.getProbNet();

                // Test begins here

                // Test ends here
            }
        }
    }

    // Methods

    /**
     * Read networks from directory 'pathToTestFiles' and writes them in 0.2 and 0.7 version in
     * 'pathToNewFiles/0.2' and 'pathToNewFiles/0.7'.
     */
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

    /** List recursively files in PGMX version and writes its directory, version and name. */
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

    /** Replicates from pathToTestFiles a tree of new files in pathToNewFiles/0.2 and pathToNewFiles/0.7. */
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

    /**
     * Creates folders to write files for testing. If the folders exist from previous tests, removes contents.
     * @param pathToNewFiles
     * @param pathToNewFiles02
     * @param pathToNewFiles07
     * @throws IOException
     */
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

    /**
     * Remove recursively all the files and folders of 'folder'
     * @param folder
     */
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

    /**
     * Gets the version of a PGMX file
     * @param pgmxFile
     * @return
     */
    private String getVersion(File pgmxFile) throws ParserException {
        PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
        String absolutePath = pgmxFile.getAbsolutePath();
        InputStream networkStream = getClass().getClassLoader().getResourceAsStream(absolutePath);
        return pgmxReader.getVersion(absolutePath, networkStream);
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
        /**
         * Constructor
         * @param rootFolder
         */
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

        /**
         * Looks for next file in the tree and updates <code>hasNext</code>
         * @return Next File element
         */
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

}
