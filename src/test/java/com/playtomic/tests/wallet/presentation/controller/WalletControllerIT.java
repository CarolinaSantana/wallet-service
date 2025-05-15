package com.playtomic.tests.wallet.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.presentation.dto.CreateWalletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class WalletControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
                .andExpect(jsonPath("$.availableBalance").value(0));
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

}
