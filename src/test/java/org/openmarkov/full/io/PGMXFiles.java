package org.openmarkov.full.io;

public class PGMXFiles implements PGMXFilter {

    private final String PGMX_EXTENSION = ".pgmx";

    public boolean meetsCondition(PGMXCompound compound) {
        return compound.getFile().getName().toLowerCase().endsWith(PGMX_EXTENSION);
    }

}
