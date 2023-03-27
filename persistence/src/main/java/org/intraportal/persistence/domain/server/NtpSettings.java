package org.intraportal.persistence.domain.server;

import java.util.List;
import java.util.Objects;

public class NtpSettings {

    private List<NtpServerConfiguration> serverConfigurations;

    public NtpSettings() {
    }

    public NtpSettings(List<NtpServerConfiguration> serverConfigurations) {
        this.serverConfigurations = serverConfigurations;
    }

    public List<NtpServerConfiguration> getServerConfigurations() {
        return serverConfigurations;
    }

    public void setServerConfigurations(List<NtpServerConfiguration> serverConfigurations) {
        this.serverConfigurations = serverConfigurations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NtpSettings that = (NtpSettings) o;
        return Objects.equals(serverConfigurations, that.serverConfigurations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverConfigurations);
    }

    @Override
    public String toString() {
        return "NtpSettings{" +
                "serverConfigurations=" + serverConfigurations +
                '}';
    }

}
