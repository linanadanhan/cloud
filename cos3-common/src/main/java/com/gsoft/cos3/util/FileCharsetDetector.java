package com.gsoft.cos3.util;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileCharsetDetector {
	private static boolean found = false;
	private static String encoding = null;

	
	public static String getCharset(InputStream is) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(is);
		int p = (bin.read() << 8) + bin.read();
		String code = null;
		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}
		return code;
	}
	
	public static void main(String[] argv) throws Exception {
		File file1 = new File(
				"D:\\4-tempfile\\数据交换\\发放清册(5201)-解密后的报文-样例.xml");

		System.out.println("文件编码:"
				+ FileCharsetDetector.getCharset(new FileInputStream(
						file1)));
	}

	/**
	 * 传入一个文件(File)对象，检查文件编码
	 * 
	 * @param file
	 *            File对象实例
	 * @return 文件编码，若无，则返回null
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String guessFileEncoding(File file)
			throws FileNotFoundException, IOException {
		return guessFileEncoding(new FileInputStream(file), new nsDetector());
	}

	/**
	 * 传入一个文件流对象，检查文件编码
	 * 
	 * @param is
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static String guessFileEncoding(InputStream is)
			throws FileNotFoundException, IOException {
		String result = guessFileEncoding(is, new nsDetector());
		if(result == null || !result.toLowerCase().equals("utf-8")) {
			result = "gbk";
		}
		return result;
	}

	/**
	 * <pre>
	 * 获取文件的编码
	 * @param file
	 *            File对象实例
	 * @param languageHint
	 *            语言提示区域代码 @see #nsPSMDetector ,取值如下：
	 *             1 : Japanese
	 *             2 : Chinese
	 *             3 : Simplified Chinese
	 *             4 : Traditional Chinese
	 *             5 : Korean
	 *             6 : Dont know(default)
	 * </pre>
	 * 
	 * @return 文件编码，eg：UTF-8,GBK,GB2312形式(不确定的时候，返回可能的字符编码序列)；若无，则返回null
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public String guessFileEncoding(InputStream is, int languageHint)
			throws FileNotFoundException, IOException {
		return guessFileEncoding(is, new nsDetector(languageHint));
	}

	/**
	 * 获取文件的编码
	 * 
	 * @param file
	 * @param det
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static String guessFileEncoding(InputStream is, nsDetector det)
			throws FileNotFoundException, IOException {
		det.Init(new nsICharsetDetectionObserver() {
			public void Notify(String charset) {
				encoding = charset;
				found = true;
			}
		});

		BufferedInputStream imp = new BufferedInputStream(is);
		byte[] buf = new byte[1024];
		int len;
		boolean done = false;
		boolean isAscii = false;

		while ((len = imp.read(buf, 0, buf.length)) != -1) {
			// Check if the stream is only ascii.
			isAscii = det.isAscii(buf, len);
			if (isAscii) {
				break;
			}
			// DoIt if non-ascii and not done yet.
			done = det.DoIt(buf, len, false);
			if (done) {
				break;
			}
		}
		imp.close();
		det.DataEnd();

		if (isAscii) {
			encoding = "ASCII";
			found = true;
		}

		if (!found) {
			String[] prob = det.getProbableCharsets();
			// 这里将可能的字符集组合起来返回
			for (int i = 0; i < prob.length; i++) {
				if (i == 0) {
					encoding = prob[i];
				} else {
					encoding += "," + prob[i];
				}
			}

			if (prob.length > 0) {
				// 在没有发现情况下,也可以只取第一个可能的编码,这里返回的是一个可能的序列
				return encoding;
			} else {
				return null;
			}
		}
		return encoding;
	}
}