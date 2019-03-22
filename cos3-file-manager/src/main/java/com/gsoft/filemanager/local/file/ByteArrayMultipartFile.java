package com.gsoft.filemanager.local.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author pilsy
 */
public class ByteArrayMultipartFile implements MultipartFile {

    private final byte[] bytes;

    private String name;

    private String originalFilename;

    private String contentType;

    public ByteArrayMultipartFile(String name, String originalFilename, String contentType, byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("byte[]不能为空！");
        } else {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.bytes = bytes;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        FileOutputStream outputStream = new FileOutputStream(dest);
        try {
            outputStream.write(bytes);
        } finally {
            outputStream.close();
        }
    }

}
