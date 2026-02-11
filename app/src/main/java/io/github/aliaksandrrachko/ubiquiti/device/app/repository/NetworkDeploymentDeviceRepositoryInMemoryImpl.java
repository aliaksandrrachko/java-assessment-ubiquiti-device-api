package io.github.aliaksandrrachko.ubiquiti.device.app.repository;

import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class NetworkDeploymentDeviceRepositoryInMemoryImpl implements NetworkDeploymentDeviceRepository {

    private final Map<String, NetworkDevice> devices = new ConcurrentHashMap<>();

    @Override
    public NetworkDevice register(NetworkDevice networkDevice) {
        devices.put(networkDevice.getMacAddress(), networkDevice);
        return networkDevice;
    }

    @Override
    public Optional<NetworkDevice> findByMacAddress(String macAddress) {
        return Optional.ofNullable(devices.get(macAddress));
    }

    @Override
    public List<NetworkDevice> findAllSortedByType() {
        return devices.values().stream()
            .sorted(Comparator.comparing(NetworkDevice::getType))
            .toList();
    }
}
