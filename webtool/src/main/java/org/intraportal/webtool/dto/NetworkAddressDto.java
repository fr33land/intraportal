package org.intraportal.webtool.dto;

public class NetworkAddressDto {

    private String address;
    private String hostName;
    private String canonicalHostName;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getCanonicalHostName() {
        return canonicalHostName;
    }

    public void setCanonicalHostName(String canonicalHostName) {
        this.canonicalHostName = canonicalHostName;
    }

}
