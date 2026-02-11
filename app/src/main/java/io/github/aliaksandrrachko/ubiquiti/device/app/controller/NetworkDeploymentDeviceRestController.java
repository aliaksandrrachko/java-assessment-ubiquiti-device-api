package io.github.aliaksandrrachko.ubiquiti.device.app.controller;

import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice;
import io.github.aliaksandrrachko.ubiquiti.device.app.repository.NetworkDeploymentDeviceRepository;
import io.github.aliaksandrrachko.ubiquiti.device.app.validation.MacAddress;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;

@RestController
@RequestMapping(value = "/network-deployments/devices", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Network Deployment Devices", description = "API for managing network deployment devices")
@Validated
@RequiredArgsConstructor
public class NetworkDeploymentDeviceRestController {

    private final NetworkDeploymentDeviceRepository networkDeploymentDeviceRepository;

    @PostMapping
    @Operation(summary = "Register a device", description = "Register a new device to the network deployment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Device registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or uplink device not found")
    })
    public NetworkDevice registerDevice(@Valid @RequestBody NetworkDevice networkDevice) {
        return networkDeploymentDeviceRepository.register(networkDevice);
    }

    @GetMapping
    @Operation(summary = "Get all devices", description = "Retrieve all registered devices sorted by device type (Gateway > Switch > Access Point)")
    @ApiResponse(responseCode = "200", description = "List of devices retrieved successfully")
    public List<NetworkDevice> getAllDevicesSorted() {
        return networkDeploymentDeviceRepository.findAllSortedByType();
    }

    @GetMapping("/{macAddress}")
    @Operation(summary = "Get device by MAC address", description = "Retrieve a specific device by its MAC address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Device found"),
        @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public Optional<NetworkDevice> getDeviceByMacAddress(@MacAddressParameter @MacAddress @PathVariable String macAddress) {
        return networkDeploymentDeviceRepository.findByMacAddress(macAddress);
    }

    @GetMapping("/topology")
    @Operation(summary = "Get network topology", description = "Retrieve the complete network device topology as a tree structure")
    @ApiResponse(responseCode = "200", description = "Topology retrieved successfully")
    @ApiResponse(responseCode = "501", description = "Not implemented yet")
    public Object getTopology() {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED, "Topology endpoint not yet implemented");
    }

    @GetMapping("/topology/{macAddress}")
    @Operation(summary = "Get topology from device", description = "Retrieve network topology starting from a specific device")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Topology retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Device not found"),
        @ApiResponse(responseCode = "501", description = "Not implemented yet")
    })
    public Object getTopologyFromDevice(@MacAddressParameter @MacAddress @PathVariable String macAddress) {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED, "Topology endpoint not yet implemented");
    }
}
