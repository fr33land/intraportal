package org.intraportal.webtool.service.mappers;

import org.intraportal.webtool.dto.NetworkAddressDto;
import org.intraportal.webtool.dto.NetworkInterfaceDto;

import java.net.InetAddress;
import java.net.NetworkInterface;

import static java.util.stream.Collectors.toList;

public class NetworkInterfaceMapper {

    public static NetworkInterfaceDto toNetworkInterfaceDto(NetworkInterface networkInterface) {
        var dto = new NetworkInterfaceDto();

        dto.setIndex(networkInterface.getIndex());
        dto.setName(networkInterface.getName());

        networkInterface.getInterfaceAddresses().stream()
                .map(interfaceAddress -> interfaceAddress.getAddress().getCanonicalHostName());
        var addressDtos = networkInterface.getInterfaceAddresses().stream()
                .map(interfaceAddress -> toNetworkAddressDto(interfaceAddress.getAddress()))
                .collect(toList());
        dto.setAddresses(addressDtos);

        return dto;
    }

    public static NetworkAddressDto toNetworkAddressDto(InetAddress inetAddress) {
        var dto = new NetworkAddressDto();

        dto.setAddress(inetAddress.getHostAddress());
        dto.setHostName(inetAddress.getHostName());
        dto.setCanonicalHostName(inetAddress.getCanonicalHostName());

        return dto;
    }

}