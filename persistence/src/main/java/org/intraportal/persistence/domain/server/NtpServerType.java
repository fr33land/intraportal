package org.intraportal.persistence.domain.server;

import java.util.Arrays;

public enum NtpServerType {

    SERVER("server"),
    POOL("pool");

    private final String value;

    NtpServerType(String value) {
        this.value = value;
    }

    public static NtpServerType resolve(String serverTypeValue) {
        return Arrays.stream(values())
                .filter(value -> value.getValue().equals(serverTypeValue))
                .findFirst()
                .orElse(null);
    }

    public String getValue() {
        return value;
    }

}
