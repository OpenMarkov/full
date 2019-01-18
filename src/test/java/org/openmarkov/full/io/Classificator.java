package org.openmarkov.full.io;

import bitbucket.NetsRepository;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;
import org.junit.Test;
import org.openmarkov.core.exception.*;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.NodeType;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.constraint.OnlyAtemporalVariables;
import org.openmarkov.core.model.network.type.BayesianNetworkType;
import org.openmarkov.core.model.network.type.InfluenceDiagramType;
import org.openmarkov.core.model.network.type.MIDType;
import org.openmarkov.core.model.network.type.NetworkType;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;
import org.openmarkov.io.probmodel.reader.ReaderFactory;
import org.openmarkov.io.probmodel.strings.XMLAttributes;
import org.openmarkov.io.probmodel.writer.PGMXWriter_0_2;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class Classificator extends PGMXReader_0_2 {

    public static void main(String args[]) throws Exception {
        Classificator classificator = new Classificator("/home/manuel/Redes/OriginalNetworks");
        classificator.listFiles();
    }

    // Attributes
    private String pathToFiles="/home/manuel/Redes/OriginalNetworks";
    private String pathToNewFiles = "/home/manuel/Redes/NewNetworks";

    // Constructor
    /** This class performs several operations with files in PGMX format.
     * @param paths Optional String[] parameter. paths[0] = path to files; paths[1] = path to new files. */
    public Classificator(String... paths) {
        if (paths.length >=1 ) {
            this.pathToFiles = paths[0];
        }
        if (paths.length >= 2) {
            this.pathToNewFiles = paths[1];
        }
    }

    // Methods

    /**
     * Remove recursively all the files and folders of 'folder'
     * @param folder
     */
    private void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete(); // Also deletes the directory
    }

    /**
     * Creates folders to write files for testing. If the folders exist from previous tests, removes contents.
     * @param pathToNewFiles02
     * @param pathToNewFiles07
     * @throws IOException
     */
    private void createTestFoldersTree(String pathToNewFiles02, String pathToNewFiles07) throws IOException {
        // Create test directories if they do not exists
        File newFiles = new File(pathToNewFiles);
        newFiles.createNewFile(); // Create path; if file already exists do nothing.

        File newFiles02 = new File(pathToNewFiles02);
        File newFiles07 = new File(pathToNewFiles07);
        newFiles02.createNewFile();
        newFiles07.createNewFile();
        // If directories exists from a previous test, remove the contents
        deleteFolder(newFiles02);
        deleteFolder(newFiles07);
    }

    /**
     * Read networks from directory 'pathToFiles' and writes them in 0.2 and 0.7 version in
     * 'pathToNewFiles/0.2' and 'pathToNewFiles/0.7'.
     */
    public void testConversionBetweenVersions() throws IOException {
        String pathToNewFiles02 = pathToNewFiles + "/0_2";
        String pathToNewFiles07 = pathToNewFiles + "/0_7";
        createTestFoldersTree(pathToNewFiles02, pathToNewFiles07);

        File directory = new File(pathToFiles);
        File[] fList = directory.listFiles();
        int numCharsPathToNewFiles = pathToNewFiles.length();
        for (File file : fList) {
            if (file.isDirectory()) {
                // Create new sub-directories
                String subPath = file.getAbsolutePath().substring(numCharsPathToNewFiles);
                String pathToNewDirectory02 = pathToNewFiles02 + subPath;
                File newDirectory02 = new File(pathToNewDirectory02);
                newDirectory02.createNewFile();
                String pathToNewDirectory07 = pathToNewFiles07 + subPath;
                File newDirectory07 = new File(pathToNewDirectory07);
                newDirectory07.createNewFile();
            } else {
                PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
                ProbNetInfo probNetInfo = null;
                InputStream networkStream = getClass().getClassLoader().getResourceAsStream(file.getAbsolutePath());
                try {
                    probNetInfo = pgmxReader.loadProbNetInfo(file.getAbsolutePath(), networkStream);
                } catch (ParserException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getCommonFirstPartStrings(String strA, String strB) {
        int min = strA.length();
        int aux = strB.length();
        min = (aux < min) ? aux : min;
        int i = 0;
        int lastSlash = 0;
        while (strA.charAt(i) == strB.charAt(i)) {
            if (strA.charAt(i) == '/') {
                lastSlash = i + 1;
            }
            i++;
        }
        return strA.substring(0, i);
    }

    /** List recursively files in PGMX version and writes its directory, version and name. */
    public void listFiles() throws Exception {
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
                    System.err.println("No existe el fichero " + canonicalPath);
                }
            } else if (file.isDirectory()) {
                String absolutePat = file.getAbsolutePath();
                System.out.println();
                System.out.println(file.getAbsolutePath());
                for (int i = 0; i < absolutePat.length(); i++) {
                    System.out.print("-");
                }
                System.out.println();
                //listFiles(file.getAbsolutePath());
            }
        }
    }

//    /**
//     * Reads all the networks from the repository that meet the restriction given in the parameter "networkType"
//     *
//     * @param networkType <code>NetworkType</code>
//     * @return <code>List</code> of <code>ProbNet</code>s
//     */
//    public static List<ProbNet> readProbNetsDB(NetworkType networkType) {
//        NetsRepository netsRepository = new NetsRepository();
//        List<URL> bayesianNetworksURLList = netsRepository.getNetworks(networkType);
//        PGMXReader_0_2 reader = new PGMXReader_0_2();
//        List<ProbNet> probNetsDB = new ArrayList<ProbNet>();
//        List<String> wrongNetworksNames = new ArrayList<String>();
//        int readingErrors = 0;
//        for (URL bayesianNetworkURL : bayesianNetworksURLList) {
//            ProbNet probNet = null;
//            String fileName = null;
//            try {
//                fileName = bayesianNetworkURL.getFile();
//                probNet = reader.loadProbNet(fileName, bayesianNetworkURL.openStream());
//                probNetsDB.add(probNet);
//            } catch (ParserException | IOException e) {
//                readingErrors++;
//                wrongNetworksNames.add(fileName);
//            }
//        }
//        if (readingErrors > 0) {
//            if (probNetsDB.isEmpty()) {
//                System.err.println("No Bayesian networks for testing due to reading errors.");
//            } else {
//                System.err.println("Some errors reading these networks:");
//            }
//            System.err.println();
//            for (String wrongNetworkName : wrongNetworksNames) {
//                System.err.println(wrongNetworkName);
//            }
//        } else {
//            if (probNetsDB.isEmpty()) {
//                System.err.println("No networks found in repository.");
//            }
//        }
//        // Order the networks, from smallest to largest number of variables
//        int numNetworks = probNetsDB.size();
//        ProbNet aux;
//        for (int i = 0; i < numNetworks - 1; i++) {
//            for (int j = i + 1; j < numNetworks; j++) {
//                if (probNetsDB.get(i).getVariables().size() > probNetsDB.get(j).getVariables().size()) {
//                    aux = probNetsDB.get(j);
//                    probNetsDB.set(j, probNetsDB.get(i));
//                    probNetsDB.set(i, aux);
//                }
//            }
//        }
//
//        return probNetsDB;
//    }
//
//    /**
//     * @param netName = path + network name + extension. <code>String</code>
//     * @return The <code>ProbNet</code> readed or <code>null</code>
//     */
//    public ProbNetInfo loadProbNetInfo(String netName, InputStream... inputStream ) throws ParserException {
//
//        // Get file if not included.
//        InputStream stream = null;
//        if ( inputStream.length == 0 ) {
//            try {
//                stream = new FileInputStream( netName );
//            }
//            catch ( FileNotFoundException e ) {
//                throw new ParserException( "File " + netName + " not found." );
//            }
//        }
//        else {
//            if ( inputStream.length > 1 ) {
//                throw new ParserException( "Only is allowed to open ONE InputStream, not " + inputStream.length + "." );
//            }
//            stream = inputStream[0];
//        }
//
//        // Get root element.
//        SAXBuilder builder = new SAXBuilder();
//        builder.setJDOMFactory( new LocatedJDOMFactory() );
//        Document document = null;
//        try {
//            document = builder.build( stream );
//        }
//        catch ( JDOMException e ) {
//            throw new ParserException( "Can not parse XML document " + netName + ":" + e.getMessage() );
//        }
//        catch ( IOException e ) {
//            throw new ParserException( "Error trying to open " + netName + ".\n" + e.getMessage() );
//        }
//        Element root = document.getRootElement();
//
//        return loadProbNetInfo( root, netName );
//    }
//
//    @Test
//    public final void testOpenSaveRepositoryNets() {
//        NetsRepository repository = new NetsRepository();
//        List<URL> listURL = repository.getNetworks();
//
//        for (URL url : listURL) {
//            // The name is irrelevant because this nets will only be created for tests purposes and it will be deleted
//            // after each iteration
//            String networkName = url.getPath();
//            networkName = networkName.substring(networkName.lastIndexOf("/") + 1, networkName.length());
//
//            PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
//
//            try {
//                ProbNetInfo probNetInfo = null;
//                ProbNet probNet = null;
//                try {
//                    probNetInfo = pgmxReader.loadProbNetInfo(networkName, url.openStream());
//                    probNet = probNetInfo.getProbNet();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                assertNotNull(probNet);
//                assertNotNull(probNet.getNodes());
//
//                PGMXWriter_0_2 pgmxWritter = new PGMXWriter_0_2();
//                pgmxWritter.writeProbNet(networkName, probNet, probNetInfo.getEvidence());
//
//                FileInputStream file = new FileInputStream(networkName);
//                probNetInfo = pgmxReader.loadProbNetInfo(networkName, file);
//                probNet = probNetInfo.getProbNet();
//                System.out.println("Loaded, saved and reloaded probNet:" + url.getPath());
//                assertNotNull(probNet);
//                assertNotNull(probNet.getNodes());
//                EvidenceCase preResolutionEvidence;
//                int numSimulations = 10;
//                boolean useMultithreading = true;
//
//                if (probNetInfo.getEvidence().size() > 0) {
//                    preResolutionEvidence = probNetInfo.getEvidence().get(0);
//                } else {
//                    preResolutionEvidence = new EvidenceCase();
//                }
//                if (probNet.getNetworkType().equals(BayesianNetworkType.getUniqueInstance())) {
//                    try {
//                        testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);
//                    } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
//                        e.printStackTrace();
//                    }
//                } else if (probNet.getNetworkType().equals(InfluenceDiagramType.getUniqueInstance())) {
//                    try {
//                        if (probNet.getNodes(NodeType.DECISION).size() > 0) {
//                            testResolveNetwork(probNet, preResolutionEvidence, true);
//                        } else {
//                            testResolveNetwork(probNet, preResolutionEvidence, false);
//                        }
//
//                        // TODO - Check propagate errors
//                        testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);
//
//                        if (hasCostEffectiveness(probNet)) {
//                            testCEADecisionNetwork(probNet, preResolutionEvidence);
//                            testCEAGlobalNetwork(probNet, preResolutionEvidence);
//                            testCEPSANetwork(probNet, preResolutionEvidence, numSimulations, useMultithreading);
//                        }
//
//                    } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
//                        e.printStackTrace();
//                    }
//                } else if (probNet.getNetworkType().equals(MIDType.getUniqueInstance())) {
//                    try {
//                        if (probNet.getNodes(NodeType.DECISION).size() > 0) {
//                            testResolveNetwork(probNet, preResolutionEvidence, true);
//                        } else {
//                            testResolveNetwork(probNet, preResolutionEvidence, false);
//                        }
//                        // TODO - Check propagate errors
//                        testPropagateNetwork(probNet, probNet.getVariables(), preResolutionEvidence);
//
//                        if (hasCostEffectiveness(probNet)) {
//                            testCEADecisionNetwork(probNet, preResolutionEvidence);
//                            testCEAGlobalNetwork(probNet, preResolutionEvidence);
//                            testCEPSANetwork(probNet, preResolutionEvidence, numSimulations, useMultithreading);
//                        }
//
//                        if (!probNet.hasConstraint(OnlyAtemporalVariables.class)) {
//                            testTemporalEvolutionNetwork(probNet, preResolutionEvidence);
//                        }
//
//                    } catch (NotEvaluableNetworkException | IncompatibleEvidenceException | UnexpectedInferenceException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            } catch (WriterException | FileNotFoundException | ParserException e) {
//                e.printStackTrace();
//
//            } finally {
//                File fileToBeDeleted = new File(networkName);
//                fileToBeDeleted.delete();
//            }
//        }
//    }
//

}
