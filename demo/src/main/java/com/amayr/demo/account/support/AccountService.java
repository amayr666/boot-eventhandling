package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import com.amayr.demo.exception.DocumentNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@AllArgsConstructor
@Service
public class AccountService {
    private AccountRepository accountRepository;

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
            throw new DocumentNotFoundException(
                    String.format("Document '%s' with id '%s' has not been found",
                            Account.class.getName(), account.getId()));
        }
    }
}
