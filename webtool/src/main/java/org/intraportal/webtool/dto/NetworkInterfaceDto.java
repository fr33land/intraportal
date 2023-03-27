package org.intraportal.webtool.dto;

import java.util.List;

public class NetworkInterfaceDto {

    private Integer index;
    private String name;
    private List<NetworkAddressDto> addresses;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NetworkAddressDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<NetworkAddressDto> addresses) {
        this.addresses = addresses;
    }

}
