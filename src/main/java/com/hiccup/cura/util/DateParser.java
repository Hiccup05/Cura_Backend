package com.hiccup.cura.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class DateParser {
    private DateParser() {
        /* This utility class should not be instantiated */
    }

    public static LocalDate parseDate(String value, String fieldName) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be in YYYY-MM-DD format");
        }
    }
}
