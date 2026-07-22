package com.pradeep.pdfextractor.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Manages config.properties only.
 *
 * Responsibilities:
 *   - remember last used PDF folder, BinMaster.xlsx, Output.xlsx paths
 *   - load them on startup
 *   - save them whenever the user changes a path
 *
 * Keys:
 *   pdfFolder=D:/Invoices
 *   binMaster=D:/BinMaster.xlsx
 *   outputExcel=D:/Output.xlsx
 */
public class ConfigManager {

    private static final String KEY_PDF_FOLDER = "pdfFolder";
    private static final String KEY_BIN_MASTER = "binMaster";
    private static final String KEY_OUTPUT_EXCEL = "outputExcel";

    private final Path configFile;
    private final Properties properties = new Properties();

    public ConfigManager(Path configFile) {
        this.configFile = configFile;
        load();
    }

    private void load() {

        if (Files.notExists(configFile)) {
            return;
        }

        try (InputStream in = Files.newInputStream(configFile)) {
            properties.load(in);
        } catch (IOException e) {
            // No config yet, or unreadable — start with empty properties.
            // Intentionally silent: this is a "remember last path"
            // convenience feature, not a critical operation.
        }

    }

    private void save() {

        try {

            if (configFile.getParent() != null) {
                Files.createDirectories(configFile.getParent());
            }

            try (OutputStream out = Files.newOutputStream(configFile)) {
                properties.store(out, "PDF Extractor configuration");
            }

        } catch (IOException e) {
            // TEMP DIAGNOSTIC — remove after debugging
            System.err.println("ConfigManager.save() FAILED: " + e);
            e.printStackTrace();
        }

    }

    public String getPdfFolder() {
        return properties.getProperty(KEY_PDF_FOLDER, "");
    }

    public void setPdfFolder(String path) {
        properties.setProperty(KEY_PDF_FOLDER, path == null ? "" : path);
        save();
    }

    public String getBinMaster() {
        return properties.getProperty(KEY_BIN_MASTER, "");
    }

    public void setBinMaster(String path) {
        properties.setProperty(KEY_BIN_MASTER, path == null ? "" : path);
        save();
    }

    public String getOutputExcel() {
        return properties.getProperty(KEY_OUTPUT_EXCEL, "");
    }

    public void setOutputExcel(String path) {
        properties.setProperty(KEY_OUTPUT_EXCEL, path == null ? "" : path);
        save();
    }

}