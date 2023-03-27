package org.intraportal.persistence.domain.server;

import java.util.Objects;

public class NtpServerConfiguration {

    private NtpServerType type;

    private String address;

    public NtpServerConfiguration() {
    }

    public NtpServerConfiguration(NtpServerType type, String address) {
        this.type = type;
        this.address = address;
    }

    public NtpServerType getType() {
        return type;
    }

    public void setType(NtpServerType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NtpServerConfiguration that = (NtpServerConfiguration) o;
        return type == that.type && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, address);
    }

    @Override
    public String toString() {
        return "NtpServerConfiguration{" +
                "type=" + type +
                ", address='" + address + '\'' +
                '}';
    }

}
