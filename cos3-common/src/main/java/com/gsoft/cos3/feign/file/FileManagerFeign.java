package com.gsoft.cos3.feign.file;

import javax.ws.rs.core.MediaType;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.gsoft.cos3.dto.FileNode;
import com.gsoft.cos3.feign.FeignClientConfiguration;

/**
 * 文件存储Feign接口
 * 
 * @author SN
 *
 */
@FeignClient(value = "cos3-file-manager", url = "${feign.url}", configuration = FeignClientConfiguration.class)
public interface FileManagerFeign {

	@RequestMapping("/file/downloadByReferenceId")
	public ResponseEntity<byte[]> download(@RequestParam(name = "referenceId") String referenceId);

	@RequestMapping(value="/file/uploadFile", produces = { MediaType.APPLICATION_JSON }, consumes = MediaType.MULTIPART_FORM_DATA)
	public FileNode webUploader(@RequestPart(name = "file") MultipartFile file);

}
