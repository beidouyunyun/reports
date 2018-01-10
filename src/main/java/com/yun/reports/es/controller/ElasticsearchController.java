package com.yun.reports.es.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yun.reports.utils.api.ApiResponseUtils;
import com.yun.reports.utils.api.ElasticsearchUtils;

@RestController
@RequestMapping(value = "/es", produces = MediaType.APPLICATION_JSON_VALUE)
public class ElasticsearchController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchController.class);
	
	
	@PostMapping(value = "/connect")
	public ResponseEntity<?> connectES(@RequestBody Map<String, String> map) {
		
		LOGGER.info("ElasticsearchController: connectES()");
		
		String esClusterName = map.get("esClusterName");
		String esHosts = map.get("esHosts");
		
		LOGGER.debug("esClusterName: " + esClusterName +";esHosts: " + esHosts);
		
		if(esClusterName != null || "".equals(esClusterName)) {
			if(esHosts != null || "".equals(esHosts)) {
				
				//ElasticsearchUtils.connectES(esClusterName, esHosts);
				
				return ResponseEntity.ok(ApiResponseUtils.ok(ElasticsearchUtils.connectES(esClusterName, esHosts)));
				
			}else {
				return ResponseEntity.ok(ApiResponseUtils.failed("esHosts is empty!"));
			}
		}
		return ResponseEntity.ok(ApiResponseUtils.failed("esClusterName is empty!"));
	}
}
