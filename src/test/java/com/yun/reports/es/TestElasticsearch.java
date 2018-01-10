package com.yun.reports.es;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.yun.reports.es.controller.ElasticsearchController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MockServletContext.class)
@WebAppConfiguration
public class TestElasticsearch {

	private MockMvc mockMvc;
	
	@Before
	public void setUp()  throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(new ElasticsearchController()).build();
	}
	
	@Test
	public void testConnectES() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/es/connect?esClusterName=elk_test&esHosts=192.168.93.201:9300").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
}
