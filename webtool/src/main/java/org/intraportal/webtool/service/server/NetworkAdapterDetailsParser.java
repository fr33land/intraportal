package org.intraportal.webtool.service.server;

import org.intraportal.webtool.dto.NetworkAdapterDetailsDto;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class NetworkAdapterDetailsParser {

    public static NetworkAdapterDetailsDto toNetworkAdapterDetailsDto(List<String> networkManagerAdapterOutputLines) {
        var dtoBuilder = NetworkAdapterDetailsDto.Builder
                .create();

        networkManagerAdapterOutputLines.stream()
                .forEach(line -> parseLine(line, dtoBuilder));

        return dtoBuilder.build();
    }

    private static void parseLine(String networkManagerAdapterOutputLine, NetworkAdapterDetailsDto.Builder dtoBuilder) {
        if (StringUtils.isBlank(networkManagerAdapterOutputLine)) {
            return;
        }

        String[] lineChunks = networkManagerAdapterOutputLine.split(":");
        if (lineChunks.length >= 2) {

            var name = lineChunks[0].trim();
            var value = lineChunks[1].trim();

            if ("connection.id".equals(name)) {
                dtoBuilder.withName(value);
            } else if ("ipv4.addresses".equals(name)) {
                dtoBuilder.usingIpAddressInCIDR(value);
            } else if ("ipv4.gateway".equals(name)) {
                dtoBuilder.withDefaultGateway(value);
            } else if ("ipv4.dns".equals(name)) {
                dtoBuilder.withDns(value);
            }
        }
    }

}
