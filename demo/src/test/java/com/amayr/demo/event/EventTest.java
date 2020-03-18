package com.amayr.demo.event;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventTest {

    @Test
    void setsDateTime() {
        Event event = new Event("123", "Type");
        assertThat(event.getHappenedAt()).isNotNull();
    }

    @Test
    void failsWhenTypeIsNull() {
        assertThatThrownBy(() -> new Event("id", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Type must not be empty!");
    }

    @Test
    void failsWhenTypeIsEmpty() {
        assertThatThrownBy(() -> new Event("id", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Type must not be empty!");
    }

    @Test
    void failsWhenTypeIsBlank() {
        assertThatThrownBy(() -> new Event("id", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Type must not be empty!");
    }

    @Test
    void failsWhenAccountIdIsNull() {
        assertThatThrownBy(() -> new Event(null, "type"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("AccountId must not be empty!");
    }

    @Test
    void failsWhenAccountIdIsBlank() {
        assertThatThrownBy(() -> new Event("", "type"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("AccountId must not be empty!");
    }

    @Test
    void failsWhenAccountIdIsEmpty() {
        assertThatThrownBy(() -> new Event("   ", "type"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("AccountId must not be empty!");
    }

    @Test
    void createsEvent() {
        String accountId = "aid";
        String type = "type";
        Event event = new Event(accountId, type);

        assertThat(event.getAccountId()).isEqualTo(accountId);
        assertThat(event.getType()).isEqualTo(type);
    }
}
