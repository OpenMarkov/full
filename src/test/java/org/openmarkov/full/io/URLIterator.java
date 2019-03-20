package org.openmarkov.full.io;

import bitbucket.NetsRepository;
import org.openmarkov.core.exception.ParserException;
import org.openmarkov.core.io.ProbNetInfo;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.io.probmodel.reader.PGMXReader_0_2;

import java.io.*;
import java.net.URL;
import java.util.List;

public class URLIterator implements PGMXIterator {

    private String version;
    private String pathToNewFiles;
    // Attributes
    private File next;
    private List<URL> listURL;
    private int nextURLIndex;
    private PGMXCompound compound;

    private List<PGMXFilter> filters;

    @Override
    public PGMXCompound next() {
        URL url = listURL.get(nextURLIndex++);
        String networkName = url.getPath();

        File file = new File(networkName);
        String newName = pathToNewFiles + file.getName() + "-" + version;

        // Copy network to a file
        InputStream infile = null;
        OutputStream outfile = null;
        try {
            infile = url.openStream();
            outfile = new FileOutputStream(newName);

            byte[] buffer = new byte[1024];
            while (infile.read(buffer, 0, 1024) > 0) {
                outfile.write(buffer);
            }
        } catch (IOException e) {
            System.out.println("Error opening network " + networkName);
        } finally {
            try {
                if (infile != null) {
                    infile.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            } catch (IOException e) {
            }
        }

        PGMXReader_0_2 pgmxReader = new PGMXReader_0_2();
        ProbNetInfo probNetInfo = null;
        ProbNet probNet = null;
        try {
            probNetInfo = pgmxReader.loadProbNetInfo(networkName, url.openStream());
            compound = new PGMXCompound(new File(newName));
            probNet = probNetInfo.getProbNet();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserException e) {
            e.printStackTrace();
        }
        return compound;
    }

    @Override
    public boolean hasNext() {
        return nextURLIndex < listURL.size();
    }

    // Constructor
    public URLIterator(String pathToNewFiles, String version, List<PGMXFilter>... filters) {
        this.version = version;
        this.pathToNewFiles = pathToNewFiles;
        this.filters = filters != null && filters.length == 1 ? filters[0] : null;
        NetsRepository repository = new NetsRepository();
        listURL = repository.getNetworks();
        nextURLIndex = 0;
        next = null;
    }
}

