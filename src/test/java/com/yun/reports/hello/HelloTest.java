package com.yun.reports.hello;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.yun.reports.es.controller.ElasticsearchController;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MockServletContext.class)
@WebAppConfiguration
public class HelloTest {

	private MockMvc mockMvc1;
	private MockMvc mockMvc2;

	@Before
	public void setUp()  throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc1 = MockMvcBuilders.standaloneSetup(new ElasticsearchController()).build();
		this.mockMvc2 = MockMvcBuilders.standaloneSetup(new HelloController()).build();
	}

	@Test
	public void getHello() throws Exception {
		mockMvc1.perform(MockMvcRequestBuilders.get("/hello").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(content().string(equalTo("Hello World")));
	}
	
	/*@Test
	public void testConnectES() throws Exception {
		MockHttpServletRequestBuilder request = (MockHttpServletRequestBuilder) mockMvc.perform(MockMvcRequestBuilders.get("/es/connect").accept(MediaType.APPLICATION_JSON));
		ResultActions result = mockMvc.perform(request);
		result.andExpect(status().isOk());
	}*/
	
	@Test
	public void testConnectES() throws Exception {
		mockMvc2.perform(MockMvcRequestBuilders.get("/es/connect").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}

}
