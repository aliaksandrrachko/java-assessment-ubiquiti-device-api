package io.github.aliaksandrrachko.ubiquiti.device.app.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice;
import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice.DeviceType;
import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDeviceTopologyEntry;
import io.github.aliaksandrrachko.ubiquiti.device.app.repository.NetworkDeploymentDeviceRepository;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NetworkDeploymentDeviceRestControllerTest {

    private @Autowired NetworkDeploymentDeviceRepository repository;

    private RestTestClient restTestClient;

    @BeforeEach void setUp(WebApplicationContext webApplicationContext) {
        restTestClient = RestTestClient.bindToApplicationContext(webApplicationContext).build();
    }

    @AfterEach void clearRepository() {
        repository.clear();
    }

    @Test
    void shouldRegisterDevice() {
        // given
        NetworkDevice networkDevice = new NetworkDevice("02:A3:4F:12:8D:C7", DeviceType.GATEWAY, null);

        // when & then
        registerDevice(networkDevice).expectStatus().isOk().expectBody(NetworkDevice.class).consumeWith(assertThatEqualsTo(networkDevice));
    }

    @Test
    void shouldRegisterDeviceWithUplink() {
        // given
        NetworkDevice gateway = new NetworkDevice("AA:BB:CC:DD:EE:01", DeviceType.GATEWAY, null);
        registerDevice(gateway).expectStatus().isOk();

        NetworkDevice switchDevice = new NetworkDevice("AA:BB:CC:DD:EE:02", DeviceType.SWITCH, "AA:BB:CC:DD:EE:01");

        // when & then
        registerDevice(switchDevice).expectStatus().isOk().expectBody(NetworkDevice.class).consumeWith(assertThatEqualsTo(switchDevice));
    }

    @ParameterizedTest
    @MethodSource(value = {
        "invalidDevicesWithInvalidMacAddresses",
        "invalidDevicesWithUnexistingUplinkDevice"
    })
    void shouldRejectInvalidDevice(
        // given
        NetworkDevice device, String expectedDetail
    ) {
        // when & then
        registerDevice(device).expectStatus().isBadRequest().expectBody()
            .jsonPath("$.detail").value(detail -> assertThat(detail.toString()).contains(expectedDetail));
    }

    static Stream<Arguments> invalidDevicesWithInvalidMacAddresses() {
        String expectedMsg = "Invalid request content";
        return Stream.of(
            arguments(new NetworkDevice("invalid-mac", DeviceType.GATEWAY, null), expectedMsg),
            arguments(new NetworkDevice(null, DeviceType.GATEWAY, null), expectedMsg),
            arguments(new NetworkDevice("", DeviceType.GATEWAY, null), expectedMsg),
            arguments(new NetworkDevice("AA:BB:CC:DD:EE:FF", DeviceType.GATEWAY, "invalid-uplink"), expectedMsg),
            arguments(new NetworkDevice("AA:BB:CC:DD:EE:FF", DeviceType.GATEWAY, ""), expectedMsg),
            arguments(new NetworkDevice("AA:BB:CC:DD:EE:FF", null, null), expectedMsg)
        );
    }

    static Stream<Arguments> invalidDevicesWithUnexistingUplinkDevice() {
        return Stream.of(
            arguments(new NetworkDevice("AA:BB:CC:DD:EE:03", DeviceType.SWITCH, "FF:FF:FF:FF:FF:FF"), "Uplink device not found")
        );
    }

    @Test
    void shouldFindAllDevicesSortedByType() {
        // given
        List<NetworkDevice> networkDevices = List.of(
            new NetworkDevice("AA:BB:CC:DD:EE:11", DeviceType.GATEWAY, null),
            new NetworkDevice("AA:BB:CC:DD:EE:12", DeviceType.ACCESS_POINT, null),
            new NetworkDevice("AA:BB:CC:DD:EE:13", DeviceType.SWITCH, null),
            new NetworkDevice("AA:BB:CC:DD:EE:14", DeviceType.GATEWAY, "AA:BB:CC:DD:EE:11")
        );

        networkDevices.forEach(device -> registerDevice(device).expectStatus().isOk());

        // when & then - sorted by GATEWAY > SWITCH > ACCESS_POINT
        findAllDevices().expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<List<NetworkDevice>>() {})
            .consumeWith(exchangeResult ->
                assertThat(exchangeResult.getResponseBody()).hasSameSizeAs(networkDevices)
                    .isSortedAccordingTo(Comparator.comparing(NetworkDevice::getType)));
    }

    @Test
    void shouldGetDeviceByMacAddress() {
        // given
        NetworkDevice device = new NetworkDevice("AA:BB:CC:DD:EE:21", DeviceType.GATEWAY, null);
        registerDevice(device).expectStatus().isOk();

        // when & then
        getDevice(device.getMacAddress()).expectStatus().isOk().expectBody(NetworkDevice.class).consumeWith(assertThatEqualsTo(device));
    }

    @Test
    void shouldReturnNoContent_WhenDeviceNotFound() {
        // given & when & then
        getDevice("FF:FF:FF:FF:FF:FF").expectStatus().isNoContent();
    }

    @Test
    void shouldGetTopology() {
        // given
        NetworkDevice gateway = new NetworkDevice("AA:BB:CC:DD:EE:31", DeviceType.GATEWAY, null);
        NetworkDevice switchDevice = new NetworkDevice("AA:BB:CC:DD:EE:32", DeviceType.SWITCH, "AA:BB:CC:DD:EE:31");
        NetworkDevice accessPoint = new NetworkDevice("AA:BB:CC:DD:EE:33", DeviceType.ACCESS_POINT, "AA:BB:CC:DD:EE:32");

        registerDevice(gateway).expectStatus().isOk();
        registerDevice(switchDevice).expectStatus().isOk();
        registerDevice(accessPoint).expectStatus().isOk();

        // when & then
        getNetworkTopology().expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<List<NetworkDeviceTopologyEntry>>() {})
            .consumeWith(exchangeResult -> {
                List<NetworkDeviceTopologyEntry> topology = exchangeResult.getResponseBody();
                assertThat(topology).isNotNull().hasSize(1);
                
                NetworkDeviceTopologyEntry root = topology.get(0);
                assertThat(root.getMacAddress()).isEqualTo("AA:BB:CC:DD:EE:31");
                assertThat(root.getType()).isEqualTo(DeviceType.GATEWAY);
                assertThat(root.getLinkedDevices()).hasSize(1);
                
                NetworkDeviceTopologyEntry child = root.getLinkedDevices().get(0);
                assertThat(child.getMacAddress()).isEqualTo("AA:BB:CC:DD:EE:32");
                assertThat(child.getType()).isEqualTo(DeviceType.SWITCH);
                assertThat(child.getLinkedDevices()).hasSize(1);
                
                NetworkDeviceTopologyEntry grandChild = child.getLinkedDevices().get(0);
                assertThat(grandChild.getMacAddress()).isEqualTo("AA:BB:CC:DD:EE:33");
                assertThat(grandChild.getType()).isEqualTo(DeviceType.ACCESS_POINT);
                assertThat(grandChild.getLinkedDevices()).isNull();
            });
    }

    @Test
    void shouldGetTopologyWithMultipleRoots() {
        // given
        NetworkDevice gateway1 = new NetworkDevice("AA:BB:CC:DD:EE:41", DeviceType.GATEWAY, null);
        NetworkDevice gateway2 = new NetworkDevice("AA:BB:CC:DD:EE:42", DeviceType.GATEWAY, null);

        registerDevice(gateway1).expectStatus().isOk();
        registerDevice(gateway2).expectStatus().isOk();

        // when & then
        getNetworkTopology().expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<List<NetworkDeviceTopologyEntry>>() {})
            .consumeWith(exchangeResult ->
                assertThat(exchangeResult.getResponseBody()).isNotNull().hasSize(2));
    }

    @Test
    void shouldReturnEmptyTopology_WhenNoDevicesRegistered() {
        // when & then
        getNetworkTopology().expectStatus().isOk()
            .expectBody(new ParameterizedTypeReference<List<NetworkDeviceTopologyEntry>>() {})
            .consumeWith(exchangeResult ->
                assertThat(exchangeResult.getResponseBody()).isNotNull().isEmpty());
    }

    @Test
    void shouldGetTopologyFromDevice() {
        // given
        NetworkDevice gateway = new NetworkDevice("AA:BB:CC:DD:EE:51", DeviceType.GATEWAY, null);
        NetworkDevice switchDevice = new NetworkDevice("AA:BB:CC:DD:EE:52", DeviceType.SWITCH, "AA:BB:CC:DD:EE:51");
        NetworkDevice accessPoint = new NetworkDevice("AA:BB:CC:DD:EE:53", DeviceType.ACCESS_POINT, "AA:BB:CC:DD:EE:52");

        registerDevice(gateway).expectStatus().isOk();
        registerDevice(switchDevice).expectStatus().isOk();
        registerDevice(accessPoint).expectStatus().isOk();

        // when & then
        getNetworkTopology("AA:BB:CC:DD:EE:52").expectStatus().isOk().expectBody(NetworkDeviceTopologyEntry.class)
            .consumeWith(exchangeResult -> {
                NetworkDeviceTopologyEntry root = exchangeResult.getResponseBody();
                assertThat(root).isNotNull();
                assertThat(root.getMacAddress()).isEqualTo("AA:BB:CC:DD:EE:52");
                assertThat(root.getType()).isEqualTo(DeviceType.SWITCH);
                assertThat(root.getLinkedDevices()).hasSize(1);
                assertThat(root.getLinkedDevices().get(0).getMacAddress()).isEqualTo("AA:BB:CC:DD:EE:53");
            });
    }

    @Test
    void shouldReturnNoContentTopology_WhenDeviceIsNotRegistered() {
        // when & then
        getNetworkTopology("FF:FF:FF:FF:FF:FF").expectStatus().isNoContent();
    }

    private @NonNull Consumer<EntityExchangeResult<NetworkDevice>> assertThatEqualsTo(NetworkDevice device) {
        return exchangeResult ->
            Assertions.assertThat(exchangeResult.getResponseBody()).isNotNull().usingRecursiveComparison().isEqualTo(device);
    }

    private RestTestClient.@NonNull ResponseSpec registerDevice(NetworkDevice networkDevice) {
        return restTestClient.post().uri("/network-deployments/devices").body(networkDevice).exchange();
    }

    private RestTestClient.@NonNull ResponseSpec getDevice(String macAddress) {
        return restTestClient.get().uri("/network-deployments/devices/{macAddress}", macAddress).exchange();
    }

    private RestTestClient.@NonNull ResponseSpec findAllDevices() {
        return restTestClient.get().uri("/network-deployments/devices").exchange();
    }

    private RestTestClient.@NonNull ResponseSpec getNetworkTopology() {
        return restTestClient.get().uri("/network-deployments/devices/topology").exchange();
    }

    private RestTestClient.@NonNull ResponseSpec getNetworkTopology(String macAddress) {
        return restTestClient.get().uri("/network-deployments/devices/topology/{macAddress}", macAddress).exchange();
    }
}
