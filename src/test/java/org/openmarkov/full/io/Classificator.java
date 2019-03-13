package org.openmarkov.full.io;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.inference.InferenceOptions;
import org.openmarkov.core.inference.MulticriteriaOptions;
import org.openmarkov.core.inference.TemporalOptions;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.ProbNetWriter;
import org.openmarkov.core.model.graph.Link;
import org.openmarkov.core.model.network.*;
import org.openmarkov.core.model.network.constraint.PNConstraint;
import org.openmarkov.core.model.network.modelUncertainty.ProbDensFunction;
import org.openmarkov.core.model.network.modelUncertainty.UncertainValue;
import org.openmarkov.core.model.network.potential.*;
import org.openmarkov.core.model.network.potential.canonical.ICIPotential;
import org.openmarkov.core.model.network.potential.canonical.MinMaxPotential;
import org.openmarkov.core.model.network.potential.treeadd.Threshold;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDBranch;
import org.openmarkov.core.model.network.potential.treeadd.TreeADDPotential;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;
import org.openmarkov.io.probmodel.strings.XMLAttributes;
import org.openmarkov.io.probmodel.writer.PGMXWriter_0_2;
import org.openmarkov.io.probmodel.writer.PGMXWriter_0_5;

import java.io.*;
import java.util.*;

/**
 * @author Manuel Arias
 */
public class Classificator extends PGMXReader_0_2 {


    public static void main(String args[]) {
        try {
            Classificator classificator = new Classificator(args);
            classificator.testConversionBetweenVersions();
            classificator.performTests();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    // Attributes
    private static final String defaultPpathToTestFiles="/home/manuel/Redes/OriginalNetworks";
    private static final String defaultPathToNewFiles = "/home/manuel/Redes/NewNetworks";

    private final String V0_2 = "0.2.0";
    private final String V0_7 = "0.7.0";
    private final String advancedFeaturesFile = "Networks with advanced features.txt";

    private String pathToTestFiles;
    private String pathToNewFiles;

    private List<String> networksWithAdvancedFeatures;

    // Constructor
    /** This class performs several operations with files in PGMX format.
     * @param paths Optional String[] parameter. paths[0] = path to files; paths[1] = path to new files. */

    public Classificator(String[] paths) throws IOException {
        // Sets path to test files and new files
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

        networksWithAdvancedFeatures = getNetworksWithAdvancedFeatures(pathToTestFiles);
    }

    private List<String> getNetworksWithAdvancedFeatures(String pathToTestFiles) {
        List<String> networksWithAdvancedFeatures = new ArrayList<String>();
        String fileName = pathToTestFiles + File.separator + advancedFeaturesFile;
        File pathToAdvancedFeaturesFile = new File(fileName);
        if (pathToAdvancedFeaturesFile.exists()) {
            BufferedReader buffer = null;
            try {
                buffer = new BufferedReader(new FileReader(pathToAdvancedFeaturesFile));
            } catch (FileNotFoundException e) {
                writeExceptionInfo("Can not open for reading file: " + fileName, null, e);
            }
            String string;
            try {
                while ((string = buffer.readLine()) != null) {
                    networksWithAdvancedFeatures.add(string);
                }
            } catch (IOException e) {
                writeExceptionInfo("IOException while reading file: " + fileName, null, e);
            }
        }
        return networksWithAdvancedFeatures;
    }

    private void writeNetworksWithAdvancedFeatures(String patoToTestFiles) {
        String fileName = pathToTestFiles + File.separator + advancedFeaturesFile;
        BufferedWriter writer = null;
        try {
            //create a temporary file
            File file = new File(fileName);
            writer = new BufferedWriter(new FileWriter(file));
            for (String networkName : networksWithAdvancedFeatures) {
                writer.write(networkName);
            }
        } catch (Exception e) {
            System.out.println("Can not write networks with advanced features.");
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }

    // Methods
    private void performTests() {
        int differentNetworks = 0;
        int differentNetworks0_2 = 0;
        int differentNetworks0_7 = 0;
        int numNetworks = 0;
        File filePathToNewFiles = null;
        try {
            filePathToNewFiles = getPathToNewFiles(pathToNewFiles);
        } catch (IOException e) {
            System.out.println("Can not read directory to test files.");
            System.exit(-1);
        }
        File testNodeFile = new File(pathToTestFiles);
        List<StringFilter> filters = getPGMXFilters();
        for (IteratorPGMX iterator = new FileIterator(testNodeFile, filters); iterator.hasNext(); ) {
            File originalFile = iterator.next();
            numNetworks++;
            String originalFileName = originalFile.getAbsolutePath();
            String version = null;
            try {
                version = getVersion(originalFile);
            } catch (ParserException e) {
                writeExceptionInfo("Can not read file: " + originalFileName, null, e);
                continue;
            }
            PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
            InputStream networkStream = null;
            try {
                networkStream = new FileInputStream(originalFileName);
            } catch (FileNotFoundException e) {
                System.out.println("Can not read file " + originalFileName);
                continue;
            }
            ProbNetInfo probNetInfo = null;
            try {
                probNetInfo = pgmxReader.loadProbNetInfo(originalFileName, networkStream);
            } catch (ParserException e) {
                e.printStackTrace();
            }
            ProbNet originalProbNet = probNetInfo.getProbNet();
            List<EvidenceCase> originalEvidenceCases = probNetInfo.getEvidence();

            // Write and read probNetInfo in versions 0.2 and 0.7.
            String pathToNewFile0_2 = getNewPath(originalFileName, V0_2);
            boolean canBeWrittenIn0_2 = version.matches(V0_2) || !networkNameIsIncludedInListOfAdvancedFeatures(pathToNewFile0_2);
            boolean canBeWrittenIn0_5 = false;
            boolean canBeReaded;
            ProbNetInfo probNetInfo02_bis = null;
            if (canBeWrittenIn0_2) {
                // Write 0.2
                ProbNetWriter writer02 = new PGMXWriter_0_2();
                canBeReaded = true;
                try {
                    writer02.writeProbNet(pathToNewFile0_2, originalProbNet, originalEvidenceCases);
                    // Read 0.2
                    InputStream networkStream02_bis = new FileInputStream(pathToNewFile0_2);
                    probNetInfo02_bis = pgmxReader.loadProbNetInfo(pathToNewFile0_2, networkStream02_bis);
                } catch (WriterException e) {
                    canBeWrittenIn0_2 = false;
                    writeExceptionInfo("Error writing " + originalFileName, V0_2, e);
                } catch (ParserException e) {
                    canBeReaded = false;
                    writeExceptionInfo("Error reading " + originalFileName, V0_2, e);
                } catch (FileNotFoundException e) {
                    System.out.println("Can not read file " + pathToNewFile0_2);
                    continue;
                }
            }
            if (pathToNewFile0_2.contains("BN-catarnet-05.pgmx")) {
                System.out.println("Diff");
            }
            if ((probNetInfo02_bis != null) && (!sameInfoProbNetsInfo(probNetInfo, probNetInfo02_bis))) {
                System.out.println("Different networks in V0_2 " + originalFileName);
                differentNetworks0_2++;
            }

            String pathToNewFile0_7 = getNewPath(originalFile.getAbsolutePath(), V0_7);
            ProbNetWriter writer05 = new PGMXWriter_0_5();
            try {
                writer05.writeProbNet(pathToNewFile0_7, originalProbNet, originalEvidenceCases);
                canBeWrittenIn0_5 = true;
            } catch (WriterException e) {
                networksWithAdvancedFeatures.add(originalFile.getName());
                canBeWrittenIn0_2 = false;
                writeExceptionInfo("Error writing "+ pathToNewFile0_7, V0_7, e);
            }

            // Read the probNetInfo recently written in both versions.
            if (canBeWrittenIn0_5) {
                PGMXReader_0_2 pgmxReader0_5 = new PGMXReader_0_2();
                InputStream networkStream0_5 = null;
                ProbNetInfo probNetInfo0_5 = null;
                try {
                    networkStream0_5 = new FileInputStream(pathToNewFile0_7);
                    probNetInfo0_5 = pgmxReader.loadProbNetInfo(originalFileName, networkStream0_5);
                } catch (ParserException e) {
                    writeExceptionInfo("Error reading " + originalFileName, V0_7, e);
                } catch (FileNotFoundException e) {
                    System.out.println("Can not read file " + pathToNewFile0_7);
                    continue;
                }
                if ((probNetInfo0_5 != null) && (!sameInfoProbNetsInfo(probNetInfo, probNetInfo0_5))) {
                    System.out.println("Different networks in V0_5 " + originalFileName);
                    differentNetworks0_7++;
                }

            }
            // Compare the contents with the original probNetInfo.
            // Report differences for each network and write message
            // Test ends here
        }
        if (!networksWithAdvancedFeatures.isEmpty()) {
            System.out.println("Networks with advanced features: ");
            for (String networkName : networksWithAdvancedFeatures) {
                System.out.println(networkName);
            }
            writeNetworksWithAdvancedFeatures(pathToTestFiles);
        }
        differentNetworks = differentNetworks0_2 + differentNetworks0_7;
        System.out.println("Total networks: " + numNetworks);
        if (differentNetworks != 0) {
            if (differentNetworks0_2 != 0) {
                System.out.println("Total different networks Version 0.2 = " + differentNetworks0_2);
            }
            if (differentNetworks0_7 != 0) {
                System.out.println("Total different networks Version 0.7 = " + differentNetworks0_7);
            }
            System.out.println("Total different networks = " + differentNetworks);
        }
    }

    private List<StringFilter> getPGMXFilters() {
        List<StringFilter> filters = new ArrayList<>(1);
        filters.add(new PGMXFiles());
        return filters;
    }

    private File getPathToNewFiles(String pathToNewFiles) throws IOException {
        File filePathToNewFiles = new File(pathToNewFiles);
        if (!filePathToNewFiles.exists() || !filePathToNewFiles.isDirectory()) {
            throw new IOException("No test path.");
        }
        return filePathToNewFiles;
    }

    /**
     * Checks if a string is contained in an array of strings
     * @param netName
     * @return
     */
    private boolean networkNameIsIncludedInListOfAdvancedFeatures(String netName) {
        boolean advancedFeatures = false;
        int size = networksWithAdvancedFeatures.size();
        int i;
        for (i = 0; i < size && !networksWithAdvancedFeatures.get(i).contains(netName); i++);
        return i != size;
    }

    private void writeExceptionInfo(String message, String version, Exception e) {
        System.err.println(message);
        if (version != null) {
            System.err.println("Version: " + version);
        }
        System.err.println(e.getMessage());
        System.err.println(e.getStackTrace());
    }

    /**
     * Read networks from directory 'pathToTestFiles' and writes them in 0.2 and 0.7 version in
     * 'pathToNewFiles/0.2' and 'pathToNewFiles/0.7'.
     */
    public void testConversionBetweenVersions() throws IOException {
        String pathToNewFiles02 = pathToNewFiles + File.separator + V0_2;
        String pathToNewFiles07 = pathToNewFiles + File.separator + V0_7;
        cleanTestFoldersTree(pathToTestFiles, pathToNewFiles02, pathToNewFiles07);
        createTestFolders(pathToTestFiles, pathToNewFiles02, pathToNewFiles07);

/*
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
*/
    }

    private String getNewPath(String absolutePathOld, String version) {
        return pathToNewFiles + File.separator + version + absolutePathOld.substring(pathToTestFiles.length());
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
                String newSubFolderString = newFolder.getAbsolutePath() + File.separator +
                        subOriginFolderFile.getAbsolutePath().substring(originFolder.getAbsolutePath().length() + 1);
                File newSubFolder = new File(newSubFolderString);
                newSubFolder.mkdir();
                replicateOriginFolderStructureInOtherFolder(subOriginFolderFile, newSubFolder);
            }
        }
    }

    /** Replicates from pathToTestFiles a tree of new files in pathToNewFiles/0.2 and pathToNewFiles/0.7. */
    private void createTestFolders(String pathToTestFiles, String pathToNewFiles0_2, String pathToNewFiles0_7) {
        File originalFiles = new File(pathToTestFiles);
        try {
            // Remove folders from previous tests
            File new0_2 = new File(pathToNewFiles0_2);
            File new0_7 = new File(pathToNewFiles0_7);
            removeContentsFolder(new0_2);
            removeContentsFolder(new0_7);
            // Create new folders
            new0_2.createNewFile();
            replicateOriginFolderStructureInOtherFolder(originalFiles, new0_2);
            new0_7.createNewFile();
            replicateOriginFolderStructureInOtherFolder(originalFiles, new0_7);
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
        InputStream networkStream = null;
        try {
            networkStream = new FileInputStream(absolutePath);
        } catch (FileNotFoundException e) {
            throw new ParserException("File: " + absolutePath + " does not exists.");
        }
        return pgmxReader.getVersion(absolutePath, networkStream);
    }

    // Methods to compare data structures.
    /**
     *
     * @param probNetInfo1
     * @param probNetInfo2
     * @return
     */
    private boolean sameInfoProbNetsInfo(ProbNetInfo probNetInfo1, ProbNetInfo probNetInfo2) {
        boolean bothNotNull = probNetInfo1 != null && probNetInfo2 != null;
        boolean bothNull = probNetInfo1 == null && probNetInfo2 == null;
        return bothNull ||
                   ( bothNotNull &&
                     sameInfoListsOfEvidencecases(probNetInfo1.getEvidence(), probNetInfo2.getEvidence()) &&
                     sameInfoProbNets(probNetInfo1.getProbNet(), probNetInfo2.getProbNet()) );
    }

    /**
     * @param probNet1
     * @param probNet2
     * @return True if networks are equal
     */
    private boolean sameInfoProbNets(ProbNet probNet1, ProbNet probNet2) {
        boolean bothNotNull = probNet1 != null && probNet2 != null;
        boolean bothNull = probNet1 == null && probNet2 == null;
        return bothNull ||
                   ( bothNotNull &&
                     sameInfoMiscelanea(probNet1, probNet2) &&
                     sameInfoListOfConstraints(probNet1, probNet2) &&
                     sameInfoListOfVariables(probNet1, probNet2) &&
                     sameInfoListOfLinks(probNet1, probNet2) &&
                     sameInfoListOfPotentials(probNet1, probNet2) );
    }

    /**
     * Compares list of evidence cases
     * @param evidenceCases1
     * @param evidenceCases2
     * @return
     */
    private boolean sameInfoListsOfEvidencecases(List<EvidenceCase> evidenceCases1, List<EvidenceCase> evidenceCases2) {
        boolean bothNotNull = evidenceCases1 != null && evidenceCases2 != null;
        boolean bothNull = evidenceCases1 == null && evidenceCases2 == null;
        boolean same = bothNull || bothNotNull;
        if (bothNotNull) {
            int size = evidenceCases1.size();
            if (size == evidenceCases2.size()) {
                int i;
                for (i = 0; i < size && sameInfoEvidenceCases(evidenceCases1.get(i), evidenceCases2.get(i)); i++);
                same = i == size;
            } else {
                same = false;
            }
        }
        return same;
    }

    /**
     *
     * @param evidenceCase1
     * @param evidenceCase2
     * @return
     */
    private boolean sameInfoEvidenceCases(EvidenceCase evidenceCase1, EvidenceCase evidenceCase2) {
        int numberOfFindings = evidenceCase1.getNumberOfFindings();
        boolean same = numberOfFindings == evidenceCase2.getNumberOfFindings();
        if (same) {
            List<Variable> variables1 = evidenceCase1.getVariables(); // Number of findings == number of variables
            for (int i = 0; i < numberOfFindings && same; i++) {
                Finding findingVariable1 = evidenceCase1.getFinding(variables1.get(i)); // Always not null
                Finding findingVariable2 = evidenceCase2.getFinding(variables1.get(i));  // Same variables in both evidence case, otherwise findingVariable2 == null
                same = findingVariable2 != null &&
                         findingVariable1.getState() == findingVariable2.getState() &&
                         findingVariable1.getStateIndex() == findingVariable2.getStateIndex() &&
                         findingVariable1.getNumericalValue() == findingVariable2.getNumericalValue();
            }
        }
        return same;
    }

    /**
     * Comparison of several data that is not variables, links or potentials
     * @param pr1
     * @param pr2
     * @return
     */
    private boolean sameInfoMiscelanea(ProbNet pr1, ProbNet pr2) {
        return sameInfoStrings(pr1.getName(), pr2.getName()) &&
               sameInfoStrings(pr1.getComment(), pr2.getComment()) &&
               sameInfoCycleLength(pr1.getCycleLength(), pr2.getCycleLength()) &&
               sameInfoMapsStrings(pr1.additionalProperties, pr2.additionalProperties) &&
               sameInfoAgents(pr1.getAgents(), pr2.getAgents()) &&
               sameInfoInferenceOptions(pr1.getInferenceOptions(), pr2.getInferenceOptions());
    }

    private boolean sameInfoInferenceOptions(InferenceOptions inferenceOptions1, InferenceOptions inferenceOptions2) {
        return inferenceOptions1.discountRate == inferenceOptions2.discountRate &&
               sameInfoVariables(inferenceOptions1.simulationIndexVariable, inferenceOptions2.simulationIndexVariable) &&
               sameInfoMultiCriteriaOptions(inferenceOptions1.getMultiCriteriaOptions(), inferenceOptions2.getMultiCriteriaOptions()) &&
               sameInfoTemporalOptions(inferenceOptions1.getTemporalOptions(), inferenceOptions2.getTemporalOptions());
    }

    private boolean sameInfoTemporalOptions(TemporalOptions temporalOptions1, TemporalOptions temporalOptions2) {
        return temporalOptions1.getHorizon() == temporalOptions2.getHorizon() &&
               temporalOptions1.getTransition() == temporalOptions2.getTransition();
    }

    private boolean sameInfoMultiCriteriaOptions(MulticriteriaOptions multicriteria1, MulticriteriaOptions multicriteria2) {
        return multicriteria1.isCeOptionsShowed() == multicriteria2.isCeOptionsShowed() &&
               multicriteria1.isUnicriterionOptionsShowed() == multicriteria2.isCeOptionsShowed() &&
               sameInfoStrings(multicriteria1.getMainUnit(), multicriteria2.getMainUnit()) &&
               multicriteria1.getMulticriteriaType() == multicriteria2.getMulticriteriaType();
    }

    /**
     * Compares the contents of two list of agents, information must be in the same order in both lists.
     * @param agents1
     * @param agents2
     * @return true if both lists are equal
     */
    private boolean sameInfoAgents(List<StringWithProperties> agents1, List<StringWithProperties> agents2) {
        boolean bothNotNull = agents1 != null && agents2 != null;
        boolean same = bothNotNull || (agents1 == null && agents2 == null);
        if (bothNotNull) {
            int size = agents1.size();
            // Asumption that contents are in the same order in both lists
            if (size == agents2.size()) {
                int i;
                for (i = 0; i < size && sameInfoStringsWithProperties(agents1.get(i), agents2.get(i)); i++);
                same = i == size;
            } else {
                same = false;
            }
        }
        return same;
    }

    /**
     *
     * @param cycleLength1
     * @param cycleLength2
     * @return
     */
    private boolean sameInfoCycleLength(CycleLength cycleLength1, CycleLength cycleLength2) {
        boolean bothNotNull = cycleLength1 != null && cycleLength2 != null;
        boolean bothNull = cycleLength1 == null && cycleLength2 == null;
        return bothNull ||
               ( bothNotNull &&
                 cycleLength1.getUnit() == cycleLength2.getUnit() &&
                 cycleLength1.getValue() == cycleLength2.getValue() );
    }

    /**
     * Compare two list of constraints, that include the constraints
     * @param probNet1
     * @param probNet2
     * @return
     */
    private boolean sameInfoListOfConstraints(ProbNet probNet1, ProbNet probNet2) {
        List<PNConstraint> constraints1 = probNet1.getConstraints();
        List<PNConstraint> constraints2 = probNet2.getConstraints();
        boolean bothNull = constraints1 == null && constraints2 == null;
        boolean bothNotNull = constraints1 != null && constraints2 != null;
        boolean same = bothNull || bothNotNull;
        if (bothNotNull) {
            int size = constraints1.size();
            if (size == constraints2.size()) {
                int i;
                for (i = 0; i < size && constraints1.get(i).getClass() == constraints2.get(i).getClass(); i++);
                same = i == size;
            } else {
                same = false;
            }
        }
        return same;
    }

    /**
     *
     * @param probNet1
     * @param probNet2
     * @return
     */
    private boolean sameInfoListOfVariables(ProbNet probNet1, ProbNet probNet2) {
        List<Variable> variables1 = probNet1.getVariables();
        List<Variable> variables2 = probNet2.getVariables();
        int size = variables1.size();
        boolean same;
        if (size == variables2.size()) {
            int i;
            for (i = 0; i < size && sameInfoVariables(variables1.get(i), variables2.get(i)); i++);
            same = i == size;
        } else {
            same = false;
        }
        return same;
    }

    /**
     * Compares the contents of two variables.
     * @param variable1
     * @param variable2
     * @return
     */
    private boolean sameInfoVariables(Variable variable1, Variable variable2) {
        boolean bothVariablesNotNull = variable1 != null && variable2 != null;
        boolean bothVariablesNull = variable1 == null && variable2 == null;

        boolean same = bothVariablesNull || (bothVariablesNotNull && sameInfoStrings(variable1.getName(), variable2.getName()));
        if (same && bothVariablesNotNull) {
            same = sameInfoListOfStates(Arrays.asList(variable1.getStates()), Arrays.asList(variable2.getStates())) &&
                   sameInfoStringsWithProperties(variable1.getAgent(), variable2.getAgent()) &&
                   sameInfoStringsWithProperties(variable1.getUnit(), variable2.getUnit()) &&
                   variable1.getPrecision() == variable2.getPrecision() &&
                   variable1.getTimeSlice() == variable2.getTimeSlice() &&
                   variable1.isTemporal() == variable2.isTemporal() &&
                   variable1.getVariableType() == variable2.getVariableType();
            if (same) {
                PartitionedInterval interval1 = variable1.getPartitionedInterval();
                PartitionedInterval interval2 = variable2.getPartitionedInterval();
                boolean bothIntervalsNotNull = interval1 != null && interval2 != null;
                boolean bothIntervalsNull = interval1 == null && interval2 == null;
                same = bothIntervalsNull ||
                        ( bothIntervalsNotNull && sameInfoPartitionedIntervals(interval1, interval2) &&
                          variable1.getDecisionCriterion() == variable2.getDecisionCriterion() &&
                          variable1.getTimeSlice() == variable2.getTimeSlice() );
            }
        }
        return same;
    }

    private boolean sameInfoPartitionedIntervals(PartitionedInterval interval1, PartitionedInterval interval2) {
        boolean bothNull = interval1 == null && interval2 == null;
        boolean bothNotNull = interval1 != null && interval2 != null;
        return bothNull ||
                ( bothNotNull && interval1.getMin() == interval2.getMin() &&
                  interval1.getMax() == interval2.getMax() &&
                  interval1.getNumSubintervals() == interval2.getNumSubintervals() &&
                  sameInfoArraysOfBooleans(interval1.getBelongsToLeftSide(), interval2.getBelongsToLeftSide()) &&
                  sameInfoArrayOfDoubles(interval1.getLimits(), interval2.getLimits()) );
    }

    private boolean sameInfoArraysOfBooleans(boolean[] booleans1, boolean[] booleans2) {
        boolean bothNull = booleans1 == null && booleans2 == null;
        boolean bothNotNull = booleans1 != null && booleans2 != null;
        boolean same = bothNull || (bothNotNull && booleans1.length == booleans2.length);
        if (same && bothNotNull) {
            int i;
            for (i = 0; i < booleans1.length && booleans1[i] == booleans2[i]; i++);
            same = i == booleans1.length;
        }
        return same;
    }

    /**
     *
     * @param probNet1
     * @param probNet2
     * @return
     */
    private boolean sameInfoListOfLinks(ProbNet probNet1, ProbNet probNet2) {
        List<Link<Node>> links1 = probNet1.getLinks();
        List<Link<Node>> links2 = probNet2.getLinks();
        int size = links1.size();
        boolean same = size == links2.size();
        for (int i = 0; i < size && same; i++) {
            Link<Node> link11 = links1.get(i);
            Node node11 = link11.getNode1();
            Variable variable11 = node11.getVariable();
            String name11 = variable11.getName();

            Node node12 = link11.getNode2();
            Variable variable12 = node12.getVariable();
            String name12 = variable12.getName();

            Link link22 = null;
            try {
                Node node21 = probNet2.getNode(name11);
                Node node22 = probNet2.getNode(name12);
                same &= (node21 != null && node22 != null);
                link22 = same ? probNet2.getLink(node21, node22, link11.isDirected()) : null;
                same &= !(link22 == null);

                // Compare restrictions
                if (same) {
                    Potential restrictions1 = link11.getRestrictionsPotential();
                    Potential restrictions2 = link22.getRestrictionsPotential();
                    boolean bothNull = restrictions1 == null && restrictions2 == null;
                    boolean bothNotNull = restrictions1 != null && restrictions2 != null;
                    same &= bothNull || (bothNotNull && restrictions1.getClass() == restrictions2.getClass());
                    if (same && bothNotNull) {
                        if (restrictions1.getClass() == TablePotential.class) {
                            same &= sameInfoTablePotentials((TablePotential)restrictions1, (TablePotential)restrictions2);
                        } else { // At this moment, restrictions are TablePotentials,
                            same &= sameInfoCommonPartPotentials(restrictions1, restrictions2);
                        }
                    }
                }
            } catch (NodeNotFoundException e) {
                same = false;
            }

            // Checks revealingStates and revealingIntervals
            if (same && link22 != null) {
                same = sameInfoListOfStates(link11.getRevealingStates(), link22.getRevealingStates()) &&
                       sameInfoListOfRevealingIntervals(link11.getRevealingIntervals(), link22.getRevealingIntervals());
            }
        }

        return same;
    }

    private boolean sameInfoListOfRevealingIntervals(List<PartitionedInterval> revealingIntervals1, List<PartitionedInterval> revealingIntervals2) {
        boolean bothEmpty = revealingIntervals1.isEmpty() && revealingIntervals2.isEmpty();
        boolean bothNotEmpty = !revealingIntervals1.isEmpty() && !revealingIntervals2.isEmpty();
        boolean same = bothEmpty || bothNotEmpty;
        int numStates = revealingIntervals1.size();
        for (int i = 0; i < numStates && same; i++) {
            same = sameInfoPartitionedIntervals(revealingIntervals1.get(i), revealingIntervals2.get(i));
        }
        return same;
    }

    private boolean sameStates(State state1, State state2) {
        return sameInfoStrings(state1.getName(), state2.getName()) &&
                sameInfoMapsStrings(state1.additionalProperties, state2.additionalProperties);
    }

    private boolean sameInfoListOfStates(List<State> states1, List<State> states2) {
        boolean bothEmpty = states1.isEmpty() && states2.isEmpty();
        boolean bothNotEmpty = !states1.isEmpty() && !states2.isEmpty();
        int numStates = states1.size();
        boolean same = bothEmpty || (bothNotEmpty && numStates == states2.size());
        if (same && bothNotEmpty) {
            int i;
            for (i = 0; i < numStates && sameStates(states1.get(i), states2.get(i)); i++);
            same = i == numStates;
        }
        return same;
    }

    private boolean sameInfoListOfPotentials(ProbNet probNet1, ProbNet probNet2) {
        int numPotentials = probNet1.getNumPotentials();
        boolean same = numPotentials == probNet2.getNumPotentials();
        if (same && numPotentials > 0) {
            Set<TablePotential> constantPotentials1 = probNet1.getConstantPotentials();
            Set<TablePotential> constantPotentials2 = probNet2.getConstantPotentials();
            same = sameInfoConstantPotentials(constantPotentials1, constantPotentials2);
            if (same) {
                List<Potential> potentials1 = probNet1.getPotentials();
                potentials1.removeAll(constantPotentials1);
                List<Potential> potentials2 = probNet2.getPotentials();
                potentials2.removeAll(constantPotentials2);
                int size = potentials1.size();
                int i;
                for (i = 0; i < size; i++) {
                    if (!sameInfoPotentials(potentials1.get(i), potentials2.get(i))) {
                        break;
                    }
                    System.out.println("Potential " + i);
                }
                same = i == size;
            }
        }
        return same;
    }

    private boolean sameInfoPotentials(Potential potential1, Potential potential2) {
        Class potentialClass = potential1.getClass();
        boolean same = potentialClass == potential2.getClass();
        if (same) {
            if (TablePotential.class.isAssignableFrom(potentialClass)) {
                same = sameInfoTablePotentials((TablePotential)potential1, (TablePotential)potential2);
                if (same && AugmentedTable.class.isAssignableFrom(potentialClass)) {
                    same = sameInfoAugmentedTablePotentials((AugmentedTable)potential1, (AugmentedTable)potential2);
                }
            } else if (ICIPotential.class.isAssignableFrom(potentialClass)) {
                same = sameInfoICIPotentials((ICIPotential)potential1, (ICIPotential)potential2);
                if (same && MinMaxPotential.class.isAssignableFrom(potentialClass)) {
                    same = sameInfoMinMaxPotentials((MinMaxPotential)potential1, (MinMaxPotential)potential2);
                }
            } else if (potentialClass == UniformPotential.class) {
                same = sameInfoUniformPotentials((UniformPotential)potential1, (UniformPotential)potential2);
            } else if (TreeADDPotential.class.isAssignableFrom(potentialClass)) {
                same = sameInfoTreeADDPotentials((TreeADDPotential)potential1, (TreeADDPotential)potential2);
            } else if (CycleLengthShift.class == potentialClass) {
                same = sameInfoCycleLengthShiftPotentials((CycleLengthShift)potential1, (CycleLengthShift)potential2);
            } else if (DiscretizedCauchyPotential.class == potentialClass) {
                same = sameInfoDiscretizedCauchyPotentials((DiscretizedCauchyPotential)potential1, (DiscretizedCauchyPotential)potential2);
            } else if (ExactDistrPotential.class == potentialClass) {
                same = sameInfoExactDistrPotentials((ExactDistrPotential)potential1, (ExactDistrPotential)potential2);
            } else if (UnivariateDistrPotential.class == potentialClass) {
                same = sameInfoUnivariableDistrPotentials((UnivariateDistrPotential)potential1, (UnivariateDistrPotential)potential2);
            } else if (GLMPotential.class.isAssignableFrom(potentialClass)) {
                same = sameInfoGLMPotentials((GLMPotential)potential1, (GLMPotential)potential2);
                if (same && WeibullHazardPotential.class.isAssignableFrom(potentialClass)) {
                    same = sameInfoWeibullHazardPotentials((WeibullHazardPotential)potential1, (WeibullHazardPotential)potential2);
                } else if (same && FunctionPotential.class == potentialClass) {
                    same = sameInfoFunctionPotentials((FunctionPotential)potential1, (FunctionPotential)potential2);
                }
            } else if (BinomialPotential.class == potentialClass) {
                same = sameInfoBinomialPotentials((BinomialPotential)potential1, (BinomialPotential)potential2);
            } else if (DeltaPotential.class == potentialClass) {
                same = sameInfoDeltaPotentials((DeltaPotential)potential1, (DeltaPotential)potential2);
            } else {
                same = sameInfoCommonPartPotentials(potential1, potential2);
            }
        }
        return same;
    }

    private boolean sameInfoDeltaPotentials(DeltaPotential potential1, DeltaPotential potential2) {
        return  potential1.getNumericValue() == potential2.getNumericValue() &&
                potential1.getStateIndex() == potential2.getStateIndex() &&
                sameStates(potential1.getState(), potential2.getState());
    }

    private boolean sameInfoBinomialPotentials(BinomialPotential potential1, BinomialPotential potential2) {
        return potential1.getN() == potential2.getN() && potential1.gettheta() == potential2.gettheta();
    }

    private boolean sameInfoFunctionPotentials(FunctionPotential potential1, FunctionPotential potential2) {
        return potential1.getFunction().matches(potential2.getFunction());
    }

    private boolean sameInfoWeibullHazardPotentials(WeibullHazardPotential potential1, WeibullHazardPotential potential2) {
        return sameInfoVariables(potential1.getTimeVariable(), potential2.getTimeVariable()) &&
                potential1.getGamma() == potential2.getGamma();
    }

    private boolean sameInfoGLMPotentials(GLMPotential potential1, GLMPotential potential2) {
        return sameInfoCommonPartPotentials(potential1, potential2) &&
                sameInfoArrayOfDoubles(potential1.getCholeskyDecomposition(), potential2.getCholeskyDecomposition()) &&
                sameInfoArrayOfDoubles(potential1.getCoefficients(), potential2.getCoefficients()) &&
                sameInfoArrayOfDoubles(potential1.getCovarianceMatrix(), potential2.getCovarianceMatrix()) &&
                potential1.getConstant() == potential2.getConstant() &&
                sameInfoArrayOfStrings(potential1.getCovariates(), potential2.getCovariates());
    }

    private boolean sameInfoUnivariableDistrPotentials(UnivariateDistrPotential potential1, UnivariateDistrPotential potential2) {
        return sameInfoCommonPartPotentials(potential1, potential2) &&
                sameInfoAugmentedTablePotentials(potential1.getAugmentedTable(), potential2.getAugmentedTable()) &&
                sameInfoVariables(potential1.getChildVariable(), potential2.getChildVariable()) &&
                sameInfoAugmentedTablePotentials(potential1.getDistributionTable(), potential2.getDistributionTable()) &&
                sameListOfVariablesNames(potential1.getParameterVariables(), potential2.getParameterVariables()) &&
                sameListOfVariablesNames(potential1.getFiniteStatesVariables(), potential2.getFiniteStatesVariables()) &&
                sameInfoStrings(potential1.getProbDensFunctionName(), potential2.getProbDensFunctionName());
    }

    private boolean sameInfoExactDistrPotentials(ExactDistrPotential potential1, ExactDistrPotential potential2) {
        return sameInfoCommonPartPotentials(potential1, potential2) &&
                sameInfoTablePotentials(potential1.getTablePotential(), potential2.getTablePotential());
    }

    private boolean sameInfoDiscretizedCauchyPotentials(DiscretizedCauchyPotential potential1, DiscretizedCauchyPotential potential2) {
        return sameInfoCommonPartPotentials(potential1, potential2) &&
                sameInfoPotentials(potential1.getMedian(), potential2.getMedian()) &&
                sameInfoPotentials(potential1.getScale(), potential2.getScale());
    }

    private boolean sameInfoCycleLengthShiftPotentials(CycleLengthShift potential1, CycleLengthShift potential2) {
        return sameInfoCommonPartPotentials(potential1, potential2) &&
               sameInfoCycleLength(potential1.getCycleLength(), potential2.getCycleLength());
    }

    private boolean sameInfoAugmentedTablePotentials(AugmentedTable potential1, AugmentedTable potential2) {
        return sameInfoArrayOfStrings(potential1.getFunctionValues(), potential2.getFunctionValues());
    }

    private boolean sameInfoArrayOfStrings(String[] functionValues1, String[] functionValues2) {
        boolean bothNull = functionValues1 == null && functionValues2 == null;
        boolean bothNotNull = functionValues1 != null && functionValues2 != null;
        boolean same = bothNull || (bothNotNull && functionValues1.length == functionValues2.length);
        if (same) {
            int i;
            for (i = 0; i < functionValues1.length && sameInfoStrings(functionValues1[i], functionValues2[i]); i++);
            same = i == functionValues1.length;
        }
        return same;
    }

    private boolean sameInfoUniformPotentials(UniformPotential potential1, UniformPotential potential2) {
        return sameInfoCommonPartPotentials(potential1, potential2) &&
                potential1.isUncertain() == potential2.isUncertain() &&
                potential1.getDiscreteValue() == potential2.getDiscreteValue();
    }

    private boolean sameInfoTreeADDPotentials(TreeADDPotential potential1, TreeADDPotential potential2) {
        boolean same = sameInfoCommonPartPotentials(potential1, potential2) &&
                       sameInfoVariables(potential1.getRootVariable(), potential2.getRootVariable());
        same &= sameInfoListOfBranches(potential1.getBranches(), potential2.getBranches());
        return same;
    }

    private boolean sameInfoListOfBranches(List<TreeADDBranch> branches1, List<TreeADDBranch> branches2) {
        boolean bothNull = branches1 == null && branches2 == null;
        boolean bothNotNull = branches1 != null && branches2 != null;
        int size = bothNotNull ? branches1.size() : 0;
        boolean same = bothNull ||
                       ( bothNotNull && size == branches2.size() );
        if (same && bothNotNull) {
            int i;
            for (i = 0; i < size && sameInfoBranches(branches1.get(i), branches2.get(i)); i++);
            same = i == size;
        }
        return same;
    }

    private boolean sameInfoBranches(TreeADDBranch treeADDBranch1, TreeADDBranch treeADDBranch2) {
        return sameListOfVariablesNames(treeADDBranch1.getAddableVariables(), treeADDBranch2.getAddableVariables()) &&
               sameInfoStrings(treeADDBranch1.getLabel(), treeADDBranch2.getLabel()) &&
               sameThresholds(treeADDBranch1.getLowerBound(), treeADDBranch2.getLowerBound()) &&
               sameThresholds(treeADDBranch1.getUpperBound(), treeADDBranch2.getUpperBound()) &&
               sameInfoListOfStates(treeADDBranch1.getStates(), treeADDBranch2.getStates()) &&
               sameInfoPotentials(treeADDBranch1.getPotential(), treeADDBranch2.getPotential());
    }

    private boolean sameThresholds(Threshold threshold1, Threshold threshold2) {
        return threshold1.getLimit() == threshold2.getLimit() && threshold1.belongsToLeft() == threshold2.belongsToLeft();
    }

    private boolean sameInfoMinMaxPotentials(MinMaxPotential potential1, MinMaxPotential potential2) {
        return sameInfoVariables(potential1.getPseudoVariable(), potential2.getPseudoVariable()) &&
                sameInfoTablePotentials(potential1.getCPT(), potential2.getCPT());
    }

    /**
     * Compares the common part of two ICI Potentials
     * @param potential1
     * @param potential2
     * @return
     */
    private boolean sameInfoICIPotentials(ICIPotential potential1, ICIPotential potential2) {
        boolean same = sameInfoCommonPartPotentials(potential1, potential2) &&
                potential1.getModelType() == potential2.getModelType() &&
                potential1.getFamily() == potential2.getFamily() &&
                sameInfoArrayOfDoubles(potential1.getLeakyParameters(), potential2.getLeakyParameters());
        List<Variable> variables1 = potential1.getVariables();
        List<Variable> variables2 = potential2.getVariables();
        int numVariables = variables1.size();
        if (same) {
            for (int i = 1; i < numVariables; i++) {
                Variable variable1 = variables1.get(i);
                Variable variable2 = variables2.get(i);
                same &= sameInfoArrayOfDoubles(potential1.getNoisyParameters(variable1), potential2.getNoisyParameters(variable2));
            }
        }
        if (same) {
            // Compare subpotentials is equivalent to compare the CPT
            try {
                TablePotential cpt1 = potential1.getCPT();
                TablePotential cpt2 = potential2.getCPT();
                same = sameInfoTablePotentials(cpt1, cpt2);
            } catch (NonProjectablePotentialException e) {
                System.out.println("ICIPotentials do not project properly.");
                e.printStackTrace();
            } catch (WrongCriterionException e) {
                System.out.println("ICIPotentials have a wrong criterion.");
                e.printStackTrace();
            }
        }

        return same;
    }

    private boolean sameInfoConstantPotentials(Set<TablePotential> constantPotentials1, Set<TablePotential> constantPotentials2) {
        boolean bothNotNull = constantPotentials1 != null && constantPotentials2 != null;
        boolean bothNull = constantPotentials1 == null && constantPotentials2 == null;
        boolean same = bothNull || (bothNotNull && constantPotentials1.size() == constantPotentials2.size());
        if (bothNotNull && same) {
            int numConstantPotentials = constantPotentials1.size();
            if (numConstantPotentials > 0) {
                List<TablePotential> list1 = new ArrayList<TablePotential>(constantPotentials1);
                List<TablePotential> list2 = new ArrayList<TablePotential>(constantPotentials2);
                for (int i = 0; i < numConstantPotentials && same; i++) {
                    int j;
                    for (j = 0; j < numConstantPotentials && !sameInfoTablePotentials(list1.get(i), list2.get(j)); j++);
                    same = j < numConstantPotentials;
                }
            }
        }
        return same;
    }

    /**
     * Compare two not null constant potentials
     * @param tablePotential1
     * @param tablePotential2
     * @return
     */
    private boolean sameInfoTablePotentials(TablePotential tablePotential1, TablePotential tablePotential2) {
        // Common part for all potentials
        boolean same = sameInfoCommonPartPotentials(tablePotential1, tablePotential2);

        // Compare values
        same &= tablePotential1.values.length == tablePotential2.values.length;
        if (same) {
            int i;
            for (i = 0; i < tablePotential1.values.length && tablePotential1.values[i] == tablePotential2.values[i]; i++);
            same = i == tablePotential1.values.length;
        }
        same &= tablePotential1.getInitialPosition() == tablePotential2.getInitialPosition();
        // It does not compare offsets and dimensions because variables are already checked.

        boolean bothNull = tablePotential1.uncertainValues == null && tablePotential2.uncertainValues == null;
        boolean bothNotNull = tablePotential1.uncertainValues != null && tablePotential2.uncertainValues != null;
        same &= bothNull || (bothNotNull && tablePotential1.uncertainValues.length == tablePotential2.uncertainValues.length);
        if (same && bothNotNull) {
            int i;
            for (i = 0; i < tablePotential1.uncertainValues.length && sameInfoUncertainValues(tablePotential1.uncertainValues[i], tablePotential2.uncertainValues[i]); i++);
            same = i == tablePotential1.uncertainValues.length;
        }

        return same;
    }

    private boolean sameInfoUncertainValues(UncertainValue uncertainValue1, UncertainValue uncertainValue2) {
        boolean same = sameInfoStrings(uncertainValue1.getName(), uncertainValue2.getName());
        if (same) {
            ProbDensFunction probDensFunction1 = uncertainValue1.getProbDensFunction();
            ProbDensFunction probDensFunction2 = uncertainValue2.getProbDensFunction();
            same &= sameInfoArrayOfDoubles(probDensFunction1.getParameters(), probDensFunction2.getParameters()) &&
                    probDensFunction1.getMinimum() == probDensFunction2.getMinimum() &&
                    probDensFunction1.getMaximum() == probDensFunction2.getMaximum() &&
                    probDensFunction1.getMean() == probDensFunction2.getMean() &&
                    probDensFunction1.getStandardDeviation() == probDensFunction2.getStandardDeviation();
        }
        return same;
    }

    /**
     * Compares two potentials
     * @param potential1
     * @param potential2
     * @return
     */
    private boolean sameInfoCommonPartPotentials(Potential potential1, Potential potential2) {
        // Compare miscelanea attributes
        boolean same = potential1.getCriterion() == potential2.getCriterion() &&
                potential1.isAdditive() == potential2.isAdditive() &&
                potential1.isUncertain() == potential2.isUncertain() &&
                sameInfoStrings(potential1.getComment(), potential2.getComment()) &&
                potential1.getPotentialRole() == potential2.getPotentialRole();
        if (same) { // Compare properties
            Map<String, Object> properties1 = potential1.properties;
            Map<String, Object> properties2 = potential2.properties;
            int numProperties = properties1.size();
            same &= numProperties == properties2.size();
            if (same && numProperties > 0) {
                Set<String> keys1 = properties1.keySet();
                for (String key : keys1) {
                    Object object1 = properties1.get(key);
                    Object object2 = properties1.get(key);
                    boolean bothNotNull = object1 != null && object2 != null;
                    boolean bothNull = object1 == null && object2 == null;
                    Class class1 = object1.getClass();
                    Class class2 = object2.getClass();
                    same &= bothNull || (bothNotNull && class1 == class2);
                    if (bothNotNull && same && class1 == String.class) {
                        same = sameInfoStrings(((String)object1), ((String)object2));
                    }
                }
            }
        }

        // Variables (assumption that variables are in the same order)
        same &= sameListOfVariablesNames(potential1.getVariables(), potential2.getVariables());

        return same;
    }

    /**
     * Compares two wrappings of a Map with a name.
     * @param swp1
     * @param swp2
     * @return
     */
    private boolean sameInfoStringsWithProperties(StringWithProperties swp1, StringWithProperties swp2) {
        boolean bothNotNull = swp1 != null && swp2 != null;
        boolean bothNull = swp1 == null && swp2 == null;
        return bothNull ||
               ( bothNotNull &&
                 sameInfoStrings(swp1.getString(), swp2.getString()) &&
                 sameInfoMapsStrings(swp1.getAdditionalProperties().getInformation(), swp2.getAdditionalProperties().getInformation()) );
    }

    /**
     * Compares two HashMaps key = String,value = String
     * @param map1
     * @param map2
     * @return
     */
    private boolean sameInfoMapsStrings(Map<String,String> map1, Map<String,String> map2) {
        boolean notNull = map1 != null && map2 != null;
        boolean same = notNull || (map1 == null && map2 == null);
        if (same && notNull) {
            Set<String> set1 = map1.keySet();
            Set<String> set2 = map1.keySet();
            same = set1.size() == set2.size();
            if (same) {
                for (String key : set1) {
                    same = set2.contains(key) ? sameInfoStrings(map1.get(key), map2.get(key)) : false;
                    if (!same) break;
                }
            }
        }
        return same;
    }

    private boolean sameListOfVariablesNames(List<Variable> variables1, List<Variable> variables2) {
        int numVariables = variables1.size();
        boolean same = numVariables == variables2.size();
        if (same) {
            int i;
            for (i = 0; i < numVariables && same && sameInfoStrings(variables1.get(i).getName(), variables2.get(i).getName()); i++);
            same = i == numVariables;
        }
        return same;
    }

    /**
     *
     * @param parameters1
     * @param parameters2
     * @return
     */
    private boolean sameInfoArrayOfDoubles(double[] parameters1, double[] parameters2) {
        boolean bothNull = parameters1 == null && parameters2 == null;
        boolean bothNotNull = parameters1 != null && parameters2 != null;
        boolean same = bothNull || bothNotNull;
        if (bothNotNull && parameters1.length == parameters2.length) {
            int i;
            for (i = 0; i < parameters1.length && parameters1[i] == parameters2[i]; i++) ;
            same = i == parameters1.length;
        } else {
            same = false;
        }
        return same;
    }

    /**
     * Compare two strings that can be null
     * @param name1
     * @param name2
     * @return
     */
    private boolean sameInfoStrings(String name1, String name2) {
        return ( (name1 == null && name2 == null) || (name1 != null && name2 != null && name1.compareTo(name2) == 0) );
    }

    // Auxiliar internal clases and interfaces
    private interface IteratorPGMX {
        File next();
        boolean hasNext();
    }

    private interface StringFilter {
        boolean meetsCondition(String string);
    }

    private class PGMXFiles implements StringFilter {
        private final String PGMX_FILES = ".PGMX";

        @Override
        public boolean meetsCondition(String string) {
            return string.toUpperCase().endsWith(PGMX_FILES);
        }
    }

    private class FileIterator implements IteratorPGMX {

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
        public FileIterator(File rootFolder, List<StringFilter>... filters) {
            this.filters = filters != null && filters.length == 1 ? filters[0] : null;
            hasNext = false;
            next = null;

            if (rootFolder.isDirectory()) {
                subNodes = new ArrayList<>();
                File[] rootChildren = rootFolder.listFiles();
                subNodes.add(rootChildren);

                subNodesIndexes = new ArrayList<>();
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
            next = next == null ? next : lookForNext();
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
            if (filters != null) {
                for (StringFilter filter : filters) {
                    match &= filter.meetsCondition(file.getName());
                }
            }
            return match;
        }
    }

}
