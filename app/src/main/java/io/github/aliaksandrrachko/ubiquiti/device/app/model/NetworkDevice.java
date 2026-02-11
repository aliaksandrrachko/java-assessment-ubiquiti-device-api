package io.github.aliaksandrrachko.ubiquiti.device.app.model;

import io.github.aliaksandrrachko.ubiquiti.device.app.validation.MacAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkDevice {

    @NotBlank(message = "MAC address is required")
    @MacAddress
    private String macAddress;

    @NotNull(message = "Device type is required")
    private DeviceType type;

    @MacAddress
    private String uplinkMacAddress;

    public enum DeviceType {
        GATEWAY,
        SWITCH,
        ACCESS_POINT
    }
}
