package com.mola.molachat.common.utils;

import net.coobird.thumbnailator.Thumbnails;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author : molamola
 * @Project: molachat
 * @Description:
 * @date : 2021-03-25 01:06
 **/
public class FileUtils {

    private static Set<String> imageSuffixSet = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png","bmp","gif")
    );

    /**
     * 文件或目录是否存在
     */
    public static boolean exists(String path) {
        return new File(path).exists();
    }

    /**
     * 文件是否存在
     */
    public static boolean existsFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    /**
     * 文件或目录是否存在
     */
    public static boolean existsAny(String... paths) {
        return Arrays.stream(paths).anyMatch(path -> new File(path).exists());
    }

    /**
     * 删除文件或文件夹
     */
    public static void deleteIfExists(File file) throws IOException {
        if (file.exists()) {
            if (file.isFile()) {
                if (!file.delete()) {
                    throw new IOException("Delete file failure,path:" + file.getAbsolutePath());
                }
            } else {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File temp : files) {
                        deleteIfExists(temp);
                    }
                }
                if (!file.delete()) {
                    throw new IOException("Delete file failure,path:" + file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * 删除文件或文件夹
     */
    public static void deleteIfExists(String path) throws IOException {
        deleteIfExists(new File(path));
    }


    /**
     * 查看文件或者文件夹大小
     */
    public static long getFileSize(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                return file.length();
            } else {
                long size = 0;
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File temp : files) {
                        if (temp.isFile()) {
                            size += temp.length();
                        }
                    }
                }
                return size;
            }
        }
        return 0;
    }

    public static File createFileSmart(String path) throws IOException {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                createDirSmart(file.getParent());
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            throw new IOException("createFileSmart=" + path, e);
        }
    }

    public static File createDirSmart(String path) throws IOException {
        try {
            File file = new File(path);
            if (!file.exists()) {
                Stack<File> stack = new Stack<>();
                File temp = new File(path);
                while (temp != null) {
                    stack.push(temp);
                    temp = temp.getParentFile();
                }
                while (stack.size() > 0) {
                    File dir = stack.pop();
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                }
            }
            return file;
        } catch (Exception e) {
            throw new IOException("createDirSmart=" + path, e);
        }
    }

    /**
     * 获取目录所属磁盘剩余容量
     */
    public static long getDiskFreeSize(String path) {
        File file = new File(path);
        return file.getFreeSpace();
    }

    public static void unmap(MappedByteBuffer mappedBuffer) throws IOException {
        try {
            Class<?> clazz = Class.forName("sun.nio.ch.FileChannelImpl");
            Method m = clazz.getDeclaredMethod("unmap", MappedByteBuffer.class);
            m.setAccessible(true);
            m.invoke(clazz, mappedBuffer);
        } catch (Exception e) {
            throw new IOException("LargeMappedByteBuffer close", e);
        }
    }

    /**
     * 去掉后缀名
     */
    public static String getFileNameNoSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(0, index);
        }
        return fileName;
    }

    public static boolean canWrite(String path) {
        File file = new File(path);
        File test;
        if (file.isFile()) {
            test = new File(
                    file.getParent() + File.separator + UUID.randomUUID().toString() + ".test");
        } else {
            test = new File(file.getPath() + File.separator + UUID.randomUUID().toString() + ".test");
        }
        try {
            test.createNewFile();
            test.delete();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void unzip(String zipPath, String toPath, String... unzipFile) throws IOException {
        try (
                ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipPath))
        ) {
            toPath = toPath == null ? new File(zipPath).getParent() : toPath;
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                final String entryName = entry.getName();
                if (entry.isDirectory() || (unzipFile != null && unzipFile.length > 0
                        && Arrays.stream(unzipFile)
                        .noneMatch((file) -> entryName.equalsIgnoreCase(file)))) {
                    zipInputStream.closeEntry();
                    continue;
                }
                File file = createFileSmart(toPath + File.separator + entryName);
                try (
                        FileOutputStream outputStream = new FileOutputStream(file)
                ) {
                    byte[] bts = new byte[8192];
                    int len;
                    while ((len = zipInputStream.read(bts)) != -1) {
                        outputStream.write(bts, 0, len);
                    }
                }
            }
        }
    }

    /**
     * 判断文件存在是重命名
     */
    public static String renameIfExists(String path) {
        File file = new File(path);
        if (file.exists() && file.isFile()) {
            int index = file.getName().lastIndexOf(".");
            String name = file.getName().substring(0, index);
            String suffix = index == -1 ? "" : file.getName().substring(index);
            int i = 1;
            String newName;
            do {
                newName = name + "(" + i + ")" + suffix;
                i++;
            }
            while (existsFile(file.getParent() + File.separator + newName));
            return newName;
        }
        return file.getName();
    }

    /**
     * 创建指定大小的Sparse File
     */
    public static void createFileWithSparse(String filePath, long length) throws IOException {
        Path path = Paths.get(filePath);
        try {
            Files.deleteIfExists(path);
            try (
                    SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, StandardOpenOption.SPARSE)
            ) {
                channel.position(length - 1);
                channel.write(ByteBuffer.wrap(new byte[]{0}));
            }
        } catch (IOException e) {
            throw new IOException("create spares file fail,path:" + filePath + " length:" + length, e);
        }
    }

    /**
     * 使用RandomAccessFile创建指定大小的File
     */
    public static void createFileWithDefault(String filePath, long length) throws IOException {
        Path path = Paths.get(filePath);
        try {
            Files.deleteIfExists(path);
            try (
                    RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "rw");
            ) {
                randomAccessFile.setLength(length);
            }
        } catch (IOException e) {
            throw new IOException("create spares file fail,path:" + filePath + " length:" + length, e);
        }
    }

    public static String getSystemFileType(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file = file.getParentFile();
        }
        return Files.getFileStore(file.toPath()).type();
    }

    public static void imageFileCompress(String fromPic, String toPic, Long fileSize) throws IOException {
        float rate = 1f;
        if (fileSize > 5000000L) { // 大于5m
            rate = 0.1f;
        } else if (fileSize > 1000000L) {
            rate = 0.2f;
        } else if (fileSize > 500000L) {
            rate = 0.5f;
        } else if (fileSize > 250000L) {
            rate = 0.75f;
        }
        Thumbnails.of(fromPic)
                .scale(1f)
                .outputQuality(rate)
                .toFile(toPic);
    }

    public static boolean isImage(String fileName) {
        String lower = fileName.toLowerCase();
        for (String suffix : imageSuffixSet) {
            if (lower.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 读取文件内容，作为字符串返回
     */
    public static String readFileAsString(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        }

        if (file.length() > 1024 * 1024 * 1024) {
            throw new IOException("File is too large");
        }

        StringBuilder sb = new StringBuilder((int) (file.length()));
        // 创建字节输入流
        FileInputStream fis = new FileInputStream(filePath);
        // 创建一个长度为10240的Buffer
        byte[] bbuf = new byte[10240];
        // 用于保存实际读取的字节数
        int hasRead = 0;
        while ( (hasRead = fis.read(bbuf)) > 0 ) {
            sb.append(new String(bbuf, 0, hasRead));
        }
        fis.close();
        return sb.toString();
    }

    /**
     * 根据文件路径读取byte[] 数组
     */
    public static byte[] readFileByBytes(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException(filePath);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
            BufferedInputStream in = null;

            try {
                in = new BufferedInputStream(new FileInputStream(file));
                short bufSize = 1024;
                byte[] buffer = new byte[bufSize];
                int len1;
                while (-1 != (len1 = in.read(buffer, 0, bufSize))) {
                    bos.write(buffer, 0, len1);
                }

                byte[] var7 = bos.toByteArray();
                return var7;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException var14) {
                    var14.printStackTrace();
                }

                bos.close();
            }
        }
    }
}
