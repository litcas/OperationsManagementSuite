package com.rengu.operationsoanagementsuite.Utils;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CompressUtils {
    // 解压缩Zip文件
    public static void deCompressZip(File zipFile, String destFilePath) throws IOException {
        if (zipFile != null) {
            ZipArchiveInputStream zipArchiveInputStream = new ZipArchiveInputStream(new FileInputStream(zipFile));
            ArchiveEntry archiveEntry = null;
            while ((archiveEntry = zipArchiveInputStream.getNextEntry()) != null) {
                String filePath = destFilePath + archiveEntry.getName();
                File file = new File(filePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
    }
}