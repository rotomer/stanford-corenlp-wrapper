package com.rotomer.stanford_corenlp_wrapper;

import java.io.IOException;
import java.nio.file.Path;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Files.write;

class FileUtils {
    static String readFile(final Path filePath) {
        try {
            return new String(readAllBytes(filePath));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void writeFile(final Path filePath, final Iterable<String> lines) {
        try {
            write(filePath, lines);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
