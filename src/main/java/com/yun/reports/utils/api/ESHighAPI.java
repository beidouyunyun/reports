package com.yun.reports.utils.api;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.alibaba.fastjson.JSONObject;

public class ESHighAPI {

	private static final Logger logger = LogManager.getLogger(ESHighAPI.class);

	private static RestHighLevelClient esClient;

	public ESHighAPI() {

	}

	/**
	 * 连接ES
	 * 
	 * @param esHosts
	 * @return
	 */
	public static RestHighLevelClient connectES(String esHosts) {
		logger.info("ElasticSearchUtil: connectES()");

		if (StringUtils.isEmpty(esHosts)) {
			System.out.println("esHosts is empty");
			return null;
		}

		String[] hostLists = esHosts.split(",");
		String[] address;
		String host = null;
		int port = 0;

		for (String str : hostLists) {
			address = str.split(":");
			if (address.length > 1 && address != null) {
				host = address[0];
				port = Integer.parseInt(address[1]);
				logger.info("----esHosts：" + esHosts + ";host:" + host + ";port:" + port + "----");

				// on startup
				try {
					logger.info("--------------Connecting......------------");
					esClient = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, "http")));

					logger.info("connectES success");
				} catch (Exception e) {
					logger.info("connectES faild");
					e.printStackTrace();
				}
			}
		}
		return esClient;
	}

	/**
	 * 关闭ES
	 */
	public static void closeES() {
		if (esClient != null) {
			try {
				esClient.close();
			} catch (IOException e) {
				logger.error(e);
				logger.info("failure to close ES");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Index API
	 * 
	 * @param index
	 * @param type
	 * @param jsonData
	 *            String
	 * @return
	 */
	public static IndexRequest indexApi(String index, String type, String jsonData) {
		IndexRequest request = new IndexRequest(index, type).source(jsonData, XContentType.JSON);
		return request;
	}

	/**
	 * Index API
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param jsonData
	 *            String
	 * @return
	 */
	public static IndexRequest indexApi(String index, String type, String id, String jsonData) {
		IndexRequest request = new IndexRequest(index, type, id).source(jsonData, XContentType.JSON);
		return request;
	}

	/**
	 * Index API
	 * 
	 * @param index
	 * @param type
	 * @param jsonMap
	 *            Map<String, Object>
	 * @return
	 */
	public static IndexRequest indexApi(String index, String type, Map<String, Object> jsonMap) {
		IndexRequest request = new IndexRequest(index, type).source(jsonMap);
		return request;
	}

	/**
	 * Index API
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param jsonMap
	 *            Map<String, Object>
	 * @return
	 */
	public static IndexRequest indexApi(String index, String type, String id, Map<String, Object> jsonMap) {
		IndexRequest request = new IndexRequest(index, type, id).source(jsonMap);
		return request;
	}

	/**
	 * Index API
	 * 
	 * @param index
	 * @param type
	 * @param jsonMap
	 *            JSONObject
	 * @return
	 */
	public static IndexRequest indexApi(String index, String type, JSONObject jsonMap) {
		IndexRequest request = new IndexRequest(index, type).source(jsonMap);
		return request;
	}

	/**
	 * Index API
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param jsonMap
	 *            JSONObject
	 * @return
	 */
	public static IndexRequest indexApi(String index, String type, String id, JSONObject jsonMap) {
		IndexRequest request = new IndexRequest(index, type, id).source(jsonMap);
		return request;
	}

	/**
	 * Get API Synchronous
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public static GetResponse getApiSynchronous(String index, String type, String id) {
		logger.info("getApiSynchronous()");
		GetRequest request = new GetRequest(index, type, id);

		try {
			GetResponse response = esClient.get(request);
			return response;
		} catch (ElasticsearchException e) {
			logger.error(e);
			if (e.status() == RestStatus.NOT_FOUND) {
				logger.error(e.status() + ": does_not_exist");
			}
		} catch (IOException e) {
			logger.info("failure to getApiSynchronous");
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Get API Asynchronous
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public static GetResponse getApiAsynchronous(String index, String type, String id) {
		logger.info("getApiSynchronous()");

		GetRequest request = new GetRequest(index, type, id);

		esClient.getAsync(request, new ActionListener<GetResponse>() {

			@Override
			public void onResponse(GetResponse response) {

			}

			@Override
			public void onFailure(Exception e) {

			}
		});
		return null;
	}

	/**
	 * Delete API
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public static DeleteResponse deleteApi(String index, String type, String id) {
		DeleteRequest request = new DeleteRequest(index, type, id);
		DeleteResponse response;
		try {
			response = esClient.delete(request);
			return response;
		} catch (IOException e) {
			logger.info("failure to delete");
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Delete API Asynchronous
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public static DeleteResponse deleteApiAsynchronous(String index, String type, String id) {
		DeleteRequest request = new DeleteRequest(index, type, id);

		esClient.deleteAsync(request, new ActionListener<DeleteResponse>() {

			@Override
			public void onResponse(DeleteResponse response) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onFailure(Exception e) {
				// TODO Auto-generated method stub
			}
		});
		return null;
	}
	
	/**
	 * Update API
	 * @param index
	 * @param type
	 * @param id
	 * @param jsonData
	 * @return
	 */
	public static UpdateResponse updateApi(String index, String type, String id, String jsonData) {
		UpdateRequest request = new UpdateRequest(index, type, id)
				.doc(jsonData);
		UpdateResponse response;
		try {
			response = esClient.update(request);
			return response;
		} catch (IOException e) {
			logger.info("failure to update");
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Update API
	 * @param index
	 * @param type
	 * @param id
	 * @param jsonMap
	 * @return
	 */
	public static UpdateResponse updateApi(String index, String type, String id, Map<String, Object> jsonMap) {
		UpdateRequest request = new UpdateRequest(index, type, id)
				.doc(jsonMap);
		UpdateResponse response;
		try {
			response = esClient.update(request);
			return response;
		} catch (IOException e) {
			logger.info("failure to update");
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Update API
	 * @param index
	 * @param type
	 * @param id
	 * @param jsonMap
	 * @return
	 */
	public static UpdateResponse updateApi(String index, String type, String id, JSONObject jsonMap) {
		UpdateRequest request = new UpdateRequest(index, type, id)
				.doc(jsonMap);
		UpdateResponse response;
		try {
			response = esClient.update(request);
			return response;
		} catch (IOException e) {
			logger.info("failure to update");
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Search All API
	 * @return
	 */
	public static SearchResponse searchAllApi() {
		SearchRequest request = new SearchRequest();
		SearchResponse response = new SearchResponse();
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		
		request.source(searchSourceBuilder);
		try {
			response = esClient.search(request);
		} catch (IOException e) {
			logger.info("failure to search");
			logger.error(e);
			e.printStackTrace();
		}
		return response;
				
	}
	
	/**
	 * Search API
	 * @param name
	 * @param value
	 * @param from
	 * @param size
	 * @return
	 */
	public static SearchResponse searchApi(String name, String value, int from, int size) {
		
		SearchRequest request = new SearchRequest();
		SearchResponse response = new SearchResponse();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		searchSourceBuilder.query(QueryBuilders.termQuery(name, value));
		searchSourceBuilder.from(from);
		searchSourceBuilder.size(size);
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		
		request.source(searchSourceBuilder);
		try {
			response = esClient.search(request);
		} catch (IOException e) {
			logger.info("failure to search");
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
	
	/**
	 * Search API
	 * @param name
	 * @param value
	 * @return
	 */
	public static SearchResponse searchApi(String name, String value) {
		SearchRequest request = new SearchRequest();
		SearchResponse response = new SearchResponse();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		QueryBuilder queryBuilder = QueryBuilders.matchQuery(name, value)
				.fuzziness(Fuzziness.AUTO)
				.prefixLength(3)
				.maxExpansions(10);
		
		searchSourceBuilder.query(queryBuilder);
		request.source(searchSourceBuilder);
		try {
			response = esClient.search(request);
		} catch (IOException e) {
			logger.info("failure to search");
			logger.error(e);
			e.printStackTrace();
		}
		return response;
	}
}
