package com.example.one;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.profiles.active=test"})
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
