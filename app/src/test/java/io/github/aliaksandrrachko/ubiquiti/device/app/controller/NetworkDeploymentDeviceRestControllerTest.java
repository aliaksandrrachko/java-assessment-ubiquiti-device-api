package io.github.aliaksandrrachko.ubiquiti.device.app.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice;
import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice.DeviceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NetworkDeploymentDeviceRestControllerTest {

    private RestTestClient restTestClient;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        restTestClient = RestTestClient.bindToApplicationContext(webApplicationContext).build();
    }

    @Test
    void shouldRegisterDevice() {
        // given
        NetworkDevice networkDevice = new NetworkDevice("02:A3:4F:12:8D:C7", DeviceType.GATEWAY, null);

        // when & then
        restTestClient.post().uri("/network-deployments/devices").contentType(MediaType.APPLICATION_JSON).body(networkDevice).exchange()
            .expectStatus().isOk().expectBody(NetworkDevice.class).consumeWith(exchangeResult ->
                    assertThat(exchangeResult.getResponseBody()).isNotNull().usingRecursiveComparison().isEqualTo(networkDevice)
            );
    }
}
