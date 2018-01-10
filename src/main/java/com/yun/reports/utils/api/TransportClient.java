package com.yun.reports.utils.api;

import org.elasticsearch.common.network.InetAddresses;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class TransportClient {
	
	private static PreBuiltTransportClient  transPort = null;   
    private String esClusterName;//集群名
    private String esServerIps;//集群服务IP集合
    private Integer esServerPort;//ES集群端口


 /**
     *  ES TransPortClient 客户端连接<br>
     *  在elasticsearch平台中,可以执行创建索引,获取索引,删除索引,搜索索引等操作
     * @return
     */
   /* public TransportClient getTransPortClient() {
        try {
            if (transPort == null) {

                if(esServerIps == null || "".equals(esServerIps.trim())){
                    return  null;
                }

                Settings settings = Settings.builder()
//                      .put("cluster.name", esClusterName)// 集群名
                        .put("client.transport.sniff", true)
                        // 自动把集群下的机器添加到列表中

                        .build();
                transPort  = new  PreBuiltTransportClient(settings);
                String esIps[] = esServerIps.split(",");
                for (String esIp : esIps) {//添加集群IP列表
                    TransportAddress transportAddress =  new InetSocketTransportAddress(InetAddresses.forString(esIp),9300);
                    transPort.addTransportAddresses(transportAddress);
                }
                return transPort;
            } else {
                return transPort;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (transPort != null) {
                transPort.close();
            }
            return null;
        }
    }*/
}
