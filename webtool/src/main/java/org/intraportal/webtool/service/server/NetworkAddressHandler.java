package org.intraportal.webtool.service.server;

import org.intraportal.api.service.shell.CommandProcessHandler;
import org.intraportal.persistence.repository.audit.ActionAudit;
import org.intraportal.webtool.dto.NetworkAdapterDetailsDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.intraportal.persistence.model.audit.AuditAction.CHANGE_NETWORK;
import static org.intraportal.persistence.model.audit.AuditDomain.SERVER;
import static org.intraportal.api.exception.ApiExceptionHandler.handleProcessException;
import static java.util.stream.Collectors.toList;

@Service("NetworkAddressHandler")
public class NetworkAddressHandler {

    private static final Logger logger = LoggerFactory.getLogger(NetworkAddressHandler.class);

    private final CommandProcessHandler commandProcessHandler;
    private final String defaultDNS;

    public NetworkAddressHandler(CommandProcessHandler commandProcessHandler, @Value("${network-management.dns.default}") String defaultDNS) {
        this.commandProcessHandler = commandProcessHandler;
        this.defaultDNS = defaultDNS;
    }

    public List<NetworkAdapterDetailsDto> readAdapterSettings() throws Exception {
        var listAdaptersResult = readNetworkManagerActiveAdapters();
        logger.info("List adapters CMD output: {}", listAdaptersResult);

        var adapterNames = listAdaptersResult.stream()
                .map(outputLine -> outputLine.trim().split(":"))
                .filter(lineFields -> lineFields.length > 0)
                .map(lineFields -> lineFields[0])
                .collect(toList());
        logger.info("Filtered adapter names: {}", adapterNames);

        var networkAdapterDetails = adapterNames.stream()
                .map(this::readNetworkManagerAdapterDetails)
                .filter(Optional::isPresent)
                .map(outputLines -> outputLines.get())
                .map(NetworkAdapterDetailsParser::toNetworkAdapterDetailsDto)
                .collect(toList());
        return networkAdapterDetails;
    }

    private List<String> readNetworkManagerActiveAdapters() throws Exception {
        return commandProcessHandler.executeSingleCommandWithOutput(handleProcessException(),
                "nmcli", "-t", "connection", "show", "--active");
    }

    private Optional<List<String>> readNetworkManagerAdapterDetails(String adapterName) {
        try {
            return Optional.of(commandProcessHandler.executeSingleCommandWithOutput(handleProcessException(),
                    "nmcli", "-t", "connection", "show", adapterName));
        } catch (Exception exception) {
            logger.error("Error while calling Network manager for adapter \"{}\": {}", adapterName, exception.getMessage());
            return Optional.empty();
        }
    }

    @ActionAudit(action = CHANGE_NETWORK, domain = SERVER)
    public void changeNetworkAddress(String name, String ipAddressCidr, String defaultGateway, String DNS) throws Exception {
        var processedDNS = StringUtils.isNotBlank(DNS) ? DNS : defaultDNS;
        commandProcessHandler.executeSingleCommand(handleProcessException(),
                "./set-address.sh", name, ipAddressCidr, defaultGateway, processedDNS);
    }

}
