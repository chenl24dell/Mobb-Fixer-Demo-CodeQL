package com.datadomain.ddms.server.module.report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

public class FileSanitizer {
	/**
	 * Sanitize the file name, to resolve path traversal issue.
	 *
	 * @param dir
	 *            - file directory
	 * @param entry
	 *            - file name
	 * @throws IOException
	 */
    public static File sanitizeAndCreateFile(final File dir, final String entry) throws IOException {
        // Convert the directory to a Path object for safer manipulation
        Path dirPath;
        try {
            // Check if the directory exists, if not, create it
            if (!dir.exists()) {
                Files.createDirectories(dir.toPath());
            }
            // Convert to real path to resolve symbolic links
            dirPath = dir.toPath().toRealPath();
        } catch (NoSuchFileException e) {
            throw new IOException("Directory does not exist and could not be created: " + dir, e);
        }

        // Ensure the entry does not contain any traversal sequences
        if (entry.contains("..")) {
            throw new IOException("Path traversal attempt detected: " + entry);
        }

        // Construct the absolute path of the entry relative to the directory
        Path absoluteEntryPath = dirPath.resolve(entry).toAbsolutePath();

        // Check if the entry path is a descendant of the directory path
        if (!absoluteEntryPath.startsWith(dirPath)) {
            throw new IOException("Path traversal exception: " + entry);
        }

        // Convert the entry path back to File object for compatibility
        return absoluteEntryPath.toFile();
    }

	/**
	 * Sanitize the file name, to resolve path traversal issue.
	 *
	 * @param dir
	 *            - file directory as a string
	 * @param entry
	 *            - file name
	 * @throws IOException
	 */
	public static File sanitizeAndCreateFile(final String dir, final String entry) throws IOException {
		return sanitizeAndCreateFile(new File(dir), entry);
	}

	/**
	 * Sanitize the file name, to resolve path traversal issue.
	 *
	 * @param entry
	 *            - file name
	 * @throws IOException
	 */
	public static String sanitize(final String entry) throws IOException {
		String sanitizedEntry = entry;
		if (entry.length() == 0) {
			throw new IOException("Path traversal exception-No file length: " + entry);
		}
		if (new File(entry).isAbsolute()) {
			throw new IOException("Path traversal exception-File name is absolute: " + entry);
		}

		// remove "../" & "./"
		sanitizedEntry = sanitizedEntry.replaceAll("\\.\\./", "");
		sanitizedEntry = sanitizedEntry.replaceAll("\\./", "");

		return entry;
	}
}
