package com.gsoft.filemanager.local.entity;


import com.gsoft.cos3.entity.BaseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 文件分块
 * @author pilsy
 */
@Entity
@Table(name = "COS_FILE_CHUNK")
public class Chunk extends BaseEntity {

    /**
     * 指纹码
     */
    @Column(name = "c_code")
    private String code;

    /**
     * 文件的分组
     */
    @Column(name = "c_group")
    private String group;

    /**
     * 文件是否加密
     */
    @Column(name = "c_encrypt")
    private Boolean encrypt;

    /**
     * 当前文件块，从1开始
     */
    @Column(name = "c_chunk_number")
    private Integer chunkNumber;
    /**
     * 未编码的分块大小
     */
    @Column(name = "c_chunk_size")
    private Long chunkSize;
    /**
     * 当前分块大小
     */
    @Column(name = "c_current_chunk_size")
    private Long currentChunkSize;
    /**
     * 未切分，未编码的整个文件总大小
     */
    @Column(name = "c_total_size")
    private Long totalSize;
    /**
     * 文件标识，通过此标识下载，不能重复
     */
    @Column(name = "c_identifier")
    private String identifier;
    /**
     * 文件名
     */
    @Column(name = "c_filename")
    private String filename;
    /**
     * 相对路径
     */
    @Column(name = "c_relative_path")
    private String relativePath;
    /**
     * 总块数
     */
    @Column(name = "c_total_chunks")
    private Integer totalChunks;
    /**
     * 文件类型
     */
    @Column(name = "c_type")
    private String type;

    @Transient
    private MultipartFile file;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getChunkNumber() {
        return chunkNumber;
    }

    public void setChunkNumber(Integer chunkNumber) {
        this.chunkNumber = chunkNumber;
    }

    public Long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public Integer getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(Integer totalChunks) {
        this.totalChunks = totalChunks;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Long getCurrentChunkSize() {
        return currentChunkSize;
    }

    public void setCurrentChunkSize(Long currentChunkSize) {
        this.currentChunkSize = currentChunkSize;
    }

    public Boolean getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(Boolean encrypt) {
        this.encrypt = encrypt;
    }
}
