package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import com.amayr.demo.event.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(accountService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") String id) {
        return accountService.findById(id).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> insert(@Valid @RequestBody CreateAccountRequest account) {
        return ResponseEntity.ok(accountService.insert(account.toAccount()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody UpdateAccountRequest account) {
        return ResponseEntity.ok(accountService.update(account.toAccount(accountService.findAccountOrFail(id))));
    }

    @PostMapping("/{id}/event")
    public ResponseEntity<?> insertEvent(@PathVariable("id") String accountId, @Valid @RequestBody CreateEventRequest eventRequest) {
        return ResponseEntity.ok(accountService.addEvent(accountId, eventRequest.toEvent(accountId)));
    }

    @GetMapping("/{id}/event")
    public ResponseEntity<?> getEventsForAccount(@PathVariable("id") String accountId) {
        return ResponseEntity.ok(accountService.getEventsForAccount(accountId));
    }

    @Data
    private static class CreateAccountRequest {
        @NotBlank
        private String name;

        Account toAccount() {
            return new Account(name);
        }
    }

    @Data
    private static class UpdateAccountRequest {
        @NotBlank
        private String name;

        Account toAccount(Account original) {
            original.updateName(name);
            return original;
        }
    }

    @Data
    private static class CreateEventRequest {
        @NotBlank
        private String type;

        Event toEvent(String accountId) {
            return new Event(accountId, type);
        }
    }
}
