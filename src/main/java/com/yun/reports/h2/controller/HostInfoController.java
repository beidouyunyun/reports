package com.yun.reports.h2.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yun.reports.h2.entity.HostInfo;
import com.yun.reports.h2.service.HostInfoService;
import com.yun.reports.utils.api.ApiResponseUtils;
import com.yun.reports.utils.api.JschUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/hosts", produces = MediaType.APPLICATION_JSON_VALUE)
public class HostInfoController {

	private static final Logger logger = LoggerFactory.getLogger(HostInfoController.class);

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private HostInfoService hostInfoService;

	@ApiOperation(value = "Show All Hosts", notes = "")
	@GetMapping(value = "/")
	public ResponseEntity<?> showAllHosts() {
		logger.trace("show all hosts");
		List<HostInfo> hosts = hostInfoService.findAll();

		if (hosts.isEmpty() || hosts == null) {
			return ResponseEntity.ok(ApiResponseUtils.failed(""));
		} else {
			return ResponseEntity.ok(ApiResponseUtils.ok(hosts));
		}
	}

	@ApiOperation(value = "Test Host", notes = "SSH连接Host主机")
	@ApiImplicitParam(name = "HostInfo", value = "用户详细实体staff", required = true, dataType = "HostInfo")
	@PostMapping(value = "/test")
	public ResponseEntity<?> testHost(@RequestBody HostInfo host) {
		JschUtils shell = new JschUtils(host.getHostIp(), host.getUserName(), host.getPassWord());

		int i = shell.connect();
		if (i == 0) {
			return ResponseEntity.ok(ApiResponseUtils.ok("1")); // success
		} else if (i == 1) {
			return ResponseEntity.ok(ApiResponseUtils.failed("2")); // time out
		} else {
			return ResponseEntity.ok(ApiResponseUtils.failed("3")); // failed
		}
	}

	@ApiOperation(value = "Page HostInfo", notes = "分页展示HostInfo信息")
	@ApiImplicitParam(name = "HostInfo", value = "用户详细实体HostInfo", required = true, dataType = "HostInfo")
	@PostMapping(value = "/page")
	public ResponseEntity<?> loadHostByPage(HttpServletRequest request, @RequestBody Map<String, String> map) {

		Sort sort = new Sort(Direction.ASC, map.get("sort"));
		
		Integer pageNum = Integer.parseInt(map.get("pageNum"));
		Integer pageSize = Integer.parseInt(map.get("pageSize"));
		
		logger.trace("pageNum:" + pageNum + ";pageSize:" + pageSize + ";sort:" + sort);
		
		PageRequest pageable = new PageRequest(pageNum, pageSize, sort);
		Page<HostInfo> page = hostInfoService.showAllHosts(pageable);

		if (page != null) {
			return ResponseEntity.ok(ApiResponseUtils.ok(page));
		} else {
			return ResponseEntity.ok(ApiResponseUtils.failed("Have no host message!"));
		}
	}

	@ApiOperation(value = "Create HostInfo", notes = "根据HostInfo对象创建用户")
	@ApiImplicitParam(name = "HostInfo", value = "用户详细实体HostInfo", required = true, dataType = "HostInfo")
	@PostMapping(value = "/add")
	public ResponseEntity<?> addHost(HttpServletRequest request, @RequestBody HostInfo host) {

		logger.trace(
				"hostIp:" + host.getHostIp() + ";username:" + host.getUserName() + ";password:" + host.getPassWord());
		HostInfo h1 = hostInfoService.findHostsByIp(host.getHostIp());

		if (h1 != null) {
			if (h1.getHostIp() == null || h1.getHostIp().isEmpty() || "".equals(h1.getHostIp())) {

				host.setPlatForm("Linux");
				host.setCreateDate(simpleDateFormat.format(new Date()));
				host.setModifyDate(simpleDateFormat.format(new Date()));
				host.setStatus("1");
				host.setAgentInfo("");
				return ResponseEntity.ok(ApiResponseUtils.ok(hostInfoService.saveHost(host)));
			} else {
				return ResponseEntity.ok(ApiResponseUtils.failed("This [" + host.getHostIp() + "] was already exist!"));
			}
		} else {

			host.setPlatForm("Linux");
			host.setCreateDate(simpleDateFormat.format(new Date()));
			host.setModifyDate(simpleDateFormat.format(new Date()));
			host.setStatus("1");
			host.setAgentInfo("");
			return ResponseEntity.ok(ApiResponseUtils.ok(hostInfoService.saveHost(host)));
		}
	}
	
	@ApiOperation(value="获取用户详细信息", notes="根据url的hostIp来获取用户详细信息")
    @ApiImplicitParam(name = "hostIp", value = "用户hostIp", required = true, dataType = "String")
	@Transactional(readOnly = true)
	@GetMapping(value = "/fuzzySearch/{hostIp}")
	public ResponseEntity<?> fuzzySearch(@PathVariable String hostIp) {

		logger.trace("Fuzzy search for host: " + hostIp);

		List<HostInfo> hosts = hostInfoService.findHostByVagueIp("%" + hostIp + "%");
		if (hosts.isEmpty()) {
			return ResponseEntity.ok(ApiResponseUtils.failed(""));
		} else {
			return ResponseEntity.ok(ApiResponseUtils.ok(hosts));
		}
	}
	
	@ApiOperation(value="删除用户", notes="根据id来指定删除对象")
    @ApiImplicitParam(name = "hostIp", value = "用户hostIp", required = true, dataType = "String")
	@PostMapping(value = "/remove")
	public ResponseEntity<?> removeHostByIp(@RequestBody Map<String, String> map) {

		logger.trace("removeHostByIp()==hostIp:" + map.get("hostIp"));
		String hostIp = map.get("hostIp");
		if (hostIp != null || !"".equals(hostIp)) {
			HostInfo host = hostInfoService.findHostsByIp(hostIp);
			if (host != null) {
				hostInfoService.removeHost(hostIp);
				return ResponseEntity.ok(ApiResponseUtils.ok("remove success"));
			} else {
				return ResponseEntity.ok(ApiResponseUtils.failed("remove failed"));
			}
		} else {
			return ResponseEntity.ok(ApiResponseUtils.failed("This host not exist"));
		}
	}
	
	@ApiOperation(value="更新用户详细信息", notes="根据url的id来指定更新对象，并根据传过来的user信息来更新用户详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostIp", value = "用户hostIp", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hostInfo", value = "用户详细实体HostInfo", required = true, dataType = "HostInfo")
    })
	@PostMapping(value = "/update")
	public ResponseEntity<?> updateHost(@RequestBody HostInfo host) {

		HostInfo h = hostInfoService.findHostsByIp(host.getHostIp());

		if (h != null || !"".equals(h)) {

			h.setPlatForm(host.getPlatForm());
			h.setPassWord(host.getPassWord());
			h.setUserName(host.getUserName());
			h.setStatus("2");
			h.setModifyDate(simpleDateFormat.format(new Date()));

			return ResponseEntity.ok(ApiResponseUtils.ok(hostInfoService.saveHost(h)));
		} else {
			return ResponseEntity.ok(ApiResponseUtils.failed("This host doesn't exist"));
		}

	}
}
