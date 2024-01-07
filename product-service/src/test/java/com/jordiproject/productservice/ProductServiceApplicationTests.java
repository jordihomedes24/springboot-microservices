package com.jordiproject.productservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jordiproject.productservice.dto.ProductRequest;
import com.jordiproject.productservice.dto.ProductResponse;
import com.jordiproject.productservice.model.Product;
import com.jordiproject.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	 @Container
	 static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");
	 @Autowired
	 private MockMvc mockMvc;
	 @Autowired
	 private ObjectMapper objectMapper;
	 @Autowired
	 private ProductRepository productRepository;

	 @DynamicPropertySource
	 static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		 dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	 }



	@Test
	void shouldCreateProduct() throws Exception {
		 productRepository.deleteAll();
		 ProductRequest productRequest = createProductRequest();

		 String productRequestString = objectMapper.writeValueAsString(productRequest);

		 mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
					.contentType(MediaType.APPLICATION_JSON)
					.content(productRequestString))
				 .andExpect(MockMvcResultMatchers.status().isCreated());

        Assertions.assertEquals(1, productRepository.findAll().size());
		Product product = productRepository.findAll().get(0);
		Product expectedProduct = createProduct(product.getId());
		Assertions.assertEquals(expectedProduct, product);
	}


	@Test
	void shouldGetProduct() throws Exception {
		 productRepository.deleteAll();
		 //We persist the product on DB
		 Product product = createProduct("1");
		 productRepository.save(product);

		 //We create the expected product
		 List<ProductResponse> expectedResponse = Arrays.asList(createProductResponse());

		 String expectedResponseString = objectMapper.writeValueAsString(expectedResponse);

		 mockMvc.perform(MockMvcRequestBuilders.get("/api/product"))
				 .andExpect(MockMvcResultMatchers.status().isOk())
				 .andExpect(MockMvcResultMatchers.content().string(expectedResponseString));
	}


	private ProductRequest createProductRequest() {
		return ProductRequest.builder()
				.name("Iphone 13")
				.description("Iphone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

	private ProductResponse createProductResponse() {
		 return ProductResponse.builder()
				 .id("1")
				 .name("Iphone 13")
				 .description("Iphone 13")
				 .price(BigDecimal.valueOf(1200))
				 .build();
	}

	private Product createProduct(String id) {
		 return Product.builder()
				 .id(id)
				 .name("Iphone 13")
				 .description("Iphone 13")
				 .price(BigDecimal.valueOf(1200))
				 .build();
	}

}
