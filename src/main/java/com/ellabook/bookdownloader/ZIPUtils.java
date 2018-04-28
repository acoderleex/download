package com.ellabook.bookdownloader;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;


/**
 * Created by java on 2017/7/10.
 */

class ZIPUtils {

    /**
     * @Description:
     *     解压文件
     * @param zipPath 被压缩文件，请使用绝对路径
     * @param targetPath 解压路径，解压后的文件将会放入此目录中，请使用绝对路径
     *         默认为压缩文件的路径的父目录为解压路径
     * @param encoding 解压编码
     */
    public static void decompress(String zipPath, String targetPath, String encoding,ZIPcallback callback)
            throws FileNotFoundException, ZipException, IOException {
        int p = 0;
        // 获取解缩文件
        File file = new File(zipPath);
        if (!file.isFile()) {
            throw new FileNotFoundException("要解压的文件不存在");
        }
        // 设置解压路径
        if (targetPath == null || "".equals(targetPath)) {
            targetPath = file.getParent();
        }
        // 设置解压编码
        if (encoding == null || "".equals(encoding)) {
            encoding = "GBK";
        }
        // 实例化ZipFile对象
        ZipFile zipFile = new ZipFile(file, encoding);
        // 获取ZipFile中的条目
        Enumeration<ZipEntry> files = zipFile.getEntries();
        // 迭代中的每一个条目
        ZipEntry entry = null;
        // 解压后的文件
        File outFile = null;
        // 读取压缩文件的输入流
        BufferedInputStream bin = null;
        // 写入解压后文件的输出流
        BufferedOutputStream bout = null;
        while (files.hasMoreElements()) {
            // 获取解压条目
            entry = files.nextElement();
            // 实例化解压后文件对象
            outFile = new File(targetPath + File.separator + entry.getName());
            // 如果条目为目录，则跳向下一个
            if (entry.getName().endsWith(File.separator)) {
                outFile.mkdirs();
                continue;
            }
            // 创建目录
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }
            // 创建新文件
            outFile.createNewFile();
            // 如果不可写，则跳向下一个条目
            if (!outFile.canWrite()) {
                continue;
            }
            try {
                // 获取读取条目的输入流
                bin = new BufferedInputStream(zipFile.getInputStream(entry));
                // 获取解压后文件的输出流
                bout = new BufferedOutputStream(new FileOutputStream(outFile));
                // 读取条目，并写入解压后文件
                byte[] buffer = new byte[1024];
                int readCount = -1;
                while ((readCount = bin.read(buffer)) != -1) {
                    bout.write(buffer, 0, readCount);
                }
            } finally {
                try {
                    bin.close();
                    bout.flush();
                    bout.close();
                } catch (Exception e) {}
            }
            if (p >= 0 && p < 100) {
                if (callback != null)
                    callback.callback(p);
            }
            ++p;
        }
        if (callback != null)
            callback.callback(100);
    }

    public interface ZIPcallback {
        void callback(int progress);
    }
}
