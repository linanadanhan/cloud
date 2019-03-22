package com.gsoft.filemanager.local.controller;

import com.gsoft.cos3.dto.ReturnDto;
import com.gsoft.cos3.useragent.UserAgent;
import com.gsoft.cos3.util.Assert;
import com.gsoft.cos3.util.BeanUtils;
import com.gsoft.cos3.util.FileCharsetDetector;
import com.gsoft.cos3.util.FileUtils;
import com.gsoft.cos3.util.IOUtils;
import com.gsoft.filemanager.local.dto.ChunkDto;
import com.gsoft.filemanager.local.dto.DesktopChunkDto;
import com.gsoft.filemanager.local.dto.FileNode;
import com.gsoft.filemanager.local.dto.FileReferenceDto;
import com.gsoft.filemanager.local.file.ByteArrayMultipartFile;
import com.gsoft.filemanager.local.file.CBase64;
import com.gsoft.filemanager.local.service.FileService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    FileService fileService;

    @RequestMapping("/uploadFile")
    @CrossOrigin
    public FileNode webUploader(MultipartFile file, HttpServletRequest request)
            throws IOException, NoSuchAlgorithmException {
        return fileService.uploadWeb(file, request.getHeader("personnelId"));
    }

    @PostMapping("/chunk")
    public FileNode chunkUploader(ChunkDto chunk, HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
        return fileService.chunkUploadWeb(chunk, request.getHeader("personnelId"));
    }

    @RequestMapping("/desktopChunk")
    public FileNode chunkUploader(DesktopChunkDto desktopChunkDto) throws IOException, NoSuchAlgorithmException {
        String data = desktopChunkDto.getFile();
        int chunkSize = Math.toIntExact(desktopChunkDto.getChunkSize());

        CBase64 cBase64 = new CBase64();
        byte[] decode = cBase64.decode(data, chunkSize);
        byte[] bytes = new byte[chunkSize];
        System.arraycopy(decode, 0, bytes, 0, chunkSize);

        String fileName = desktopChunkDto.getFilename();
        ByteArrayMultipartFile byteArrayMultipartFile = new ByteArrayMultipartFile("file", fileName, "application/octet-stream", bytes);
        ChunkDto chunkDto = BeanUtils.convert(desktopChunkDto, ChunkDto.class);
        chunkDto.setFile(byteArrayMultipartFile);
        return fileService.chunkUploadWeb(chunkDto, null);
    }

    @RequestMapping("/getChunkNumber/{identifier}")
    public int getChunkNumber(@PathVariable("identifier") String identifier) {
        return fileService.getChunkNumber(identifier);
    }

    @RequestMapping("/downloadChunkFromNumber/{identifier}")
    public void downloadChunkFromNumber(HttpServletRequest request, HttpServletResponse response, @PathVariable("identifier") String identifier, @RequestParam Integer chunkNumber) throws IOException {
        String fileName = request.getParameter("fileName");
        if (Assert.isEmpty(fileName)) {
            fileName = request.getHeader("fileName");
            if (!Assert.isEmpty(fileName)) {
                fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
            }
        }
        ChunkDto chunkDto = fileService.getChunkByNumber(identifier, chunkNumber);
        if (Assert.isEmpty(fileName)) {
            fileName = chunkDto.getFilename();
        }

        // 浏览器内打开还是直接下载，根据参数 inline
        String inlineStr = request.getParameter("inline");
        Boolean inline = false;
        if (Assert.isNotEmpty(inlineStr)) {
            try {
                inline = Boolean.parseBoolean(inlineStr.toString());
            } catch (Exception e) {
                inline = false;
            }
        }

        String userAgentStr = request.getHeader("USER-AGENT");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        if (userAgent.getBrowser().isIE()) { // IE
            String enableFileName = new String(fileName.getBytes("GBK"),
                    "ISO-8859-1");
            if (inline) {
                response.setHeader("Content-Disposition",
                        "inline;" + "filename=" + enableFileName);
            } else {
                response.setHeader("Content-Disposition",
                        "attachment;" + "filename=" + enableFileName);
            }
        } else if (userAgent.getBrowser().isFireFox()) { // 火狐
            String enableFileName = "=?UTF-8?B?"
                    + (new String(
                    Base64.encodeBase64(fileName.getBytes("UTF-8"))))
                    + "?=";
            if (inline) {
                response.setHeader("Content-Disposition",
                        "inline;" + "filename=" + enableFileName);
            } else {
                response.setHeader("Content-Disposition",
                        "attachment;" + "filename=" + enableFileName);
            }
        } else { // 其他浏览器
            if (inline) {
                response.addHeader("Content-Disposition", "inline;filename="
                        + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            } else {
                response.addHeader("Content-Disposition", "attachment;filename="
                        + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            }
        }
        InputStream fileDataIs = fileService.getFileChunkByNumber(identifier, chunkNumber);

        ServletOutputStream os = null;
        int fileSize = 0;
        try {
            fileSize = chunkDto.getCurrentChunkSize().intValue();
            String contentType = FileUtils.getContentType(fileName);
            response.setContentType(contentType);
            response.addHeader("Content-Length", "" + fileSize);
            if (inline) {
                response.setCharacterEncoding(FileCharsetDetector.guessFileEncoding(fileDataIs));
            }
            os = response.getOutputStream();
            IOUtils.copy(fileDataIs, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileDataIs);
            IOUtils.closeQuietly(os);
        }
    }


    @RequestMapping("/downloadChunk/{identifier}")
    public void downloadChunk(HttpServletRequest request, HttpServletResponse response, @PathVariable("identifier") String identifier) throws IOException {
        String fileName = request.getParameter("fileName");
        if (Assert.isEmpty(fileName)) {
            fileName = request.getHeader("fileName");
            if (!Assert.isEmpty(fileName)) {
                fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
            }
        }
        List<ChunkDto> dto = fileService.identifierInfo(identifier);
        if (Assert.isEmpty(fileName)) {
            if (Assert.isNotEmpty(dto) && dto.size() > 0) {
                fileName = dto.get(0).getFilename();
            }
        }

        // 浏览器内打开还是直接下载，根据参数 inline
        String inlineStr = request.getParameter("inline");
        Boolean inline = false;
        if (Assert.isNotEmpty(inlineStr)) {
            try {
                inline = Boolean.parseBoolean(inlineStr.toString());
            } catch (Exception e) {
                inline = false;
            }
        }

        String userAgentStr = request.getHeader("USER-AGENT");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        if (userAgent.getBrowser().isIE()) { // IE
            String enableFileName = new String(fileName.getBytes("GBK"),
                    "ISO-8859-1");
            if (inline) {
                response.setHeader("Content-Disposition",
                        "inline;" + "filename=" + enableFileName);
            } else {
                response.setHeader("Content-Disposition",
                        "attachment;" + "filename=" + enableFileName);
            }
        } else if (userAgent.getBrowser().isFireFox()) { // 火狐
            String enableFileName = "=?UTF-8?B?"
                    + (new String(
                    Base64.encodeBase64(fileName.getBytes("UTF-8"))))
                    + "?=";
            if (inline) {
                response.setHeader("Content-Disposition",
                        "inline;" + "filename=" + enableFileName);
            } else {
                response.setHeader("Content-Disposition",
                        "attachment;" + "filename=" + enableFileName);
            }
        } else { // 其他浏览器
            if (inline) {
                response.addHeader("Content-Disposition", "inline;filename="
                        + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            } else {
                response.addHeader("Content-Disposition", "attachment;filename="
                        + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            }
        }
        SequenceInputStream fileDataIs = fileService.getMergeFileDataByIdentifier(identifier);

        ServletOutputStream os = null;
        int fileSize = 0;
        try {
            fileSize = dto.get(0).getTotalSize().intValue();
            String contentType = FileUtils.getContentType(fileName);
            response.setContentType(contentType);
            response.addHeader("Content-Length", "" + fileSize);
            if (inline) {
                response.setCharacterEncoding(
                        FileCharsetDetector.guessFileEncoding(fileDataIs));
            }
            os = response.getOutputStream();
            int len;
            byte[] arr = new byte[1024 * 10];
            while ((len = fileDataIs.read(arr)) != -1) {
                os.write(arr, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileDataIs);
            IOUtils.closeQuietly(os);
        }

    }

    @RequestMapping("/download/{referenceId}")
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable("referenceId") String referenceId) throws IOException {
        InputStream fileDataIs = fileService
                .getFileDataByReferenceId(referenceId);
        String fileName = request.getParameter("fileName");
        if (Assert.isEmpty(fileName)) {
            fileName = request.getHeader("fileName");
            if (!Assert.isEmpty(fileName)) {
                fileName = new String(fileName.getBytes("ISO-8859-1"), "UTF-8");
            }
        }
        if (Assert.isEmpty(fileName)) {
            FileReferenceDto dto = fileService.referenceInfo(referenceId);
            fileName = dto.getName();
        }

        // 浏览器内打开还是直接下载，根据参数 inline
        String inlineStr = request.getParameter("inline");
        Boolean inline = false;
        if (Assert.isNotEmpty(inlineStr)) {
            try {
                inline = Boolean.parseBoolean(inlineStr.toString());
            } catch (Exception e) {
                inline = false;
            }
        }

        String userAgentStr = request.getHeader("USER-AGENT");
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentStr);
        if (userAgent.getBrowser().isIE()) { // IE
            String enableFileName = new String(fileName.getBytes("GBK"),
                    "ISO-8859-1");
            if (inline) {
                response.setHeader("Content-Disposition",
                        "inline;" + "filename=" + enableFileName);
            } else {
                response.setHeader("Content-Disposition",
                        "attachment;" + "filename=" + enableFileName);
            }
        } else if (userAgent.getBrowser().isFireFox()) { // 火狐
            String enableFileName = "=?UTF-8?B?"
                    + (new String(
                    Base64.encodeBase64(fileName.getBytes("UTF-8"))))
                    + "?=";
            if (inline) {
                response.setHeader("Content-Disposition",
                        "inline;" + "filename=" + enableFileName);
            } else {
                response.setHeader("Content-Disposition",
                        "attachment;" + "filename=" + enableFileName);
            }
        } else { // 其他浏览器
            if (inline) {
                response.addHeader("Content-Disposition", "inline;filename="
                        + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            } else {
                response.addHeader("Content-Disposition", "attachment;filename="
                        + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
            }
        }

        ServletOutputStream os = null;
        int fileSize = 0;
        try {
            fileSize = fileDataIs.available();
            String contentType = FileUtils.getContentType(fileName);
            response.setContentType(contentType);
            response.addHeader("Content-Length", "" + fileSize);
            if (inline) {
                response.setCharacterEncoding(
                        FileCharsetDetector.guessFileEncoding(fileService
                                .getFileDataByReferenceId(referenceId)));
            }
            os = response.getOutputStream();
            IOUtils.copy(fileDataIs, os);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(fileDataIs);
            IOUtils.closeQuietly(os);
        }
    }

    @RequestMapping("/downloadByReferenceId")
    public ResponseEntity<byte[]> downloadByReferenceId(@RequestParam String referenceId) throws IOException {
        InputStream fileDataIs = fileService.getFileDataByReferenceId(referenceId);
        byte[] bytes = input2byte(fileDataIs);
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(bytes, new HttpHeaders(), HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping("/getFileByRefId/{referenceId}")
    public byte[] getFilePathByRefId(@PathVariable("referenceId") String referenceId) throws IOException {
        InputStream fileDataIs = fileService
                .getFileDataByReferenceId(referenceId);

        return input2byte(fileDataIs);
    }

    /**
     * 文件流转字节
     * [功能详细描述]
     *
     * @param inStream [参数1说明]
     * @return [返回类型说明]
     * @exception/throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();

        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();

        return in2b;
    }

    /**
     * 根据referenceId 获取文件信息
     *
     * @param referenceIds 多传多个，逗号分隔
     * @return
     */
    @RequestMapping("/getFileNodesById")
    public ReturnDto getFileNodesById(@RequestParam String referenceIds) {
        return new ReturnDto(fileService.getFileNodesById(referenceIds));
    }

    /**
     * 根据分块的Identifier 获取文件信息
     *
     * @param identifiers 多传多个，逗号分隔
     * @return
     */
    @RequestMapping("/getFileNodesByIdentifier")
    public ReturnDto getFileNodesByIdentifier(@RequestParam String identifiers) {
        return new ReturnDto(fileService.getFileNodesByIdentifier(identifiers));
    }
}
