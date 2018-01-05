package com.yun.reports.h2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.yun.reports.h2.entity.HostInfo;


public interface HostInfoRepository extends JpaRepository<HostInfo, Integer>,PagingAndSortingRepository<HostInfo, Integer> {

	@Query(value = "select * from t_host_info t where t.host_ip = ?1",nativeQuery = true)
	HostInfo getHostsByIp(String hostIp);

	@Query(value = "select host_ip,user_name,pass_word,status,plat_form,modify_date from t_host_info t where t.host_ip = ?1",nativeQuery = true)
	HostInfo getHostByIp(String hostIp);
	
	@Query(value = "select * from t_host_info t where t.host_ip like :hostIp",nativeQuery = true)
	List<HostInfo> getHostsByVagueIp(@Param("hostIp") String hostIp);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update t_host_info set agent_info = :agentInfo where host_ip = :hostIp",nativeQuery = true)
	void updateAgentByHostIp(@Param("agentInfo") String agentInfo,@Param("hostIp") String hostIp);
	
}
