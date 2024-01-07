package com.jordiproject.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jordiproject.orderservice.dto.OrderLineItemDto;
import com.jordiproject.orderservice.dto.OrderRequest;
import com.jordiproject.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {
	@Container
	static MySQLContainer mysql = new MySQLContainer<>("mysql:5.7.34");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ObjectMapper objectMapper;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.datasource.url", mysql::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
		dynamicPropertyRegistry.add("spring.datasource.username", mysql::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", mysql::getPassword);
		dynamicPropertyRegistry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
	}

	@BeforeEach
	void beforeEach() {
		mysql.waitingFor(new LogMessageWaitStrategy().withRegEx(".*MySQL init process done. Ready for start up.*"));
	}

	@AfterEach
	void afterEach() {
		orderRepository.deleteAll();
	}

	@Test
	void shouldPlaceOrder() throws Exception {
		OrderRequest orderRequest = mockOrderRequest();
		String orderRequestString = objectMapper.writeValueAsString(orderRequest);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
				.contentType(MediaType.APPLICATION_JSON)
				.content(orderRequestString))
				.andExpectAll(
						status().isCreated(),
						content().contentType("text/plain;charset=UTF-8"),
						content().string("Order Placed Successfully")
				);
	}

	private OrderRequest mockOrderRequest() {
		List<OrderLineItemDto> orderLineItemDtoList = new ArrayList<>();
		orderLineItemDtoList.add(OrderLineItemDto.builder()
				.id(1L)
				.skuCode("iphone_13")
				.price(new BigDecimal(1200))
				.quantity(1)
				.build()
		);

		return OrderRequest.builder()
				.orderLineItemsDtoList(orderLineItemDtoList)
				.build();
	}
}
