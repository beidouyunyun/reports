package com.yun.reports.es.controller;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yun.reports.utils.api.ApiResponseUtils;
import com.yun.reports.utils.api.ESHighAPI;
import com.yun.reports.utils.api.ElasticsearchUtils;

@RestController
@RequestMapping(value = "/es", produces = MediaType.APPLICATION_JSON_VALUE)
public class ElasticsearchController {

	private static final Logger logger = LogManager.getLogger(ElasticsearchController.class);

	private static String esClusterName; // 集群名
	private static String esHosts; // 主机 IP:PORT

	private static String _index;
	private static String _type;
	private static String _condition;

	@PostMapping(value = "/connect")
	public ResponseEntity<?> connectES(@RequestBody Map<String, String> map) {

		logger.info("ElasticsearchController: connectES()");

		esClusterName = map.get("esClusterName");
		esHosts = map.get("esHosts");

		logger.info("esClusterName: " + esClusterName + " ;esHosts: " + esHosts);

		if (esClusterName != null || "".equals(esClusterName)) {
			if (esHosts != null || "".equals(esHosts)) {

				ElasticsearchUtils.connectES(esClusterName, esHosts, false);

				logger.info("success to connect the ES");
				return ResponseEntity.ok(ApiResponseUtils.ok("success"));

			} else {
				return ResponseEntity.ok(ApiResponseUtils.failed("esHosts is empty!"));
			}
		}
		return ResponseEntity.ok(ApiResponseUtils.failed("esClusterName is empty!"));
	}

	@GetMapping(value = "/connectGet")
	public ResponseEntity<?> connectESGet(@RequestParam Map<String, String> map) {

		logger.info("ElasticsearchController: connectES()");

		esClusterName = map.get("esClusterName");
		esHosts = map.get("esHosts");

		logger.info("esClusterName: " + esClusterName + " ;esHosts: " + esHosts);

		if (esClusterName != null || "".equals(esClusterName)) {
			if (esHosts != null || "".equals(esHosts)) {

				ElasticsearchUtils.connectES(esClusterName, esHosts, false);

				logger.info("success to connect the ES");
				return ResponseEntity.ok(ApiResponseUtils.ok("success"));

			} else {
				logger.info("esHosts is empty!");
				return ResponseEntity.ok(ApiResponseUtils.failed("esHosts is empty!"));
			}
		}
		logger.info("esClusterName is empty!");
		return ResponseEntity.ok(ApiResponseUtils.failed("esClusterName is empty!"));
	}

	@PostMapping(value = "/search")
	public ResponseEntity<?> searchByCondition(@RequestBody Map<String, String> map) {
		logger.info("ElasticsearchController: searchByCondition()");

		_index = map.get("index");
		_type = map.get("type");
		_condition = map.get("condition");

		logger.info("esClusterName: " + esClusterName + ";esHosts: " + esHosts + ";_index " + _index + ";_type: "
				+ _type + ";_condition: " + _condition);

		if (esClusterName != null || "".equals(esClusterName)) {
			if (esHosts != null || "".equals(esHosts)) {

				ElasticsearchUtils.connectES(esClusterName, esHosts, false);
				logger.info("success to connect the ES");

				IndexResponse response = ElasticsearchUtils.createIndex(_index, _type, _condition);

				if (response.status().getStatus() == 200) {
					logger.info("index create success!");
					return ResponseEntity.ok(ApiResponseUtils.ok("index create success"));
				} else {
					logger.info("index create failed!");
					return ResponseEntity.ok(ApiResponseUtils.failed("index create failed"));
				}
			} else {
				logger.info("esHosts is empty!");
				return ResponseEntity.ok(ApiResponseUtils.failed("esHosts is empty!"));
			}
		}
		logger.info("esClusterName is empty!");
		return ResponseEntity.ok(ApiResponseUtils.failed("esClusterName is empty!"));
	}

	@GetMapping(value = "/searchAll")
	public ResponseEntity<?> searchAll(@RequestParam Map<String, String> map) {
		logger.info("ElasticsearchController: searchAll()");

		esHosts = map.get("esHosts");

		logger.info("esHosts: " + esHosts);

		if (esHosts != null || "".equals(esHosts)) {
			ESHighAPI.connectES(esHosts);
			
			SearchResponse response = ESHighAPI.searchAllApi();
			
			if(response.status().getStatus() == 200 ) {
				logger.info(response.status().getStatus() + ": search success!");
				logger.info("Hits: "+response.getHits().totalHits+";TotalShards: "+response.getTotalShards());
				return ResponseEntity.ok(ApiResponseUtils.ok(response.getHits().totalHits));
			}else {
				logger.info("esHosts is empty!");
				return ResponseEntity.ok(ApiResponseUtils.failed(response.status().getStatus() + ": esHosts is empty!"));
			}
		} else {
			logger.info("esHosts is empty!");
			return ResponseEntity.ok(ApiResponseUtils.failed("esHosts is empty!"));
		}
	}
}
