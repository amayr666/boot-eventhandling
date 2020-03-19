package com.amayr.demo.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "account")
public class Account {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
    List<Statistic> statistics = new ArrayList<>();

    public Account(String name) {
        updateName(name);
    }

    public void updateName(String name) {
        Assert.isTrue(!StringUtils.isBlank(name), "Name must not be emtpy!");

        if (!StringUtils.isBlank(name)) {
            this.name = name;
        }
    }

    public void addStatistic(Statistic statistic) {
        this.statistics.add(0, statistic);
    }

    @JsonIgnore
    public List<Statistic> getStatistics() {
        return Collections.unmodifiableList(statistics);
    }

    public Optional<Statistic> findStatisticForDayAndType(LocalDate day, String type) {
        for (Statistic statistic : statistics) {
            if (statistic.getType().equals(type) &&
                    DateUtils.isSameDay(Date.valueOf(day), Date.valueOf(statistic.getDay()))) {
                return Optional.of(statistic);
            }
        }

        return Optional.empty();
    }
}
