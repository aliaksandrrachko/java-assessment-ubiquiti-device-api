package io.github.aliaksandrrachko.ubiquiti.device.app.repository;

import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice;
import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDeviceTopologyEntry;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class NetworkDeploymentDeviceRepositoryInMemoryImpl implements NetworkDeploymentDeviceRepository {

    private final Map<String, NetworkDevice> devices = new ConcurrentHashMap<>();

    @Override
    public NetworkDevice register(NetworkDevice networkDevice) {
        if (networkDevice.getUplinkMacAddress() != null && !devices.containsKey(networkDevice.getUplinkMacAddress())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Uplink device not found: %s".formatted(networkDevice.getUplinkMacAddress()));
        }

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

    @Override
    public List<NetworkDeviceTopologyEntry> getTopology() {
        return devices.values().stream()
            .filter(d -> d.getUplinkMacAddress() == null)
            .map(this::buildTree)
            .toList();
    }

    @Override
    public Optional<NetworkDeviceTopologyEntry> getTopologyFromDevice(String macAddress) {
        NetworkDevice device = devices.get(macAddress);
        if (device == null) {
            return Optional.empty();
        }
        return Optional.of(buildTree(device));
    }

    private NetworkDeviceTopologyEntry buildTree(NetworkDevice device) {
        List<NetworkDeviceTopologyEntry> linkedDevices = devices.values().stream()
            .filter(d -> device.getMacAddress().equals(d.getUplinkMacAddress()))
            .map(this::buildTree)
            .toList();
        
        return NetworkDeviceTopologyEntry.builder()
            .macAddress(device.getMacAddress())
            .type(device.getType())
            .linkedDevices(linkedDevices.isEmpty() ? null : linkedDevices)
            .build();
    }

    @Override
    public void clear() {
        devices.clear();
    }
}
