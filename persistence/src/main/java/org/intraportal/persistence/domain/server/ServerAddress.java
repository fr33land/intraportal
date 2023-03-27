package org.intraportal.persistence.domain.server;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class ServerAddress {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime time;
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime timeUTC;

}
