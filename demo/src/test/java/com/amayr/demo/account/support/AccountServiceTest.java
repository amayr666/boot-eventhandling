package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import com.amayr.demo.event.Event;
import com.amayr.demo.event.support.EventRepository;
import com.amayr.demo.exception.DocumentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    private @Mock AccountRepository accountRepository;
    private @Mock EventRepository eventRepository;

    private AccountService accountService;

    private static String accountName = "dummy";
    private static String accountId = "id123";

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, eventRepository);
    }

    @Test
    void updateAccountFailsIfNotExisting() {
        given(accountRepository.existsById(any())).willReturn(false);

        assertThatThrownBy(() -> accountService.update(account()))
                .isInstanceOf(DocumentNotFoundException.class)
                .hasMessage("Document 'com.amayr.demo.account.Account' with id 'id123' has not been found");
    }

    @Test
    void updateAccount() {
        given(accountRepository.existsById(accountId)).willReturn(true);
        given(accountRepository.save(any())).willAnswer(arg -> arg.getArgument(0));

        Account updatedAccount = accountService.update(account());
        assertThat(updatedAccount).isNotNull();
        verify(accountRepository, times(1)).save(any());
    }

    @Test
    void failsAddingEventIfAccountNotFound() {
        given(accountRepository.findById(accountId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.addEvent(accountId, event()))
                .isInstanceOf(DocumentNotFoundException.class)
                .hasMessage("Document 'com.amayr.demo.account.Account' with id 'id123' has not been found");
    }

    @Test
    void addsEvent() {
        given(accountRepository.findById(accountId)).willReturn(Optional.of(account()));
        given(eventRepository.insert((Event) any())).willAnswer(arg -> arg.getArgument(0));

        Event event = accountService.addEvent(accountId, event());
        assertThat(event).isNotNull();
        verify(eventRepository, times(1)).insert((Event) any());
    }

    @Test
    void findAccountOrFailWithNotExistingAccount() {
        given(accountRepository.findById(accountId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findAccountOrFail(accountId))
                .isInstanceOf(DocumentNotFoundException.class)
                .hasMessage("Document 'com.amayr.demo.account.Account' with id 'id123' has not been found");
    }

    @Test
    void findAccountOrFailFindsAccount() {
        given(accountRepository.findById(accountId)).willReturn(Optional.of(account()));

        assertThat(accountService.findAccountOrFail(accountId)).isNotNull();
    }

    private Account account() {
        Account account = new Account(accountName);
        ReflectionTestUtils.setField(account, "id", accountId);
        return account;
    }

    private Event event() {
        Event event = new Event(accountId, "type");
        return event;
    }
}
