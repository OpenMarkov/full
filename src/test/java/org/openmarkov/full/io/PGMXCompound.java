package org.openmarkov.full.io;

import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.exception.WriterException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.io.ProbNetWriter;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;
import org.openmarkov.io.probmodel.writer.PGMXWriter_0_2;
import org.openmarkov.io.probmodel.writer.PGMXWriter_1_0;

import java.io.*;
import java.net.URL;
import java.util.List;


/**
 * Auxiliar class for tests
 */
// TODO Replace usages of Strings with the "version" by the enum Version
public class PGMXCompound {
    private final String V0_2 = "0.2.0";
    private final String V0_7 = "0.7.0";

    private ProbNetInfo probNetInfo;
    private ProbNet probNet;
    private List<EvidenceCase> evidenceCases;
    private File file;
    private String version;
    private URL url;
    private String fileName;

    private boolean errorReading;
    private boolean triedToRead;
    private boolean errorWriting;
    private boolean fileOrigin;
    private boolean urlOrigin;

    public PGMXCompound(File file) {
        setFile(file);
        fileOrigin = true;
        urlOrigin = false;
    }

    public PGMXCompound(URL url, String fileName) {
        this.url = url;
        this.fileName = fileName;
        fileOrigin = false;
        urlOrigin = true;
    }

    public void setFile(File file) {
        this.file = file;
        initialize();
    }

    public void initialize() {
        triedToRead = errorWriting = errorReading = false;
        probNet = null;
        evidenceCases = null;
    }

    public File getFile() {
        return file;
    }

    public boolean wasExceptionThrownWhileReading() {
        return errorReading;
    }

    public boolean wasExceptionThrownWhileWriting() {
        return errorWriting;
    }

    public ProbNetInfo getProbNetInfo() {
        if (probNetInfo == null) {
            readProbNetInfoIfNecessary();
        }
        return probNetInfo;
    }

    public ProbNet getProbNet() {
        if (probNet == null) {
            readProbNetInfoIfNecessary();
        }
        return probNet;
    }

    public List<EvidenceCase> getEvidence() {
        if (evidenceCases == null) {
            readProbNetInfoIfNecessary();
        }
        return evidenceCases;
    }

    public String getVersion() {
        if (version == null) {
            readProbNetInfoIfNecessary();
        }
        return version;
    }

    private void readProbNetInfoIfNecessary() {
        if (!triedToRead) {
            triedToRead = true;
            errorReading = false;
            PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
            String absolutePath = file.getAbsolutePath();
            try {
                if (fileOrigin) {
                    this.probNetInfo = pgmxReader.loadProbNetInfo(absolutePath, new FileInputStream(absolutePath));
                    this.version = pgmxReader.getVersion(absolutePath, new FileInputStream(absolutePath));
                } else {
                    InputStream stream = url.openStream();

                    this.probNetInfo = pgmxReader.loadProbNetInfo(fileName, url.openStream());
                    this.version = pgmxReader.getVersion(fileName, url.openStream());
                }
                this.probNet = probNetInfo.getProbNet();
                this.evidenceCases = probNetInfo.getEvidence();
            } catch (FileNotFoundException e) {
                System.out.println("File " + absolutePath + " does not exists.");
                errorReading = true;
            } catch (ParserException e) {
                System.out.println("Parser exception reading file " + absolutePath);
                errorReading = true;
            } catch (IOException e) {
                System.out.println("Parser exception reading file " + absolutePath);
                errorReading = true;
            }
        }
    }

    public void writeProbNetInfo(String fileName, String version) {
        if (fileOrigin) {
            ProbNetWriter writer;
            if (version.matches(V0_2)) {
                writer = new PGMXWriter_0_2();
            } else {
                writer = new PGMXWriter_1_0();
            }
            try {
                writer.writeProbNet(fileName, probNet, evidenceCases);
                file = new File(fileName);
                triedToRead = false;
                errorReading = false;
                errorWriting = false;
            } catch (WriterException e) {
                System.out.println("Error writing " + fileName + "\nVersion: " + version);
                errorWriting = true;
            }
        } else {
            File baseDir = new File(System.getProperty("java.io.tmpdir"));
            File tempDir = new File(baseDir, fileName + version);
            if (tempDir.mkdir()) {
                file = tempDir;
            }
        }
    }

    private static void copyFileUsingStream(InputStream source, OutputStream destination) throws IOException {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = source.read(buffer)) > 0) {
                destination.write(buffer, 0, length);
            }
        } finally {
            source.close();
            destination.close();
        }
    }

    public boolean isErrorWriting() {
        return errorWriting;
    }

    public boolean isErrorReading() {
        return errorReading;
    }

}


