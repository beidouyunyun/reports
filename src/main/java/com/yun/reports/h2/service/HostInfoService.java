package com.yun.reports.h2.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.yun.reports.h2.entity.HostInfo;

public interface HostInfoService {

	/*
	 * 根据IP查询Host
	 */
	public HostInfo findHostByIp(String hostIp);
	
	/*
	 * 根据IP查询Host
	 */
	public HostInfo findHostsByIp(String hostIp);
	
	/*
	 * 新增Host
	 */
	public HostInfo saveHost(HostInfo host);
	
	/*
	 * 查询所有Hosts
	 */
	public List<HostInfo> findAll();
	
	/*
	 * 模糊查询Hosts
	 */
	public List<HostInfo> findHostByVagueIp(String hostIp);
	
	/*
	 * 分页查询
	 */
	public Page<HostInfo> showAllHosts(PageRequest pageable);
	
	/*
	 * 删除Host
	 */
	public void removeHost(String hostIp);
	
	/*
	 * 修改Agent 
	 */
	public void updateAgentByHostIp(String agentInfo,String hostIp);
}
