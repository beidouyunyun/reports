package com.yun.reports.h2.entity;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@Entity
@Table(name = "t_host_info")
public class HostInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hosts_seq")
	@SequenceGenerator(name = "hosts_seq", sequenceName = "hosts_seq", allocationSize = 1)
	private Integer id;

	@NonNull
	private String hostIp;

	@NonNull
	private String userName;

	@NonNull
	private String passWord;

	private String status; //运行状态 1.not started；2.start；3.stop

	private String createDate;
	
	private String modifyDate;

	private String platForm;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	@Column(columnDefinition = "CLOB")
	private String agentInfo;
}
