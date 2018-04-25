package com.shykhmat.vcsstatistics.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class that provides utility functionality to work with Date API.
 *
 */
public final class DateUtils {
    private DateUtils() {

    }

    /**
     * Method to get all dates in range.
     * 
     * @param dateFrom
     *            - first date in range
     * @param dateTo
     *            - last date in range
     * @return collection of dates in range
     */
    public static List<LocalDate> getDatesBetweenUsing(LocalDate dateFrom, LocalDate dateTo) {
        // TODO in Java 9 can be just: return
        // dateFrom.datesUntil(dateTo).collect(Collectors.toList());
        long numOfDaysBetween = ChronoUnit.DAYS.between(dateFrom, dateTo);
        return IntStream.iterate(0, i -> i + 1).limit(numOfDaysBetween).mapToObj(i -> dateFrom.plusDays(i)).collect(Collectors.toList());
    }

    /**
     * Method to convert seconds value into {@link LocalDate} instance
     * 
     * @param seconds
     *            - seconds to be converted
     * @return converted seconds value into {@link LocalDate} instance
     */
    public static LocalDate toLocalDate(long seconds) {
        Instant instant = Instant.ofEpochSecond(seconds);
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        return zonedDateTime.toLocalDate();
    }
}
