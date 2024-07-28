package com.wanted.portfolio.global.util;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class Clock {

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}
