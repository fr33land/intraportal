package org.intraportal.webtool.service;

import org.intraportal.persistence.domain.date.FormattedTimeRange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DatePickerPeriodHandlerTest {

    MockedStatic<Clock> clockMock;

    @Mock
    Model model;

    @Captor
    ArgumentCaptor<List<FormattedTimeRange>> timeRangeCaptor;

    DatePickerPeriodHandler datePickerPeriodHandler;

    @BeforeEach
    void setUp() {
        datePickerPeriodHandler = new DatePickerPeriodHandler();

        var fixedClock = Clock.fixed(Instant.parse("2022-08-29T08:15:05Z"), ZoneId.of("UTC"));
        clockMock = mockStatic(Clock.class);
        clockMock.when(Clock::systemDefaultZone).thenReturn(fixedClock);
    }

    @AfterEach
    void tearDown() {
        clockMock.close();
    }


    @Test
    void fillInDefaultDatePickerModelParameters_noTime_addsExpectedDateFields() {
        datePickerPeriodHandler.fillInDefaultDatePickerModelParameters(model, false);

        String expectedToday = "2022-08-29";
        String expectedPeriodEnd = "2022-08-30";

        verify(model).addAttribute("startDateModel", expectedToday);
        verify(model).addAttribute("endDateModel", expectedPeriodEnd);
        verify(model).addAttribute("startDatePicker", expectedToday);
        verify(model).addAttribute("endDatePicker", expectedPeriodEnd);
        verify(model).addAttribute(eq("datePickerRanges"), timeRangeCaptor.capture());
        assertThat(timeRangeCaptor.getValue())
                .containsExactlyInAnyOrder(
                        new FormattedTimeRange("Today", expectedToday, expectedPeriodEnd),
                        new FormattedTimeRange("Yesterday", "2022-08-28", expectedToday),
                        new FormattedTimeRange("Last 7 Days", "2022-08-22", expectedPeriodEnd),
                        new FormattedTimeRange("Last 30 Days", "2022-07-30", expectedPeriodEnd));
    }

    @Test
    void fillInDefaultDatePickerModelParameters_withTime_addsExpectedDateFields() {
        datePickerPeriodHandler.fillInDefaultDatePickerModelParameters(model, true);

        String expected24HoursAgo = "2022-08-28 08:15";
        String expectedToday = "2022-08-29 00:00";
        String expectedPeriodEnd = "2022-08-30 00:00";

        verify(model).addAttribute("startDateModel", expectedToday);
        verify(model).addAttribute("endDateModel", expectedPeriodEnd);
        verify(model).addAttribute("startDatePicker", expectedToday);
        verify(model).addAttribute("endDatePicker", expectedPeriodEnd);
        verify(model).addAttribute(eq("datePickerRanges"), timeRangeCaptor.capture());
        assertThat(timeRangeCaptor.getValue())
                .containsExactlyInAnyOrder(
                        new FormattedTimeRange("Last 24 hours", expected24HoursAgo, expectedPeriodEnd),
                        new FormattedTimeRange("Today", expectedToday, expectedPeriodEnd),
                        new FormattedTimeRange("Yesterday", "2022-08-28 00:00", expectedToday),
                        new FormattedTimeRange("Last 7 Days", "2022-08-22 00:00", expectedPeriodEnd),
                        new FormattedTimeRange("Last 30 Days", "2022-07-30 00:00", expectedPeriodEnd));
    }

    @Test
    void fillInDatePickerModelParameter_noTimeFormatter_addsFormattedDate() {
        LocalDate minDatePickerDate = LocalDate.of(2022, 9, 1);
        datePickerPeriodHandler.fillInDatePickerModelParameter("minDatePicker", minDatePickerDate, model, false);

        verify(model).addAttribute("minDatePicker", "2022-09-01");
    }

}