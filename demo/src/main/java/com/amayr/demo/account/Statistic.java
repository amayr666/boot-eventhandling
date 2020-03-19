package com.amayr.demo.account;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Data
public class Statistic {
    private String type;
    private LocalDate day;
    private Long count = 0L;

    public void increaseCount() {
        count++;
    }
}
