package com.rengu.operationsoanagementsuite.Utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

public class CompressUtils {

    // todo 统一压缩和解压缩zip文件使用的类库

    public static File compressToZip(String srcDirPath, String zipFilePath) throws IOException {
        return compressToZip(new File(srcDirPath), new File(zipFilePath));
    }

    // 压缩文件到zip格式
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

    // 解压缩zip文件到指定目录
    public static File decompressZip(File srcFile, File destDir, String password) throws ZipException {
        // 首先创建ZipFile指向磁盘上的.zip文件
        ZipFile zipFile = new ZipFile(srcFile);
        // 设置文件名编码，在GBK系统中需要设置
//        zipFile.setFileNameCharset("GBK");
        if (!zipFile.isValidZipFile()) {
            // 验证.zip文件是否合法，包括文件是否存在、是否为zip文件、是否被损坏等
            throw new ZipException("压缩文件验证失败，解压缩失败.");
        }
        if (destDir.isDirectory() && !destDir.exists()) {
            destDir.mkdir();
        }
        // 设置密码
        if (zipFile.isEncrypted()) {
            zipFile.setPassword(password.toCharArray());
        }
        // 将文件抽出到解压目录(解压)
        zipFile.extractAll(destDir.getPath());
        return destDir;
    }

}