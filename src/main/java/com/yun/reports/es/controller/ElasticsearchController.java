package com.yun.reports.es.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yun.reports.utils.api.ApiResponseUtils;
import com.yun.reports.utils.api.ElasticsearchUtils;

@RestController
@RequestMapping(value = "/es", produces = MediaType.APPLICATION_JSON_VALUE)
public class ElasticsearchController {
	
	private static final Logger LOGGER = LogManager.getLogger(ElasticsearchController.class);
	
	
	//@PostMapping(value = "/connect")
	/*@RequestMapping(value = "/es/connect")
	public ResponseEntity<?> connectES(@RequestBody Map<String, String> map) {
		
		LOGGER.info("ElasticsearchController: connectES()");
		
		String esClusterName = map.get("esClusterName");
		String esHosts = map.get("esHosts");
		esClusterName = "elk_test";
		esHosts = "192.168.93.201:9300";
		
		LOGGER.debug("esClusterName: " + esClusterName +";esHosts: " + esHosts);
		System.out.println("esClusterName: " + esClusterName +";esHosts: " + esHosts);
		
		if(esClusterName != null || "".equals(esClusterName)) {
			if(esHosts != null || "".equals(esHosts)) {
				
				ElasticsearchUtils.connectES(esClusterName, esHosts);
				
				LOGGER.info("success to connect the ES");
				return ResponseEntity.ok(ApiResponseUtils.ok("success"));
				
			}else {
				return ResponseEntity.ok(ApiResponseUtils.failed("esHosts is empty!"));
			}
		}
		return ResponseEntity.ok(ApiResponseUtils.failed("esClusterName is empty!"));
	}*/
	
	@GetMapping(value = "/connect")
	public ResponseEntity<?> connectESGet(@RequestParam Map<String, String> map) {
		
		LOGGER.info("ElasticsearchController: connectES()");
		
		String esClusterName = map.get("esClusterName");
		String esHosts = map.get("esHosts");
		/*esClusterName = "elk_test";
		esHosts = "192.168.93.201:9300";*/
		
		LOGGER.info("esClusterName: " + esClusterName +" ;esHosts: " + esHosts);
		
		if(esClusterName != null || "".equals(esClusterName)) {
			if(esHosts != null || "".equals(esHosts)) {
				
				ElasticsearchUtils.connectES(esClusterName, esHosts);
				
				LOGGER.info("success to connect the ES");
				return ResponseEntity.ok(ApiResponseUtils.ok("success"));
				
			}else {
				return ResponseEntity.ok(ApiResponseUtils.failed("esHosts is empty!"));
			}
		}
		return ResponseEntity.ok(ApiResponseUtils.failed("esClusterName is empty!"));
	}
}
