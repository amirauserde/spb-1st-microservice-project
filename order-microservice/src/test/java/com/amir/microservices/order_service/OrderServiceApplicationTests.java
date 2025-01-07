package com.amir.microservices.order_service;

import com.amir.microservices.order_service.event.OrderPlacedEvent;
import com.amir.microservices.order_service.stubs.InventoryClientStub;
import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.MySQLContainer;

import java.util.concurrent.CompletableFuture;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.3.0");
	@LocalServerPort
	private Integer port;
	// Mock KafkaTemplate
	@MockitoBean
	private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

	@BeforeEach
	void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;

		// Mock a ListenableFuture for SendResult<String, OrderPlacedEvent>
		// Suppress the unchecked warning for ListenableFuture
		@SuppressWarnings("unchecked")
		CompletableFuture<SendResult<String, OrderPlacedEvent>> future = Mockito.mock(CompletableFuture.class);

		// Stub KafkaTemplate.send to return the mock ListenableFuture
		doReturn(future).when(kafkaTemplate).send(anyString(), any(OrderPlacedEvent.class));
	}

	static {
		mySQLContainer.start();
	}

	@Test
	void shouldSubmitOrder() {

		String submitOrderJson = """
                {
                     "skuCode": "galaxy_24",
                     "price": 700,
                     "quantity": 1,
                     "userDetails": {
				             "email": "am.sharafi@gmail.com",
				             "firstName": "Amir",
				             "lastName": "Sharafi"
				             }
                }
                """;
		InventoryClientStub.stubInventoryCall("galaxy_24", 1);

		var responseStringBody = RestAssured.given()
				.contentType("application/json")
				.body(submitOrderJson)
				.when()
				.post("/api/order")
				.then()
				.log().all()
				.statusCode(201)
				.extract()
				.body().asString();

		assertThat(responseStringBody, Matchers.is("Order placed successfully"));

		// Verify that the KafkaTemplate's send method was called once
//		verify(kafkaTemplate, times(1))
//				.send(eq("order-placed"), any(OrderPlacedEvent.class));
	}

}
