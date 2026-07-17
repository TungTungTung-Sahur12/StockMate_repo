package com.stockmate.stockmate_backend;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BackendE2EIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void happyPath_registerAdmin_login_createProduct_recordSale_and_verifyInventory() throws Exception {
        // Register admin
        Map<String, Object> reg = Map.of(
            "name", "Owner",
            "email", "owner@example.com",
            "password", "password123",
            "confirmPassword", "password123"
        );
        ResponseEntity<String> regRes = restTemplate.postForEntity("/api/auth/register", reg, String.class);
        assertThat(regRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Login
        Map<String, Object> login = Map.of("email", "owner@example.com", "password", "password123");
        ResponseEntity<String> loginRes = restTemplate.postForEntity("/api/auth/login", login, String.class);
        assertThat(loginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode loginJson = mapper.readTree(loginRes.getBody());
        String token = loginJson.get("token").asText();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create product (ADMIN)
        Map<String, Object> product = Map.of(
                "name", "T-Shirt A",
                "category", "TEE",
                "size", "M",
                "price", new BigDecimal("199.50"),
                "quantity", 10,
                "lowStockThreshold", 2
        );
        HttpEntity<Map<String, Object>> prodReq = new HttpEntity<>(product, headers);
        ResponseEntity<String> prodRes = restTemplate.postForEntity("/api/products", prodReq, String.class);
        assertThat(prodRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JsonNode prodJson = mapper.readTree(prodRes.getBody());
        Long productId = prodJson.get("productId").asLong();

        // Record sale
        Map<String, Object> sale = Map.of("productId", productId, "quantitySold", 3);
        HttpEntity<Map<String, Object>> saleReq = new HttpEntity<>(sale, headers);
        ResponseEntity<String> saleRes = restTemplate.postForEntity("/api/sales", saleReq, String.class);
        assertThat(saleRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Fetch product and verify quantity decreased
        HttpEntity<Void> getReq = new HttpEntity<>(headers);
        ResponseEntity<String> getRes = restTemplate.exchange("/api/products/" + productId, HttpMethod.GET, getReq, String.class);
        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        JsonNode getJson = mapper.readTree(getRes.getBody());
        assertThat(getJson.get("quantity").asInt()).isEqualTo(7);
    }

    @Test
    void unauthorized_access_is_blocked() {
        // Try to access protected endpoint without token
        ResponseEntity<String> res = restTemplate.getForEntity("/api/products", String.class);
        // Some security configurations return 401 or 403 for unauthenticated access.
        assertThat(res.getStatusCode()).isIn(HttpStatus.UNAUTHORIZED, HttpStatus.FORBIDDEN);
    }

    @Test
    void duplicate_admin_registration_is_forbidden() {
        Map<String, Object> reg = Map.of(
            "name", "Owner",
            "email", "owner2@example.com",
            "password", "password123",
            "confirmPassword", "password123"
        );
        // First registration
        ResponseEntity<String> first = restTemplate.postForEntity("/api/auth/register", reg, String.class);
        assertThat(first.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Second registration should be forbidden (admin exists)
        ResponseEntity<String> second = restTemplate.postForEntity("/api/auth/register", reg, String.class);
        assertThat(second.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(second.getBody()).contains("Admin account already exists");
    }
}
