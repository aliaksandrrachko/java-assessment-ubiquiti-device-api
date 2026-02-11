package io.github.aliaksandrrachko.ubiquiti.device.app.repository;

import io.github.aliaksandrrachko.ubiquiti.device.app.model.NetworkDevice;
import java.util.List;
import java.util.Optional;

public interface NetworkDeploymentDeviceRepository {

    NetworkDevice register(NetworkDevice networkDevice);

    List<NetworkDevice> findAllSortedByType();

    Optional<NetworkDevice> findByMacAddress(String macAddress);
}
