package com.gsoft.web.framework.controller;

import com.gsoft.cos3.dto.ReturnDto;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 覆盖spring boot默认错误提示
 *
 * @author plsy
 */
@Controller
public class ErrorController extends BasicErrorController {

	public ErrorController(ServerProperties serverProperties) {
		super(new DefaultErrorAttributes(), serverProperties.getError());
	}

	/**
	 * 覆盖默认的Json响应
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
		// cos3-im-manager没有启动的时候, 将Response由404改成200
		boolean starts = request.getAttribute("javax.servlet.forward.request_uri").toString()
				.startsWith("/cos3-im-manager/");
		if (starts) {
			return new ResponseEntity(new ReturnDto(new HashMap<>()), HttpStatus.OK);
		}

		Map<String, Object> body = this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.ALL));
		HttpStatus status = this.getStatus(request);
		return new ResponseEntity<Map<String, Object>>(body, status);
	}
}
