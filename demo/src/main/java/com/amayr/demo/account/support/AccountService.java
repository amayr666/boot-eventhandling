package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import com.amayr.demo.account.Statistic;
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

    Collection<Account> findAllAccounts() {
        return accountRepository.findAll();
    }

    Optional<Account> findAccountById(String id) {
        return accountRepository.findById(id);
    }

    Account insertAccount(Account account) {
        return accountRepository.insert(account);
    }

    Account updateAccount(Account account) {
        if (accountRepository.existsById(account.getId())) {
            return accountRepository.save(account);
        } else {
            throw constructArticleNotFoundException(account.getId());
        }
    }

    Event addEventAndUpdateStatistics(String accountId, Event event) {
        Account account = findAccountOrFail(accountId);
        Event insertedEvent = eventRepository.insert(event);
        updateAccountStatistics(account, event);
        return insertedEvent;
    }

    Collection<Event> getEventsForAccount(String accountId) {
        findAccountOrFail(accountId);
        return eventRepository.findEventByAccountId(accountId);
    }

    Account findAccountOrFail(String accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (!account.isPresent()) {
            throw constructArticleNotFoundException(accountId);
        }
        return account.get();
    }

    void updateAccountStatistics(Account account, Event event) {
        Optional<Statistic> statistic = account.findStatisticForDayAndType(event.getHappenedAt().toLocalDate(), event.getType());
        if (statistic.isPresent()) {
            statistic.get().increaseCount();
        } else {
            Statistic newStatistic = new Statistic(event.getType(), event.getHappenedAt().toLocalDate(), 1L);
            account.addStatistic(newStatistic);
        }
        accountRepository.save(account);
    }

    private DocumentNotFoundException constructArticleNotFoundException(String accountId) {
        return new DocumentNotFoundException(
                String.format("Document '%s' with id '%s' has not been found",
                        Account.class.getName(), accountId));
    }
}
