package com.playtomic.tests.wallet.infrastructure.client;


import com.playtomic.tests.wallet.infrastructure.dto.Payment;
import com.playtomic.tests.wallet.infrastructure.exception.StripeAmountTooSmallException;
import com.playtomic.tests.wallet.infrastructure.exception.StripeServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class StripeServiceTest {

    private static final URI CHARGES_URI = URI.create("http://stripe/charges");
    private static final URI REFUNDS_URI = URI.create("http://stripe/refunds");

    private StripeService stripeService;

    //Intercepts and controls what the RestTemplate responds without making any real call
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new StripeRestTemplateResponseErrorHandler());

        mockServer = MockRestServiceServer.createServer(restTemplate);

        stripeService = new StripeService(CHARGES_URI, REFUNDS_URI, restTemplate);
    }

    @Test
    void test_exception() {
        mockServer.expect(requestTo(CHARGES_URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNPROCESSABLE_ENTITY));
        Assertions.assertThrows(StripeAmountTooSmallException.class, () -> {
            stripeService.charge("4242 4242 4242 4242", new BigDecimal(5));
        });
    }

    @Test
    void test_ok() throws StripeServiceException {
        String mockResponse = """
        {"id": "paymentId"}""";

        mockServer.expect(requestTo(CHARGES_URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        Payment result = stripeService.charge("4242 4242 4242 4242", new BigDecimal(15));
        assertEquals("paymentId", result.getId());

        mockServer.verify();
    }
}
