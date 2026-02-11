package io.github.aliaksandrrachko.ubiquiti.device.app.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$", message = "Invalid MAC address format")
    private String macAddress;

    @NotNull(message = "Device type is required")
    private DeviceType type;

    @Pattern(regexp = "^([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}$", message = "Invalid uplink MAC address format")
    private String uplinkMacAddress;

    public enum DeviceType {
        GATEWAY,
        SWITCH,
        ACCESS_POINT
    }
}
