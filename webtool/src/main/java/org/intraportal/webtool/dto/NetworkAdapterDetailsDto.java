package org.intraportal.webtool.dto;

import org.apache.commons.net.util.SubnetUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Objects;

public class NetworkAdapterDetailsDto {

    private String name;

    @NotEmpty
    @Size(max = 15)
    private String ipAddress;

    @NotEmpty
    @Size(max = 15)
    private String subnetMask;

    @NotEmpty
    @Size(max = 15)
    private String defaultGateway;

    @Size(max = 15)
    private String dns;

    public NetworkAdapterDetailsDto() {
    }

    public NetworkAdapterDetailsDto(String name, String ipAddress, String subnetMask, String defaultGateway, String dns) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.subnetMask = subnetMask;
        this.defaultGateway = defaultGateway;
        this.dns = dns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public String getDefaultGateway() {
        return defaultGateway;
    }

    public void setDefaultGateway(String defaultGateway) {
        this.defaultGateway = defaultGateway;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkAdapterDetailsDto that = (NetworkAdapterDetailsDto) o;
        return Objects.equals(name, that.name) && Objects.equals(ipAddress, that.ipAddress)
                && Objects.equals(subnetMask, that.subnetMask) && Objects.equals(defaultGateway, that.defaultGateway)
                && Objects.equals(dns, that.dns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ipAddress, subnetMask, defaultGateway, dns);
    }

    @Override
    public String toString() {
        return "NetworkAdapterDetailsDto{" +
                "name='" + name + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", subnetMask='" + subnetMask + '\'' +
                ", defaultGateway='" + defaultGateway + '\'' +
                ", dns='" + dns + '\'' +
                '}';
    }

    public static class Builder {

        private String name;
        private String ipAddress;
        private String subnetMask;
        private String defaultGateway;
        private String dns;

        private Builder() {
        }

        public static NetworkAdapterDetailsDto.Builder create() {
            return new NetworkAdapterDetailsDto.Builder();
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder usingIpAddressInCIDR(String ipAddressCIDR) {
            var subnetInfo = new SubnetUtils(ipAddressCIDR)
                    .getInfo();

            this.ipAddress = subnetInfo.getAddress();
            this.subnetMask = subnetInfo.getNetmask();

            return this;
        }

        public Builder withDefaultGateway(String defaultGateway) {
            this.defaultGateway = defaultGateway;
            return this;
        }

        public Builder withDns(String dns) {
            this.dns = dns;
            return this;
        }

        public NetworkAdapterDetailsDto build() {
            return new NetworkAdapterDetailsDto(name, ipAddress, subnetMask, defaultGateway, dns);
        }

    }

}
