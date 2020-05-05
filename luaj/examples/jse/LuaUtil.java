import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static java.io.File.separator;

public class LuaUtil {


    public static byte[] readAll(String name) throws IOException {
        InputStream is = new FileInputStream(name);
        byte[] ret = readAll(is);
        is.close();
        return ret;
    }

    public static byte[] readAll(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        byte[] ret = output.toByteArray();
        output.close();
        return ret;
    }

    public static void copyFile(String from, String to) {
        try {
            copyFile(new FileInputStream(from), new FileOutputStream(to));
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    public static boolean copyFile(InputStream in, OutputStream out) {
        try {
            int byteread = 0;
            byte[] buffer = new byte[4096];
            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            //in.close();
            //out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean copyDir(String from, String to) {
        return copyDir(new File(from), new File(to));
    }

    public static boolean copyDir(File from, File to) {
        boolean ret = true;
        File p = to.getParentFile();
        if (!p.exists())
            p.mkdirs();
        if (from.isDirectory()) {
            File[] fs = from.listFiles();
            if (fs != null && fs.length != 0) {
                for (File f : fs)
                    ret = copyDir(f, new File(to, f.getName()));
            } else {
                if (!to.exists())
                    ret = to.mkdirs();
            }
        } else {
            try {
                if (!to.exists())
                    to.createNewFile();
                ret = copyFile(new FileInputStream(from), new FileOutputStream(to));
            } catch (IOException e) {
                e.printStackTrace();
                ret = false;
            }
        }
        return ret;
    }

    public static boolean rmDir(File dir) {
        if (dir.isDirectory()) {
            File[] fs = dir.listFiles();
            for (File f : fs)
                rmDir(f);
        }
        return dir.delete();
    }

    public static void rmDir(File dir, String ext) {
        if (dir.isDirectory()) {
            File[] fs = dir.listFiles();
            for (File f : fs)
                rmDir(f, ext);
            dir.delete();
        }
        if (dir.getName().endsWith(ext))
            dir.delete();
    }

    public static byte[] readZip(String zippath, String filepath) throws IOException {
        ZipFile zip = new ZipFile(zippath);
        ZipEntry entey = zip.getEntry(filepath);
        InputStream is = zip.getInputStream(entey);
        return readAll(is);
    }

// 计算文件的 MD5 值

    public static String getFileMD5(String file) {
        return getFileMD5(new File(file));
    }

    public static String getFileMD5(File file) {
        try {
            return getFileMD5(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static String getFileMD5(InputStream in) {
        byte buffer[] = new byte[4096];
        int len;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // 计算文件的 SHA-1 值
    public static String getFileSha1(String file) {
        return getFileMD5(new File(file));
    }

    public static String getFileSha1(File file) {
        try {
            return getFileSha1(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static String getFileSha1(InputStream in) {
        byte buffer[] = new byte[4096];
        int len;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            while ((len = in.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            BigInteger bigInt = new BigInteger(1, digest.digest());
            return bigInt.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void unZip(String SourceDir) throws IOException {
        unZip(SourceDir, new File(SourceDir).getParent(), "");
    }

    public static void unZip(String SourceDir, boolean bool) throws IOException {
        if (!bool) {
            unZip(SourceDir);
            return;
        }
        String name = new File(SourceDir).getName();
        int i = name.lastIndexOf(".");
        if (i > 0) {
            name = name.substring(0, i);
        }
        i = name.indexOf("_");
        if (i > 0) {
            name = name.substring(0, i);
        }
        i = name.indexOf("(");
        if (i > 0) {
            name = name.substring(0, i);
        }
        unZip(SourceDir, new File(SourceDir).getParent() + separator + name, "");
    }

    public static void unZip(String SourceDir, String extDir) throws IOException {
        unZip(SourceDir, extDir, "");
    }

    public static void unZip(String SourceDir, String extDir, String fileExt) throws IOException {
        ZipFile zip = new ZipFile(SourceDir);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (!name.startsWith(fileExt))
                continue;
            String path = name;
            if (entry.isDirectory()) {
                File f = new File(extDir + separator + path);
                if (!f.exists())
                    f.mkdirs();
            } else {
                String fname = extDir + separator + path;
                File temp = new File(fname).getParentFile();
                if (!temp.exists()) {
                    if (!temp.mkdirs()) {
                        throw new RuntimeException("create file " + temp.getName() + " fail");
                    }
                }

                FileOutputStream out = new FileOutputStream(extDir + separator + path);
                InputStream in = zip.getInputStream(entry);
                byte[] buf = new byte[4096];
                int count = 0;
                while ((count = in.read(buf)) != -1) {
                    out.write(buf, 0, count);
                }
                out.close();
                in.close();
            }
        }
        zip.close();
    }


    private static final byte[] BUFFER = new byte[4096];

    public static boolean zip(String sourceFilePath) {
        return zip(sourceFilePath, new File(sourceFilePath).getParent());
    }

    public static boolean zip(String sourceFilePath, String zipFilePath) {
        File f = new File(sourceFilePath);
        return zip(sourceFilePath, zipFilePath, f.getName() + ".zip");
    }

    public static boolean zip(String sourceFilePath, String zipFilePath, String zipFileName) {
        boolean result = false;
        File source = new File(sourceFilePath);
        File zipFile = new File(zipFilePath, zipFileName);
        if (!zipFile.getParentFile().exists()) {
            if (!zipFile.getParentFile().mkdirs()) {
                return result;
            }
        }
        if (zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                return result;
            }
        }

        FileOutputStream dest = null;
        ZipOutputStream out = null;
        try {
            dest = new FileOutputStream(zipFile);
            CheckedOutputStream checksum = new CheckedOutputStream(dest, new Adler32());
            out = new ZipOutputStream(new BufferedOutputStream(checksum));
            //out.setMethod(ZipOutputStream.DEFLATED);
            compress(source, out, "");
            checksum.getChecksum().getValue();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.closeEntry();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static void compress(File file, ZipOutputStream out, String mainFileName) {
        if (file.isFile()) {
            FileInputStream fi = null;
            BufferedInputStream origin = null;
            try {
                fi = new FileInputStream(file);
                origin = new BufferedInputStream(fi, BUFFER.length);
                //int index=file.getAbsolutePath().indexOf(mainFileName);
                String entryName = mainFileName + file.getName();
                System.out.println(entryName);
                ZipEntry entry = new ZipEntry(entryName);
                out.putNextEntry(entry);
                //			byte[] data = new byte[BUFFER];
                int count;
                while ((count = origin.read(BUFFER, 0, BUFFER.length)) != -1) {
                    out.write(BUFFER, 0, count);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (origin != null) {
                    try {
                        origin.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (file.isDirectory()) {
            File[] fs = file.listFiles();
            if (fs != null && fs.length > 0) {
                for (File f : fs) {
                    if (f.isFile())
                        compress(f, out, mainFileName);
                    else
                        compress(f, out, mainFileName + f.getName() + "/");

                }
            }
        }
    }

    public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();

    static {
        // images
        mFileTypes.put("FFD8FF", "jpg");
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        mFileTypes.put("49492A00", "tif");
        mFileTypes.put("424D", "bmp");
        //other
        mFileTypes.put("41433130", "dwg"); // CAD
        mFileTypes.put("38425053", "psd");
        mFileTypes.put("7B5C727466", "rtf"); // 日记本
        mFileTypes.put("3C3F786D6C", "xml");
        mFileTypes.put("68746D6C3E", "html");
        mFileTypes.put("44656C69766572792D646174653A", "eml"); // 邮件
        mFileTypes.put("D0CF11E0", "doc");
        mFileTypes.put("5374616E64617264204A", "mdb");
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462D312E", "pdf");
        mFileTypes.put("504B0304", "docx");
        mFileTypes.put("52617221", "rar");
        mFileTypes.put("57415645", "wav");
        mFileTypes.put("41564920", "avi");
        mFileTypes.put("2E524D46", "rm");
        mFileTypes.put("000001BA", "mpg");
        mFileTypes.put("000001B3", "mpg");
        mFileTypes.put("6D6F6F76", "mov");
        mFileTypes.put("3026B2758E66CF11", "asf");
        mFileTypes.put("4D546864", "mid");
        mFileTypes.put("1F8B08", "gz");
    }

    public static String getFileType(String path) {
        try {
            return mFileTypes.get(getFileHeader(new FileInputStream(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "unknown";
    }


    public static String getFileType(File file) {
        try {
            return mFileTypes.get(getFileHeader(new FileInputStream(file)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "unknown";
    }

    /**
     * 获取文件类型
     * ps:流会关闭
     *
     * @param inputStream
     * @return
     */
    public static String getFileType(InputStream inputStream) {
        return mFileTypes.get(getFileHeader(inputStream));
    }

    public static String getFileHeader(InputStream inputStream) {
        String value = null;
        try {
            byte[] b = new byte[4];
        /*int read() 从此输入流中读取一个数据字节。
        *int read(byte[] b) 从此输入流中将最多 b.length 个字节的数据读入一个 byte 数组中。
        * int read(byte[] b, int off, int len) 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
        */
            inputStream.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
        }
        return value;
    }

    /**
     * 将要读取文件头信息的文件的byte数组转换成string类型表示
     *
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    private boolean check(int x, int y, int[][] color, int h, int o) {
        for (int i = 0; i < h; i++) {
            if (!(color[x][y + i] == 1 && color[x + o][y + i] == 0)) {
                return false;
            }
        }
        return true;
    }


}
