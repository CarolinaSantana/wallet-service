package com.playtomic.tests.wallet.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.domain.enums.WalletStatus;
import com.playtomic.tests.wallet.domain.model.Wallet;
import com.playtomic.tests.wallet.infrastructure.client.StripeService;
import com.playtomic.tests.wallet.infrastructure.dto.Payment;
import com.playtomic.tests.wallet.infrastructure.repository.WalletRepository;
import com.playtomic.tests.wallet.presentation.dto.CreateWalletRequest;
import com.playtomic.tests.wallet.presentation.dto.CreditCardNumber;
import com.playtomic.tests.wallet.presentation.dto.TopUpWalletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WalletControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WalletRepository walletRepository;

    @MockBean
    private StripeService stripeService;

    @BeforeEach
    void cleanDatabase() {
        walletRepository.deleteAll();
    }

    @Test
    void createWallet_ok() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest("alias", "userId");

        mockMvc.perform(post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.alias").value("alias"))
                .andExpect(jsonPath("$.userId").value("userId"))
                .andExpect(jsonPath("$.totalBalance").value(0))
                .andExpect(jsonPath("$.availableBalance").value(0))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void createWallet_withNullAlias_badRequest() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest(null, "userId");

        mockMvc.perform(post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWallet_withNullUserId_badRequest() throws Exception {
        CreateWalletRequest request = new CreateWalletRequest("alias", null);

        mockMvc.perform(post("/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWallet_ok() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setAlias("alias");
        wallet.setUserId("userId");
        wallet.setTotalBalance(BigDecimal.ZERO);
        wallet.setAvailableBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(new Date());
        wallet.setStatus(WalletStatus.PENDING);
        wallet = walletRepository.save(wallet);

        mockMvc.perform(get("/wallet/{id}", wallet.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.alias").value("alias"))
                .andExpect(jsonPath("$.userId").value("userId"))
                .andExpect(jsonPath("$.totalBalance").value(0))
                .andExpect(jsonPath("$.availableBalance").value(0))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getWallet_documentNotFoundException() throws Exception {
        mockMvc.perform(get("/wallet/{id}", "-"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("The Wallet with id - does not exist")));
    }

    @Test
    void topUpWallet_ok() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setAlias("alias");
        wallet.setUserId("userId");
        wallet.setTotalBalance(BigDecimal.ZERO);
        wallet.setAvailableBalance(BigDecimal.ZERO);
        wallet.setCreatedAt(new Date());
        wallet.setStatus(WalletStatus.PENDING);
        wallet = walletRepository.save(wallet);

        TopUpWalletRequest request = new TopUpWalletRequest();
        request.setWalletId(wallet.getId());
        request.setTotal(BigDecimal.valueOf(50));
        request.setCreditCardNumber(new CreditCardNumber("1111111111111111"));

        when(stripeService.charge(request.getCreditCardNumber().value(), request.getTotal()))
                .thenReturn(new Payment("id"));

        mockMvc.perform(post("/wallet/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(wallet.getId()))
                .andExpect(jsonPath("$.totalBalance").value(50));
    }

    @Test
    void topUpWallet_documentNotFoundException() throws Exception {
        TopUpWalletRequest request = new TopUpWalletRequest();
        request.setWalletId("-");
        request.setTotal(BigDecimal.valueOf(50));
        request.setCreditCardNumber(new CreditCardNumber("1111111111111111"));

        mockMvc.perform(post("/wallet/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Wallet with id - not found")));
    }

    @Test
    void topUpWallet_invalidRequest() throws Exception {
        TopUpWalletRequest request = new TopUpWalletRequest();

        mockMvc.perform(post("/wallet/top-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

}
