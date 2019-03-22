/**
 * 
 */
package com.gsoft.cos3.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.codec.binary.Hex;
import org.springframework.util.StringUtils;

/**
 * 文件相关处理工具类
 * 
 * @author shencq
 *
 */
public class FileUtils extends org.apache.commons.io.FileUtils {
	/**
	 * 创建临时文件
	 * 
	 * @param prefix
	 *            文件名前缀
	 * @param suffix
	 *            文件扩展名
	 * @return 空文件
	 * @throws IOException
	 */
	public static File createTempFile(String prefix, String suffix)
			throws IOException {
		return File.createTempFile(prefix, suffix);
	}

	/**
	 * 在指定目录下创建临时文件。
	 * 
	 * @param prefix
	 *            文件名前缀
	 * @param suffix
	 *            文件扩展名
	 * @param directory
	 *            文件夹
	 * @return 空文件
	 * @throws IOException
	 */
	public static File createTempFile(String prefix, String suffix,
			File directory) throws IOException {
		return File.createTempFile(prefix, suffix, directory);
	}

	/**
	 * 创建一个在文件流关闭时，自动删除对应文件的输入流。
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	public static AutoDeleteFileInputStream createAutoDeleteFileInputStream(
			File file) throws FileNotFoundException {
		return new AutoDeleteFileInputStream(file);
	}

	/**
	 * 获取文件的后缀，
	 * 
	 * @param fileName
	 * @deprecated 请使用getFilenameExtension方法替代
	 * @return
	 */
	public static String getSuffix(String fileName) {
		return getFilenameExtension(fileName);
	}

	/**
	 * 获取文件的后缀
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFilenameExtension(String fileName) {
		return StringUtils.getFilenameExtension(fileName);
	}

	/**
	 * 描述：生成指纹码（MD5, Hex）.
	 * 
	 * @param file
	 * @return String
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String getMD5(File file) throws IOException,
			NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.reset();

		byte[] buffer = new byte[2048];
		int n = 0;
		InputStream is = null;
		try {
			is = FileUtils.openInputStream(file);
			while (-1 != (n = is.read(buffer))) {
				digest.update(buffer, 0, n);
			}
		} finally {
			IOUtils.closeQuietly(is);
		}
		return toHex(digest.digest());
	}

	/**
	 * 描述：生成指纹码（MD5, Hex）.
	 * 
	 * @param file
	 * @return String
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 */
	public static String getMD5(InputStream is) throws IOException,
			NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.reset();

		byte[] buffer = new byte[2048];
		int n = 0;
		try {
			while (-1 != (n = is.read(buffer))) {
				digest.update(buffer, 0, n);
			}
		} finally {
			IOUtils.closeQuietly(is);
		}
		return toHex(digest.digest());
	}

	public static String getMD5(byte[] source) {
		String s = null;
		char hexDigits[] = { // 用来将字节转换成 16 进制表示的字符
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
			// 用字节表示就是 16 个字节
			char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
			// 所以表示成 16 进制需要 32 个字符
			int k = 0; // 表示转换结果中对应的字符位置
			for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
				// 转换成 16 进制字符的转换
				byte byte0 = tmp[i]; // 取第 i 个字节
				str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
				// >>> 为逻辑右移，将符号位一起右移
				str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
			}
			s = new String(str); // 换后的结果转换为字符串

		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * 将字节数组转换成16进制字符串
	 * 
	 * @param data
	 * @return
	 */
	public static String toHex(byte[] data) {
		return Hex.encodeHexString(data);
	}

	/**
	 * 按文件名分解目录
	 * 
	 * @param md5
	 * @return
	 */
	public static String splitDirPath(String md5) {
		if (Assert.isEmpty(md5) || md5.length() < 4) {
			return "";
		}
		return File.separator + md5.substring(0, 4) + File.separator
				+ md5.substring(4, 8) + File.separator;
	}

	/**
	 * 获取MimeType文件地址
	 * 
	 * @author Jasun 2015年9月16日 上午9:50:23
	 * @return
	 */
	private static String getMimeFilePath() {
		String mimeFilePath = FileUtils.class.getResource("/").getPath()
				+ "mime.types";
		if (!new File(mimeFilePath).exists()) {
			mimeFilePath = FileUtils.class.getResource("").getPath();
			if (Assert.isNotEmpty(mimeFilePath)) {
				if (mimeFilePath.indexOf("/lib/") > 0) {
					mimeFilePath = mimeFilePath.substring(0,
							mimeFilePath.indexOf("/lib/"))
							+ File.separator + "classes/mime.types";
					if (mimeFilePath.startsWith("file:")) {
						mimeFilePath = mimeFilePath.substring("file:".length());
					}
				}
			}
		}
		return mimeFilePath;
	}

	/**
	 * 根据文件名称返回相应mimeType文件类型
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getContentType(String fileName) {
		MimetypesFileTypeMap mimetypesFileTypeMap = null;
		try {
			mimetypesFileTypeMap = new MimetypesFileTypeMap(getMimeFilePath());
		} catch (IOException e) {
			e.printStackTrace();
			mimetypesFileTypeMap = new MimetypesFileTypeMap();
		}
		return mimetypesFileTypeMap.getContentType(fileName);
	}

	/**
	 * 根据文件后缀返回对应MimeType文件类型，多个用逗号分割
	 * 
	 * @author Jasun 2015年9月16日 上午9:52:53
	 * @param suffix
	 *            文件后缀，多个用逗号分割
	 * @return 返回对应MimeType类型，多个用逗号分割
	 */
	public static String getAllContentType(String suffix) {
		MimetypesFileTypeMap mimetypesFileTypeMap = null;
		try {
			mimetypesFileTypeMap = new MimetypesFileTypeMap(getMimeFilePath());
		} catch (IOException e) {
			e.printStackTrace();
			mimetypesFileTypeMap = new MimetypesFileTypeMap();
		}
		String[] suffixs = suffix.split(",");
		StringBuilder sb = new StringBuilder();
		for (String suf : suffixs) {
			String mimeType = mimetypesFileTypeMap.getContentType("." + suf);
			if (sb.indexOf(mimeType) == -1)
				sb.append(mimeType + ",");
		}
		return sb.substring(0, sb.length() - 1);
	}
	
	/**
	 * java InputStream 转file
	 * @param ins
	 * @param file
	 * @throws IOException
	 */
	public static void inputstreamToFile(InputStream ins, File file) throws IOException {
		
		OutputStream os = new FileOutputStream(file);
		int bytesRead = 0;
		byte[] buffer = new byte[8192];
		while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
			os.write(buffer, 0, bytesRead);
		}

		os.close();
		ins.close();
	}
	
    /**
     *  复制一个目录及其子目录、文件到另外一个目录
     * @param [参数1]     [参数1说明]
     * @param [参数2]     [参数2说明]
     * @return [返回类型说明]
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void copyFolder(File src, File dest) throws IOException
    {
        if (src.isDirectory())
        {
            if (!dest.exists())
            {
                dest.mkdir();
            }
            
            String files[] = src.list();
            for (String file : files)
            {
            	if (".svn".equals(file)) {
            		continue;
            	}
            	
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        }
        else
        {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;

            while ((length = in.read(buffer)) > 0)
            {
                out.write(buffer, 0, length);
            }
            
            in.close();
            out.close();
        }
    }
}
