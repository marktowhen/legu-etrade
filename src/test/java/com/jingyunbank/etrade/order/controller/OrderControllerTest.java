package com.jingyunbank.etrade.order.controller;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.jingyunbank.etrade.TestCaseBase;
import com.jingyunbank.etrade.api.order.service.IOrderService;

public class OrderControllerTest extends TestCaseBase {

	@Autowired
	private IOrderService orderService;
	//private RestTemplate restTemplate = new TestRestTemplate();
	@Autowired
	private WebApplicationContext wac;
	private MockMvc mockMvc;
	
	@Before
	public void init(){
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
	@Test
	public void test0() throws Exception{
		assertNotNull(orderService);
		assertNotNull(mockMvc);
		mockMvc.perform(
				 put("/orders/submit")
				.param("addressID", "1233211233211233211233")
				.param("paytypeID", "1233211233211233211233")
				.param("price", "123.32")
				.param("postage", "12.32")
				.sessionAttr("LOGIN_ID", "USER-ID")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.parseMediaType("application/json;charset=UTF-8")))
				.andExpect(jsonPath("$.code").value("200"))
				.andDo(print()
				);
		//System.out.println(restTemplate.getForEntity("http://localhost:8080/user", String.class).getBody());
		
	}
	
}
