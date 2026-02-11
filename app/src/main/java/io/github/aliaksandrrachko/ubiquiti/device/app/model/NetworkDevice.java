package io.github.aliaksandrrachko.ubiquiti.device.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkDevice {

    private String macAddress;
    private DeviceType type;
    private String uplinkMacAddress;

    public enum DeviceType {
        GATEWAY,
        SWITCH,
        ACCESS_POINT
    }
}
