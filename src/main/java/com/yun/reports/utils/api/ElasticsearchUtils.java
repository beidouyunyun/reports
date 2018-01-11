package com.yun.reports.utils.api;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.alibaba.fastjson.JSONObject;

public class ElasticsearchUtils implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LogManager.getLogger(ElasticsearchUtils.class);
	// private static Map<String, ElasticsearchUtils> instanceCache;

	private static TransportClient esClient = null;

	public ElasticsearchUtils() {

	}

	/*
	 * public synchronized static ElasticsearchUtils getInstance(String hosts){ if
	 * (instanceCache == null){ instanceCache = new ConcurrentHashMap<String,
	 * ElasticsearchUtils>(); } ElasticsearchUtils instance =
	 * instanceCache.get(hosts); if (null == instance){ instance = new
	 * ElasticsearchUtils(); esClient = connectES(hosts);
	 * instance.setEsClient(esClient); instanceCache.put(hosts, instance); }
	 * 
	 * return instance; }
	 */

	/**
	 * 单点连接ES
	 * @param esHosts
	 * @return
	 */
	@SuppressWarnings("resource")
	public static TransportClient connectES(String esHosts) {
		logger.info("esHosts: " + esHosts);
		
		if (StringUtils.isEmpty(esHosts)) {
			logger.info("hosts is empty");
			return null;
		}

		String host;
		int port = 9300;

		String[] address = esHosts.split(":");
		if (address.length > 1 && address != null) {
			host = address[0];
			port = Integer.parseInt(address[1]);

			try {
				logger.info("host: " + host + " ;port: " + port);

				esClient = new PreBuiltTransportClient(Settings.EMPTY)
						.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));

				logger.info("success to connect the ES");
			} catch (Exception e) {
				logger.info("connectES faild");
				logger.error(e);
				e.printStackTrace();
			}
		}
		return esClient;
	}

	/**
	 * 连接ES集群
	 * @param esClusterName 集群名称
	 * @param esHosts es集群地址，IP:PORT
	 * @return
	 */
	@SuppressWarnings("resource")
	public static TransportClient connectES(String esClusterName, String esHosts,boolean sniff) {
		logger.info("esClusterName: " + esClusterName + " ;esHosts: " + esHosts);
		
		if (StringUtils.isEmpty(esHosts)) {
			System.out.println("hosts is empty");
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
				try {
					logger.info("host: " + host + " ;port: " + port);
					
					Settings settings = Settings.EMPTY;
					if(sniff == true) {
						// 启用嗅探，设置client.transport.sniff为true
						settings = Settings.builder()
								.put("cluster.name", esClusterName)
								.put("client.transport.sniff",true)
								.build();
					}
					
					esClient = new PreBuiltTransportClient(settings)
							.addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));

					logger.info("success to connect the ES");
				} catch (Exception e) {
					logger.info("connectES faild");
					logger.error(e);
					e.printStackTrace();
				}
			}
		}
		return esClient;
	}

	/**
	 * 关闭ES连接
	 */
	public void closeES() {
		if (esClient != null) {
			esClient.close();
		}
	}

	/**
	 * 创建索引（往ES中插入数据）
	 * 
	 * @param index
	 * @param type
	 * @param jsonData json格式字符串，否则报错
	 * @return
	 */
	public static IndexResponse createIndex(String index, String type, String jsonData) {
		IndexResponse response = esClient.prepareIndex(index, type).setSource(jsonData).execute().actionGet();
		return response;
	}

	/**
	 * 创建索引（往ES中插入数据）
	 * @param index 索引的格式，例如YYYY.MM.DD
	 * @param type 索引类型，例如group
	 * @param jsonData json格式字符串，否则报错
	 * @return
	 */
	public static IndexResponse createIndex(String index, String type, Map<String, Object> data) {
		IndexResponse response = esClient.prepareIndex(index, type).setSource(data).execute().actionGet();
		return response;
	}

	/**
	 * 创建索引（往ES中插入数据）
	 * 
	 * @param index
	 *            索引的格式，例如YYYY.MM.DD
	 * @param type
	 *            索引类型，例如group
	 * @param jsonData
	 *            json格式字符串，否则报错
	 * @return
	 */
	public static IndexResponse createIndex(String index, String type, JSONObject data) {
		IndexResponse response = esClient.prepareIndex(index, type).setSource(data).execute().actionGet();
		return response;
	}

	/**
	 * 创建索引（往ES中插入数据）
	 * @param index 索引的格式，例如YYYY.MM.DD
	 * @param type 索引类型，例如group
	 * @param jsonData json格式字符串，否则报错
	 * @return
	 */
	public static void createIndex(String index, String type, List<JSONObject> data) {
		if (null != data && data.size() > 0) {
			for (JSONObject obj : data) {
				esClient.prepareIndex(index, type).setSource(obj).execute().actionGet();
				;
			}
		}
	}

	/**
	 * 获取API 默认情况下，operationThreaded被设置为true表示在不同的线程上执行操作
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public static GetResponse getResponse(String index, String type, String id) {
		GetResponse response = esClient.prepareGet(index, type, id).get();
		return response;
	}

	/**
	 * 获取API,操作线程 
	 * 默认情况下，operationThreaded被设置为true表示在不同的线程上执行操作,此处为false
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public static GetResponse getResponseByOperationThreaded(String index, String type, String id) {
		GetResponse response = esClient.prepareGet(index, type, id).setOperationThreaded(false).get();
		return response;
	}

	/**
	 * 删除API 
	 * 默认情况下，operationThreaded被设置为true表示在不同的线程上执行操作
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @return
	 */
	public static DeleteResponse deleteIndex(String index, String type, String id) {
		DeleteResponse response = esClient.prepareDelete(index, type, id).get();
		return response;
	}

	/**
	 * 通过查询删除API 
	 * 通过查询API删除允许根据查询结果删除给定的一组文档
	 * 
	 * @param queryKey 查询字段
	 * @param queryValue 查询字段值
	 * @param source
	 * @return 删除的文件数量
	 */
	public static long deleteByQuery(String queryKey, String queryValue, String source) {
		BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient)
				.filter(QueryBuilders.matchQuery(queryKey, queryValue)).source(source).get(); // 执行操作
		long deleted = response.getDeleted();
		return deleted;
	}

	/**
	 * 通过查询删除API 
	 * 提供一个监听器,异步执行删除文档
	 * 
	 * @param queryKey 查询字段
	 * @param queryValue 查询字段值
	 * @param source
	 */
	public static void deleteByQueryListener(String queryKey, String queryValue, String source) {

		DeleteByQueryAction.INSTANCE.newRequestBuilder(esClient).filter(QueryBuilders.matchQuery(queryKey, queryValue))
				.source(source).execute(new ActionListener<BulkByScrollResponse>() {

					@Override
					public void onResponse(BulkByScrollResponse response) {
						// TODO Auto-generated method stub
						// 删除的文件数量
						long deleted = response.getDeleted();
					}

					@Override
					public void onFailure(Exception e) {
						// TODO Auto-generated method stub

					}
				}); // 执行操作
	}

	/**
	 * MultiSearch API 
	 * 多条件搜索
	 * 
	 * @param strQuery
	 * @param queryKey
	 * @param queryValue
	 * @return
	 */
	public static long multiSearch(String strQuery, String queryKey, String queryValue) {

		SearchRequestBuilder srb1 = esClient.prepareSearch().setQuery(QueryBuilders.queryStringQuery(strQuery))
				.setSize(1);
		SearchRequestBuilder srb2 = esClient.prepareSearch().setQuery(QueryBuilders.matchQuery(queryKey, queryValue))
				.setSize(1);

		MultiSearchResponse msResponse = esClient.prepareMultiSearch().add(srb1).add(srb2).get();

		// 您将从MultiSearchResponse＃getResponses（）获得所有单个响应
		long nbHits = 0;
		for (MultiSearchResponse.Item item : msResponse.getResponses()) {
			SearchResponse response = item.getResponse();
			nbHits += response.getHits().getTotalHits();
		}

		return nbHits;
	}

	/*
	 * public List<DocumentResult> search(QueryBuilder queryBuilder, String index,
	 * String type){ SearchResponse searchResponse =
	 * esClient.prepareSearch(index).setTypes(type).setQuery(queryBuilder).execute()
	 * .actionGet(); SearchHits hits = searchResponse.getHits(); SearchHit[]
	 * searchHists = hits.getHits(); if (searchHists.length > 0){
	 * List<DocumentResult> list = new ArrayList<DocumentResult>(); for (SearchHit
	 * hit: searchHists){ Integer id = (Integer)hit.getSource().get("id"); String
	 * name = (String)hit.getSource().get("name"); String function =
	 * (String)hit.getSource().get("function"); list.add(new DocumentResult(id,
	 * name, function)); } return list; } return null; }
	 */

	public Client getEsClient() {
		return esClient;
	}

	public void setEsClient(TransportClient esClient) {
		this.esClient = esClient;
	}
}
