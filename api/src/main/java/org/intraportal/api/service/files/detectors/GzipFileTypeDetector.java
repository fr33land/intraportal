package org.intraportal.api.service.files.detectors;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;
import java.util.Arrays;

public final class GzipFileTypeDetector extends FileTypeDetector {
    private static final byte[] GZIP_HEADER = {(byte) 0x1F, (byte) 0x8B};

    @Override
    public String probeContentType(final Path path) throws IOException {
        byte[] data = new byte[GZIP_HEADER.length];
        DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(path.toFile())));
        in.read(data);
        in.close();
        return Arrays.equals(data, GZIP_HEADER) ? "application/x-gzip" : null;
    }
}