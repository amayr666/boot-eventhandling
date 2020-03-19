package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import com.amayr.demo.event.Event;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@RestController
@RequestMapping("/api/account")
public class AccountController {
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<?> findAllAccounts() {
        return ResponseEntity.ok(accountService.findAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findAccountById(@PathVariable("id") String id) {
        return accountService.findAccountById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> insertAccount(@Valid @RequestBody CreateAccountRequest account) {
        return ResponseEntity.ok(accountService.insertAccount(account.toAccount()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAccount(@PathVariable("id") String id, @Valid @RequestBody UpdateAccountRequest account) {
        return ResponseEntity.ok(accountService.updateAccount(account.toAccount(accountService.findAccountOrFail(id))));
    }

    @PostMapping("/{id}/event")
    public ResponseEntity<?> insertEvent(@PathVariable("id") String accountId, @Valid @RequestBody CreateEventRequest eventRequest) {
        return ResponseEntity.ok(accountService.addEventAndUpdateStatistics(accountId, eventRequest.toEvent(accountId)));
    }

    @GetMapping("/{id}/event")
    public ResponseEntity<?> getEventsForAccount(@PathVariable("id") String accountId) {
        return ResponseEntity.ok(accountService.getEventsForAccount(accountId));
    }

    @GetMapping("/{id}/statistic")
    public ResponseEntity<?> getStatisticsForAccount(@PathVariable("id") String accountId) {
        return ResponseEntity.ok(accountService.findAccountOrFail(accountId).getStatistics());
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Data
    public static class CreateAccountRequest {
        @NotBlank
        private String name;

        Account toAccount() {
            return new Account(name);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Data
    public static class UpdateAccountRequest {
        @NotBlank
        private String name;

        Account toAccount(Account original) {
            original.updateName(name);
            return original;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @Data
    public static class CreateEventRequest {
        @NotBlank
        private String type;

        Event toEvent(String accountId) {
            return new Event(accountId, type);
        }
    }
}
