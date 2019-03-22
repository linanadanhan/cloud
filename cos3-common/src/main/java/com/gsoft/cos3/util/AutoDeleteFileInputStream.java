package com.gsoft.cos3.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 关闭输入流时，将对应的文件删除
 * 
 * @author shencq
 * 
 */
public class AutoDeleteFileInputStream extends FileInputStream {

	private File file;

	public AutoDeleteFileInputStream(File file) throws FileNotFoundException {
		super(file);
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FileInputStream#close()
	 */
	@Override
	public void close() throws IOException {
		super.close();
		FileUtils.deleteQuietly(file);
	}

}