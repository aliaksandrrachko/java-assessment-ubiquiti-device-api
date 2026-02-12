package io.github.aliaksandrrachko.ubiquiti.device.app.model;

import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice.DeviceType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NetworkDeviceTopologyEntry {

    private String macAddress;
    private DeviceType type;
    private List<NetworkDeviceTopologyEntry> linkedDevices;
}
