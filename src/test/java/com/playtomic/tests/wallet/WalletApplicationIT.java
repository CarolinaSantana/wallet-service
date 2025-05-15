package com.playtomic.tests.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
class WalletApplicationIT {

	@Test
	void emptyTest() {
	}
}
