package com.amayr.demo.account;

import org.junit.jupiter.api.Test;

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
}
