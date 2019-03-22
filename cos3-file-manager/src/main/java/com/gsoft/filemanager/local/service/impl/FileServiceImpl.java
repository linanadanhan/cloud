package com.gsoft.filemanager.local.service.impl;

import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.FileUtils;
import com.gsoft.filemanager.fastdfs.domain.StorePath;
import com.gsoft.filemanager.fastdfs.proto.storage.DownloadByteArray;
import com.gsoft.filemanager.fastdfs.service.FastFileStorageClient;
import com.gsoft.filemanager.local.dto.ChunkDto;
import com.gsoft.filemanager.local.dto.FileMetadataDto;
import com.gsoft.filemanager.local.dto.FileNode;
import com.gsoft.filemanager.local.dto.FileReferenceDto;
import com.gsoft.filemanager.local.entity.Chunk;
import com.gsoft.filemanager.local.entity.FileMetadata;
import com.gsoft.filemanager.local.entity.FileReference;
import com.gsoft.filemanager.local.persistence.FileChunkPersistence;
import com.gsoft.filemanager.local.persistence.FileMetadataPersistence;
import com.gsoft.filemanager.local.persistence.FileReferencePersistence;
import com.gsoft.filemanager.local.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

/**
 * 文件上传下载接口实现类
 */
@Service
public class FileServiceImpl implements FileService {

    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Resource
    FileReferencePersistence fileReferencePersistence;

    @Resource
    FileMetadataPersistence fileMetadataPersistence;

    @Resource
    FileChunkPersistence fileChunkPersistence;

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Value("${file.fileManager.userLocalStorage}")
    private String b;

    @Value("${file.fileManager.catalog}")
    private String catalog;

    @Override
    public FileNode uploadWeb(MultipartFile file, String currentUser) throws IOException, NoSuchAlgorithmException {
        return uploadInputStream(file.getInputStream(), file.getOriginalFilename(), file.getSize(), currentUser);
    }

    @Override
    public FileNode uploadInputStream(InputStream is, String fileName, long size, String currentUser) throws IOException, NoSuchAlgorithmException {
        File file = new File(System.getProperty("java.io.tmpdir") + "upload" + File.separator + "temp_file" + File.separator + UUID.randomUUID().toString());
        FileUtils.copyInputStreamToFile(is, file);
        String md5 = FileUtils.getMD5(file);
        FileMetadata isExistsFile = fileMetadataPersistence.findMetadataByCode(md5);
        FileReferenceDto frd = new FileReferenceDto();
        frd.setCode(md5);
        frd.setName(fileName);
        frd.setSuffix(FileUtils.getFilenameExtension(fileName));
        frd.setReferenceId(UUID.randomUUID().toString());
        Date now = new Date();
        if (Assert.isNotEmpty(currentUser)) {
            frd.setCreateBy(Long.valueOf(currentUser));
            frd.setUpdateBy(Long.valueOf(currentUser));
        }
        frd.setCreateTime(now);
        frd.setUpdateTime(now);
        if (Assert.isNotEmpty(isExistsFile)) {
            FileMetadata fileMetadata = fileMetadataPersistence.findOneByFileCode(md5);
            if (Assert.isNotEmpty(currentUser)) {
                frd.setUpdateBy(Long.valueOf(currentUser));
            }
            fileMetadata.setUpdateTime(now);
            fileMetadataPersistence.save(fileMetadata);
//            FileReference fileReference = fileReferencePersistence.findOneByCode(fileMetadata.getCode());
            FileReference fileReference = fileReferencePersistence.save(BeanUtils.map(frd, FileReference.class));
            FileNode node = new FileNode(fileName, fileReference.getReferenceId(), size);
            FileUtils.deleteQuietly(file);
            return node;
        } else {
            InputStream is2 = new FileInputStream(file);
            StorePath path = null;
            FileMetadataDto fmd = new FileMetadataDto();
            if ("true".equals(b)) {
                String fileDir = System.getProperty("java.io.tmpdir") + "upload" + FileUtils.splitDirPath(md5);
                if (Assert.isNotEmpty(catalog)) {
                    fileDir = catalog + File.separator + "upload" + FileUtils.splitDirPath(md5);
                }
                File file2 = new File(fileDir, md5 + "." + FileUtils.getFilenameExtension(fileName));
                FileUtils.copyFile(file, file2);
                FileUtils.deleteQuietly(file);
                fmd.setPath(fileDir);
            } else {
                path = fastFileStorageClient.uploadFile(is2, size, frd.getSuffix(), null);
                fmd.setPath(path.getPath());
                fmd.setGroup(path.getGroup());
            }
            fmd.setCode(md5);
            fmd.setSize(size);
            if (Assert.isNotEmpty(currentUser)) {
                fmd.setCreateBy(Long.valueOf(currentUser));
                fmd.setUpdateBy(Long.valueOf(currentUser));
            }
            fmd.setCreateTime(now);
            fmd.setUpdateTime(now);

            // TODO 再次判断文件元数据是否已存在，避免并发同一文件插入两笔相同数据
            FileMetadata fileMetadata = fileMetadataPersistence.findMetadataByCode(md5);
            if (Assert.isNotEmpty(fileMetadata)) {
                if (Assert.isNotEmpty(currentUser)) {
                    fileMetadata.setUpdateBy(Long.valueOf(currentUser));
                }
                fileMetadata.setUpdateTime(now);
                fileMetadataPersistence.save(fileMetadata);
//                FileReference fileReference = fileReferencePersistence.findOneByCode(fileMetadata.getCode());
                FileReference fileReference = fileReferencePersistence.save(BeanUtils.map(frd, FileReference.class));
                FileNode node = new FileNode(fileName, fileReference.getReferenceId(), size);
                FileUtils.deleteQuietly(file);
                return node;
            } else {
                fileMetadataPersistence.save(BeanUtils.map(fmd, FileMetadata.class));
                FileReference fileReference = fileReferencePersistence.save(BeanUtils.map(frd, FileReference.class));
                FileNode node = new FileNode(fileName, fileReference.getReferenceId(), size);
                return node;
            }
        }
    }

    @Override
    public InputStream getFileDataByReferenceId(String referenceId) throws FileNotFoundException {
        FileReference fileReference = fileReferencePersistence.findOneFileReference(referenceId);
        FileMetadata fileMetadata = fileMetadataPersistence.findOneByFileCode(fileReference.getCode());
        if ("true".equals(b)) {
            File file = new File(fileMetadata.getPath(), fileReference.getCode() + "." + fileReference.getSuffix());
            return new FileInputStream(file);
        } else {
            byte[] bytes = fastFileStorageClient.downloadFile(fileMetadata.getGroup(), fileMetadata.getPath(), new DownloadByteArray());
            return new ByteArrayInputStream(bytes);
        }
    }

    @Override
    public FileReferenceDto referenceInfo(String referenceId) {
        return BeanUtils.map(fileReferencePersistence.findOneFileReference(referenceId), FileReferenceDto.class);
    }

    @Override
    public void updateFileReference(FileReference fileReference) {
        fileReferencePersistence.updateObj(fileReference.getReferenceId(), fileReference.getCode(), fileReference.getName(),
                fileReference.getType(), fileReference.getUsed(), fileReference.getAppId(), fileReference.getBusinessKey(),
                fileReference.getBusinessType(), fileReference.getSuffix());
    }

    @Override
    public void cleanUpJunkFiles() {
        List<FileMetadata> fmd = fileMetadataPersistence.findAllByUsedNum(0);
        List<FileMetadataDto> fileMetadataDtos = BeanUtils.convert(fmd, FileMetadataDto.class);
        if (Assert.isNotEmpty(fmd)) {
            List<String> codes = new ArrayList<String>();
            for (FileMetadataDto f : fileMetadataDtos) {
                if (Assert.isNotEmpty(f.getCode())) codes.add(f.getCode());
            }
            if (Assert.isNotEmpty(codes)) {
                fileMetadataPersistence.deleteByCodes(codes);
                fileReferencePersistence.deleteByCodes(codes);
            }
        }
        fileReferencePersistence.deleteJunkData();
    }

    @Override
    public List<FileNode> getFileNodesById(String referenceIds) {
        // 解析referenceId
        String[] referenceIdArr = referenceIds.split(",");
        List<FileNode> fileNodeList = null;

        if (!Assert.isEmpty(referenceIdArr) && referenceIdArr.length > 0) {
            fileNodeList = new ArrayList<FileNode>();
            for (String referenceId : referenceIdArr) {
                FileReference fileReference = fileReferencePersistence.findOneFileReference(referenceId);
                FileMetadata fileMetadata = fileMetadataPersistence.findOneByFileCode(fileReference.getCode());
                FileNode node = new FileNode(fileReference.getName(), fileReference.getReferenceId(), fileMetadata.getSize());
                fileNodeList.add(node);
            }
        }
        return fileNodeList;
    }

    @Override
    public FileNode chunkUploadWeb(ChunkDto chunkDto, String currentUser) throws IOException, NoSuchAlgorithmException {
        Chunk chunk = BeanUtils.convert(chunkDto, Chunk.class);
        MultipartFile webFile = chunk.getFile();
        InputStream is = webFile.getInputStream();
        String fileName = webFile.getOriginalFilename();
        long size = webFile.getSize();
        chunk.setType(FileUtils.getFilenameExtension(fileName));
        File file = new File(System.getProperty("java.io.tmpdir") + "upload" + File.separator + "temp_file" + File.separator + UUID.randomUUID().toString());
        FileUtils.copyInputStreamToFile(is, file);
        String md5 = FileUtils.getMD5(file);
        Chunk isExistsFile = fileChunkPersistence.findChunkByCode(md5);

        Date now = new Date();
        if (Assert.isNotEmpty(currentUser)) {
            chunk.setCreateBy(Long.valueOf(currentUser));
            chunk.setUpdateBy(Long.valueOf(currentUser));
        }
        chunk.setCreateTime(now);
        chunk.setUpdateTime(now);
        if (Assert.isNotEmpty(isExistsFile)) {
            Chunk existsChunk = fileChunkPersistence.findOneByFileCode(md5);
            if (Assert.isNotEmpty(currentUser)) {
                chunk.setUpdateBy(Long.valueOf(currentUser));
            }
            existsChunk.setUpdateTime(now);
            fileChunkPersistence.save(existsChunk);
            FileNode node = new FileNode(fileName, existsChunk.getIdentifier(), size);
            FileUtils.deleteQuietly(file);
            return node;
        } else {
            InputStream is2 = new FileInputStream(file);
            StorePath path = null;
            if ("true".equals(b)) {
                String fileDir = System.getProperty("java.io.tmpdir") + "upload" + FileUtils.splitDirPath(md5);
                if (Assert.isNotEmpty(catalog)) {
                    fileDir = catalog + File.separator + "upload" + FileUtils.splitDirPath(md5);
                }
                File file2 = new File(fileDir, md5 + "." + FileUtils.getFilenameExtension(fileName));
                FileUtils.copyFile(file, file2);
                FileUtils.deleteQuietly(file);
                chunk.setRelativePath(fileDir);
            } else {
                path = fastFileStorageClient.uploadFile(is2, size, chunk.getType(), null);
                chunk.setRelativePath(path.getPath());
                chunk.setGroup(path.getGroup());
            }
            chunk.setCode(md5);

            if (Assert.isNotEmpty(currentUser)) {
                chunk.setCreateBy(Long.valueOf(currentUser));
                chunk.setUpdateBy(Long.valueOf(currentUser));
            }
            chunk.setCreateTime(now);
            chunk.setUpdateTime(now);

            // TODO 再次判断文件元数据是否已存在，避免并发同一文件插入两笔相同数据
            Chunk isExistchunk = fileChunkPersistence.findChunkByCode(md5);
            if (Assert.isNotEmpty(isExistchunk)) {
                if (Assert.isNotEmpty(currentUser)) {
                    isExistchunk.setUpdateBy(Long.valueOf(currentUser));
                }
                isExistchunk.setUpdateTime(now);
                fileChunkPersistence.save(isExistchunk);
                FileNode node = new FileNode(fileName, isExistchunk.getIdentifier(), size);
                FileUtils.deleteQuietly(file);
                return node;
            } else {
                Chunk save = fileChunkPersistence.save(chunk);
                FileNode node = new FileNode(fileName, save.getIdentifier(), size);
                return node;
            }
        }
    }

    @Override
    public SequenceInputStream getMergeFileDataByIdentifier(String identifier) throws IOException {
        List<Chunk> chunks = fileChunkPersistence.findOneByIdentifier(identifier);
        Vector<InputStream> v = new Vector<>();

        if ("true".equals(b)) {
            chunks.stream()
                    .sorted(Comparator.comparing(Chunk::getChunkNumber))
                    .forEach(chunk -> {
                        File file = new File(chunk.getRelativePath(), chunk.getCode() + "." + chunk.getType());
                        try {
                            v.add(new FileInputStream(file));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            chunks.stream()
                    .sorted(Comparator.comparing(Chunk::getChunkNumber))
                    .forEach(chunk -> {
                        byte[] bytes = fastFileStorageClient.downloadFile(chunk.getGroup(), chunk.getRelativePath(), new DownloadByteArray());
                        v.add(new ByteArrayInputStream(bytes));
                    });
        }
        Enumeration<InputStream> enumeration = v.elements();
        SequenceInputStream stream = new SequenceInputStream(enumeration);
        return stream;
    }

    @Override
    public List<ChunkDto> identifierInfo(String identifier) {
        return BeanUtils.map(fileChunkPersistence.findOneByIdentifier(identifier), ChunkDto.class);
    }

    @Override
    public List<FileNode> getFileNodesByIdentifier(String identifier) {
        String[] referenceIdArr = identifier.split(",");
        List<FileNode> fileNodeList = null;

        if (!Assert.isEmpty(referenceIdArr) && referenceIdArr.length > 0) {
            fileNodeList = new ArrayList<FileNode>();
            for (String referenceId : referenceIdArr) {

                List<Chunk> chunks = fileChunkPersistence.findOneByIdentifier(referenceId);
                if (Assert.isNotEmpty(chunks) && chunks.size() > 0) {
                    Chunk chunk = chunks.get(0);
                    FileNode fileNode = new FileNode(chunk.getFilename(), chunk.getIdentifier(), chunk.getTotalSize());
                    fileNodeList.add(fileNode);
                }
            }
        }
        return fileNodeList;
    }

    @Override
    public InputStream getFileChunkByNumber(String identifier, Integer chunkNumber) throws FileNotFoundException {
        Chunk chunk = fileChunkPersistence.checkChunk(identifier, chunkNumber);
        if ("true".equals(b)) {
            File file = new File(chunk.getRelativePath(), chunk.getCode() + "." + chunk.getType());
            return new FileInputStream(file);
        } else {
            byte[] bytes = fastFileStorageClient.downloadFile(chunk.getGroup(), chunk.getRelativePath(), new DownloadByteArray());
            return new ByteArrayInputStream(bytes);
        }
    }

    @Override
    public ChunkDto getChunkByNumber(String identifier, Integer chunkNumber) {
        return BeanUtils.convert(fileChunkPersistence.checkChunk(identifier, chunkNumber), ChunkDto.class);
    }

    @Override
    public int getChunkNumber(String identifier) {
        return fileChunkPersistence.getChunkNumber(identifier);
    }
}
