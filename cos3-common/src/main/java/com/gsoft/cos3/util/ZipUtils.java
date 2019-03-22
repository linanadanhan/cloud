package com.gsoft.cos3.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * @author SN
 *
 */
public class ZipUtils {
    /** 
     * 使用GBK编码可以避免压缩中文文件名乱码 
     */  
//    private static final String CHINESE_CHARSET = "GBK";  
  
    /** 
     * 文件读取缓冲区大小 
     */  
    private static final int CACHE_SIZE = 1024;  
  
    /** 
     * <p> 
     * 压缩文件 
     * </p> 
     *  
     * @param sourceFolder 
     *            压缩文件夹 
     * @param zipFilePath 
     *            压缩文件输出路径 
     * @throws Exception 
     */  
    public static void zip(String sourceFolder, String zipFilePath)  
            throws Exception {  
        OutputStream out = new FileOutputStream(zipFilePath);  
        BufferedOutputStream bos = new BufferedOutputStream(out);  
        ZipOutputStream zos = new ZipOutputStream(bos);  
        // 解决中文文件名乱码  
        //zos.setEncoding(CHINESE_CHARSET);  
        File file = new File(sourceFolder);  
        String basePath = "";  
        if (file.isDirectory()) {  
            basePath = file.getPath();  
            System.out.println("文件夹 basePath：" + basePath);  
        } else {  
            basePath = file.getParent().substring(0,  
                    file.getParent().length() - 1);  
            System.out.println("文件 basePath：" + basePath);  
        }  
        zipFile(file, basePath, zos);  
        zos.closeEntry();  
        zos.close();  
        bos.close();  
        out.close();  
    }  
  
    /** 
     * <p> 
     * 递归压缩文件 
     * </p> 
     *  
     * @param parentFile 
     * @param basePath 
     * @param zos 
     * @throws Exception 
     */  
    private static void zipFile(File parentFile, String basePath,  
            ZipOutputStream zos) throws Exception {  
        File[] files = new File[0];  
        if (parentFile.isDirectory()) {  
            files = parentFile.listFiles();  
        } else {  
            files = new File[1];  
            files[0] = parentFile;  
        }  
        String pathName = "";  
        InputStream is = null;  
        BufferedInputStream bis = null;  
        byte[] cache = new byte[CACHE_SIZE];  
        for (File file : files) {  
            if (file.isDirectory()) {  
                pathName = file.getPath().substring(basePath.length() + 1)  
                        + "/";  
                System.out.println("---------------1.文件夹pathName:" + pathName);  
                zos.putNextEntry(new ZipEntry(pathName));  
                zipFile(file, basePath, zos);  
            } else {  
                pathName = file.getPath().substring(basePath.length() + 1);  
                System.out.println("----------------2.文件pathName:" + pathName);  
                is = new FileInputStream(file);  
                bis = new BufferedInputStream(is);  
                zos.putNextEntry(new ZipEntry(pathName));  
                int nRead = 0;  
                while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {  
                    zos.write(cache, 0, nRead);  
                }  
                bis.close();  
                is.close();  
            }  
        }  
    }  
  
    /** 
     * <p> 
     * 解压压缩包 
     * </p> 
     *  
     * @param zipFilePath 
     *            压缩文件路径 
     * @param destDir 
     *            压缩包释放目录 
     * @throws Exception 
     */  
    public static void unZip(String zipFilePath, String destDir)  
            throws Exception {  
        ZipFile zipFile = new ZipFile(zipFilePath);  
        Enumeration<?> emu = zipFile.entries();  
        BufferedInputStream bis = null;  
        FileOutputStream fos = null;  
        BufferedOutputStream bos = null;  
        File file = null;  
        File parentFile = null;  
        ZipEntry entry = null;  
        byte[] cache = new byte[CACHE_SIZE];  
        while (emu.hasMoreElements()) {  
            entry = (ZipEntry) emu.nextElement();  
            if (entry.isDirectory()) {  
                new File(destDir, entry.getName()).mkdirs();  
                continue;  
            }  
            bis = new BufferedInputStream(zipFile.getInputStream(entry));  
            file = new File(destDir, entry.getName());  
            parentFile = file.getParentFile();  
            if (parentFile != null && (!parentFile.exists())) {  
                parentFile.mkdirs();  
            }  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos, CACHE_SIZE);  
            int nRead = 0;  
            while ((nRead = bis.read(cache, 0, CACHE_SIZE)) != -1) {  
                fos.write(cache, 0, nRead);  
            }  
            bos.flush();  
            bos.close();  
            fos.close();  
            bis.close();  
        }  
        zipFile.close();  
    }  
  
    public static void main(String[] args) throws Exception {  
        // String sourceFolder = "E:/logs"; //"E:/logs"  
        // "E:/大型网站技术架构：核心原理与案例分析.pdf"  
        String zipFilePath = "C:\\Users\\SN\\Desktop\\ceshi\\t1.zip"; // "E:/logs.zip"  
                                            // "E:/大型网站技术架构：核心原理与案例分析.zip"  
        String destDir = "C:\\Users\\SN\\Desktop\\ceshi\\";  
        // ZipUtils.zip(sourceFolder, zipFilePath);  
        ZipUtils.unZip(zipFilePath, destDir);  
  
    }  
}
