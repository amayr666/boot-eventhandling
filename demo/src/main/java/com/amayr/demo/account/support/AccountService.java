package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import com.amayr.demo.event.Event;
import com.amayr.demo.event.support.EventRepository;
import com.amayr.demo.exception.DocumentNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
@Service
class AccountService {
    private AccountRepository accountRepository;
    private EventRepository eventRepository;

    Collection<Account> findAll() {
        return accountRepository.findAll();
    }

    Optional<Account> findById(String id) {
        return accountRepository.findById(id);
    }

    Account insert(Account account) {
        return accountRepository.insert(account);
    }

    Account update(Account account) {
        if (accountRepository.existsById(account.getId())) {
            return accountRepository.save(account);
        } else {
            throw constructArticleNotFoundException(account.getId());
        }
    }

    Event addEvent(String accountId, Event event) {
        findAccountOrFail(accountId);
        return eventRepository.insert(event);
    }

    Collection<Event> getEventsForAccount(String accountId) {
        findAccountOrFail(accountId);
        return eventRepository.findEventByAccountId(accountId);
    }

    private Account findAccountOrFail(String accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw constructArticleNotFoundException(accountId);
        }

        return account.get();
    }

    private DocumentNotFoundException constructArticleNotFoundException(String accountId) {
        return new DocumentNotFoundException(
                String.format("Document '%s' with id '%s' has not been found",
                        Account.class.getName(), accountId));
    }
}
