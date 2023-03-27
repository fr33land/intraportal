package org.intraportal.webtool.service;

import org.intraportal.persistence.domain.date.FormattedTimeRange;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service("DatePickerPeriodHandler")
public class DatePickerPeriodHandler {

    private final DateTimeFormatter UI_COMPONENT_DATE_FORMATTER;
    private final DateTimeFormatter UI_COMPONENT_DATE_TIME_FORMATTER;

    public DatePickerPeriodHandler() {
        this.UI_COMPONENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.UI_COMPONENT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    public void fillInSingleSelectDatePickerModelParameters(Model model, boolean withTime) {
        var dateFormatter = getDateFormatter(withTime);
        LocalDateTime now = LocalDateTime.now();
        model.addAttribute("startDatePicker", now.format(dateFormatter));
    }

    public void fillInDefaultDatePickerModelParameters(Model model, boolean withTime) {
        var dateFormatter = getDateFormatter(withTime);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime periodEnd = today.plusDays(1);

        model.addAttribute("startDateModel", today.format(dateFormatter));
        model.addAttribute("endDateModel", periodEnd.format(dateFormatter));
        model.addAttribute("startDatePicker", today.format(dateFormatter));
        model.addAttribute("endDatePicker", periodEnd.format(dateFormatter));
        model.addAttribute("datePickerRanges", createDatePickerRanges(now, today, withTime, dateFormatter));
    }

    public void fillInDatePickerModelParameter(String name, LocalDate value, Model model, boolean withTime) {
        model.addAttribute(name, value.format(getDateFormatter(withTime)));
    }

    public List<FormattedTimeRange> createDatePickerRanges(LocalDateTime now, LocalDateTime today, boolean addLast24Hours, DateTimeFormatter dateFormatter) {
        LocalDateTime periodEnd = today.plusDays(1);
        LocalDateTime dayAgo = today.minusDays(1);
        LocalDateTime weekAgo = today.minusWeeks(1);
        LocalDateTime monthAgo = today.minusDays(30);

        var reportTimeRanges = new ArrayList<FormattedTimeRange>();

        if (addLast24Hours) {
            LocalDateTime dayAgoMinutesLevel = now.minusDays(1).truncatedTo(ChronoUnit.MINUTES);
            reportTimeRanges.add(new FormattedTimeRange("Last 24 hours", dayAgoMinutesLevel.format(dateFormatter), periodEnd.format(dateFormatter)));
        }
        reportTimeRanges.add(new FormattedTimeRange("Today", today.format(dateFormatter), periodEnd.format(dateFormatter)));
        reportTimeRanges.add(new FormattedTimeRange("Yesterday", dayAgo.format(dateFormatter), today.format(dateFormatter)));
        reportTimeRanges.add(new FormattedTimeRange("Last 7 Days", weekAgo.format(dateFormatter), periodEnd.format(dateFormatter)));
        reportTimeRanges.add(new FormattedTimeRange("Last 30 Days", monthAgo.format(dateFormatter), periodEnd.format(dateFormatter)));
        return reportTimeRanges;
    }

    private DateTimeFormatter getDateFormatter(boolean withTime) {
        return withTime ? UI_COMPONENT_DATE_TIME_FORMATTER : UI_COMPONENT_DATE_FORMATTER;
    }

}
