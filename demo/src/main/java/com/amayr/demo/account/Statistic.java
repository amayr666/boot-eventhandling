package com.amayr.demo.account;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@AllArgsConstructor
@Data
public class Statistic {
    private String type;
    private LocalDate day;
    private Long count = 0L;

    public void increaseCount() {
        count++;
    }
}
