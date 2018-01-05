package com.yun.reports.h2.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yun.reports.h2.entity.HostInfo;
import com.yun.reports.h2.repository.HostInfoRepository;
import com.yun.reports.h2.service.HostInfoService;

@Service
public class HostInfoServiceImpl implements HostInfoService {

	private HostInfoRepository hostInfoRepository;
	
	@Transactional(readOnly = true)
	@Override
	public HostInfo findHostByIp(String hostIp) {
		// TODO Auto-generated method stub
		return hostInfoRepository.getHostByIp(hostIp);
	}

	@Transactional(readOnly = true)
	@Override
	public HostInfo findHostsByIp(String hostIp) {
		// TODO Auto-generated method stub
		return hostInfoRepository.getHostsByIp(hostIp);
	}

	@Override
	public HostInfo saveHost(HostInfo host) {
		// TODO Auto-generated method stub
		return hostInfoRepository.save(host);
	}

	@Override
	public List<HostInfo> findAll() {
		// TODO Auto-generated method stub
		return hostInfoRepository.findAll();
	}

	@Override
	public List<HostInfo> findHostByVagueIp(String hostIp) {
		// TODO Auto-generated method stub
		return hostInfoRepository.getHostsByVagueIp(hostIp);
	}

	@Override
	public Page<HostInfo> showAllHosts(PageRequest pageable) {
		// TODO Auto-generated method stub
		return hostInfoRepository.findAll(pageable);
	}

	@Override
	public void removeHost(String hostIp) {
		// TODO Auto-generated method stub
		hostInfoRepository.delete(hostInfoRepository.getHostsByIp(hostIp));
	}

	@Override
	public void updateAgentByHostIp(String agentInfo, String hostIp) {
		// TODO Auto-generated method stub
		hostInfoRepository.updateAgentByHostIp(agentInfo, hostIp);
	}

}
