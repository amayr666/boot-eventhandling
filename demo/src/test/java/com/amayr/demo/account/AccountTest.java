package com.amayr.demo.account;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {
    @Test
    void failsWhenAccountNameIsEmpty() {
        assertThatThrownBy(() -> new Account(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be emtpy!");
    }

    @Test
    void failsWhenAccountNameIsNull() {
        assertThatThrownBy(() -> new Account(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be emtpy!");
    }

    @Test
    void failsWhenAccountNameIsBlank() {
        assertThatThrownBy(() -> new Account("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name must not be emtpy!");
    }

    @Test
    void createsAccount() {
        String accountName = "name";
        Account account = new Account(accountName);

        assertThat(account.getName()).isEqualTo(accountName);
    }

    @Test
    void addsStatisticAtTop() {
        Statistic statistic = new Statistic("existing", LocalDate.now(), 1L);
        Statistic newStatistic = new Statistic("new", LocalDate.now(), 1L);

        Account account = new Account("name");
        account.addStatistic(statistic);

        account.addStatistic(newStatistic);

        assertThat(account.getStatistics().get(0)).isEqualTo(newStatistic);
    }

    @Test
    void findsStatistic() {
        Statistic statistic = new Statistic("existing", LocalDate.now(), 1L);

        Account account = new Account("name");
        account.addStatistic(statistic);

        Optional<Statistic> found = account.findStatisticForDayAndType(statistic.getDay(), statistic.getType());
        assertThat(found).isPresent();
    }

    @Test
    void dontFindStatisticsIfOtherDay() {
        Statistic statistic = new Statistic("existing", LocalDate.now(), 1L);

        Account account = new Account("name");
        account.addStatistic(statistic);

        Optional<Statistic> found = account.findStatisticForDayAndType(statistic.getDay().plusDays(1), statistic.getType());
        assertThat(found).isEmpty();
    }

    @Test
    void dontFindStatisticsIfOtherType() {
        Statistic statistic = new Statistic("existing", LocalDate.now(), 1L);

        Account account = new Account("name");
        account.addStatistic(statistic);

        Optional<Statistic> found = account.findStatisticForDayAndType(statistic.getDay(), "different type");
        assertThat(found).isEmpty();
    }
}
