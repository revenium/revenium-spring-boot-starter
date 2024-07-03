package io.revenium.metering;

import io.revenium.metering.api.MeteringApi;
import io.revenium.metering.spring.ReveniumAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ReveniumAutoConfiguration.class, properties = {
        "revenium.metering.api-key=test-api-key",
        "revenium.metering.url=https://test-host.com"
})
@Import(ReveniumAutoConfiguration.class)
class MeteringAutoConfigurationTests {

    @Autowired
    private MeteringApi meteringApi;

    @Test
    void meteringApiBeanIsConfigured() {
        assertNotNull(meteringApi, "MeteringApi bean should not be null");
    }
}
