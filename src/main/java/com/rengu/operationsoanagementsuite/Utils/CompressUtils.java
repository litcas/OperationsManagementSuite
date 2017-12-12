package com.rengu.operationsoanagementsuite.Utils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

public class CompressUtils {

    public static File compressToZip(String srcDirPath, String zipFilePath) throws IOException {
        return compressToZip(new File(srcDirPath), new File(zipFilePath));
    }

    private static File compressToZip(File srcDir, File zipFile) throws IOException {
        String tempFolderPath = srcDir.getPath();
        ZipArchiveOutputStream zipArchiveOutputStream = new ZipArchiveOutputStream(zipFile);
        Collection<File> fileCollection = FileUtils.listFiles(srcDir, null, true);
        for (File file : fileCollection) {
            FileInputStream fileInputStream = new FileInputStream(file);
            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file, file.getAbsolutePath().replace(tempFolderPath, ""));
            zipArchiveOutputStream.putArchiveEntry(zipArchiveEntry);
            IOUtils.copy(fileInputStream, zipArchiveOutputStream);
            zipArchiveOutputStream.closeArchiveEntry();
            fileInputStream.close();
        }
        zipArchiveOutputStream.finish();
        zipArchiveOutputStream.close();
        return zipFile;
    }
}