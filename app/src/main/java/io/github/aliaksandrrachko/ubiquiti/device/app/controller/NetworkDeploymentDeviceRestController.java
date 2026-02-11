package io.github.aliaksandrrachko.ubiquiti.device.app.controller;

import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/network-deployments/devices")
public class NetworkDeploymentDeviceRestController {

    @PutMapping
    public Optional<NetworkDevice> registerDevice(@Valid @RequestBody NetworkDevice networkDevice){
        return Optional.of(networkDevice);
    }
}
