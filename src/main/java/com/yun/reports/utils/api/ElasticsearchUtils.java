package com.yun.reports.utils.api;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;  
import java.util.Map;  

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.delete.DeleteResponse;  
import org.elasticsearch.action.get.GetResponse;  
import org.elasticsearch.action.index.IndexResponse;  
import org.elasticsearch.client.Client;  
import org.elasticsearch.client.transport.TransportClient;  
import org.elasticsearch.common.settings.Settings;  
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.alibaba.fastjson.JSONObject;	

public class ElasticsearchUtils implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = LogManager.getLogger(ElasticsearchUtils.class);
	//private static Map<String, ElasticsearchUtils> instanceCache;  
	
	private String esClusterName;
	
	private String esHosts;
	
    private static TransportClient esClient = null;
    
    public ElasticsearchUtils() {
    	
    }
    
    /*public synchronized static ElasticsearchUtils getInstance(String hosts){  
        if (instanceCache == null){  
            instanceCache = new ConcurrentHashMap<String, ElasticsearchUtils>();  
        }  
        ElasticsearchUtils instance = instanceCache.get(hosts);  
        if (null == instance){  
            instance = new ElasticsearchUtils();  
            esClient = connectES(hosts);  
            instance.setEsClient(esClient);  
            instanceCache.put(hosts, instance);  
        }  
          
        return instance;  
    }*/
    
    /** 
     * 连接ES集群 
     * @param esClusterName 集群名称 
     * @param hosts es集群地址，IP:PORT 
     * @return 
     */  
    public static TransportClient connectES(String esClusterName, String esHosts){  
    	if (StringUtils.isEmpty(esHosts)) {
    		System.out.println("hosts is empty");
    		return null;
    	}
    	logger.info("esClusterName: " + esClusterName + " ;esHosts: " + esHosts);
    	
    	String[] hostLists = esHosts.split(",");
    	String[] address;
    	String host = null;
    	int port = 0;
    	for (String str : hostLists) {
    		address = str.split(":");
    		if(address.length > 1 && address != null) {
    			host = address[0];
    			port = Integer.parseInt(address[1]);
    			try {
    				logger.info("host: " +host + " ;port: "+ port);
    				
    				esClient = new PreBuiltTransportClient(Settings.EMPTY)
    				        .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
    				
    				logger.info("success to connect the ES");
    			} catch (Exception e) {
    				logger.info("connectES faild");
    				e.printStackTrace();
    			}
    		}else {
				esClient.close();;
			}
    	}
    	return esClient;
    }  
    
    /** 
     * 创建索引（往ES中插入数据） 
     * @param index 
     * @param type 
     * @param jsonData json格式字符串，否则报错 
     * @return 
     */  
    public IndexResponse createIndex(String index, String type, String jsonData, String timestamp){  
        IndexResponse response = esClient.prepareIndex(index, type).setSource(jsonData).execute().actionGet();  
        return response;  
    }  
      
    /** 
     * 创建索引（往ES中插入数据） 
     * @param index 索引的格式，例如YYYY.MM.DD 
     * @param type 索引类型，例如group 
     * @param jsonData json格式字符串，否则报错 
     * @param ttl 设置过期为空不设置 
     * @return 
     */  
    public IndexResponse createIndex(String index, String type, Map<String,Object> data, Long ttl){  
        IndexResponse response = esClient.prepareIndex(index, type).setSource(data).execute().actionGet();  
        return response;  
    }  
      
    /** 
     * 创建索引（往ES中插入数据） 
     * @param index 索引的格式，例如YYYY.MM.DD 
     * @param type 索引类型，例如group 
     * @param jsonData json格式字符串，否则报错 
     * @param ttl 设置过期，为空不设置 
     * @return 
     */  
    public IndexResponse createIndex(String index, String type, JSONObject data, Long ttl){  
        IndexResponse response = esClient.prepareIndex(index, type).setSource(data).execute().actionGet();  
        return response;  
    }  
      
    /** 
     * 创建索引（往ES中插入数据） 
     * @param index 索引的格式，例如YYYY.MM.DD 
     * @param type 索引类型，例如group 
     * @param jsonData json格式字符串，否则报错 
     * @param ttl 设置过期，为空不设置 
     * @return 
     */  
    public void createIndex(String index, String type, List<JSONObject> data, Long ttl){  
        if (null != data && data.size() > 0){  
            for (JSONObject obj : data){  
                esClient.prepareIndex(index, type).setSource(obj);  
            }  
        }  
    }  
      
    public DeleteResponse deleteIndex(String index, String type, String id){  
        DeleteResponse response = esClient.prepareDelete(index, type, id).get();  
        return response;  
    }  
      
    public DeleteResponse deleteAll(){  
        return null;  
    }  
      
    public GetResponse search(String index, String type, String id){  
        GetResponse response = esClient.prepareGet(index, type, id).get();  
        return response;  
    }  
      
    /*public List<DocumentResult> search(QueryBuilder queryBuilder, String index, String type){  
        SearchResponse searchResponse = esClient.prepareSearch(index).setTypes(type).setQuery(queryBuilder).execute().actionGet();  
        SearchHits hits = searchResponse.getHits();  
        SearchHit[] searchHists = hits.getHits();  
        if (searchHists.length > 0){  
            List<DocumentResult> list = new ArrayList<DocumentResult>();  
            for (SearchHit hit: searchHists){  
                Integer id = (Integer)hit.getSource().get("id");  
                String name = (String)hit.getSource().get("name");  
                String function = (String)hit.getSource().get("function");  
                list.add(new DocumentResult(id, name, function));  
            }  
            return list;  
        }  
        return null;  
    } */ 
    
	public Client getEsClient() {
		return esClient;
	}

	public void setEsClient(TransportClient esClient) {
		this.esClient = esClient;
	} 
}
