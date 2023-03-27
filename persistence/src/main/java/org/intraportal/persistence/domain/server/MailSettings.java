package org.intraportal.persistence.domain.server;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailSettings {

    @NotEmpty(message = "Host may not be empty")
    @Size(min = 2, max = 32, message = "Host field must be between 2 and 32 characters long")
    private String host;
    @NotNull
    @Min(value = 1)
    @Max(value = 65535)
    private Integer port;
    @NotEmpty(message = "User name may not be empty")
    @Size(min = 2, max = 32, message = "User name field must be between 2 and 32 characters long")
    private String userName;
    @NotEmpty(message = "Password may not be empty")
    @Size(min = 6, max = 32, message = "Password field must be between 6 and 32 characters long")
    private String password;

}
