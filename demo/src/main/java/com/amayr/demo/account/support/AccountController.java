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
    public ResponseEntity<?> update(@PathVariable("id") String id, @Valid @RequestBody Account account) {
        if (!id.equals(account.getId())) {
            return ResponseEntity.badRequest().body(
                    String.format("Trying to update '%s' with id '%s' but object for id '%s' is given in the put body",
                            Account.class.getName(), id, account.getId()));
        }
        return ResponseEntity.ok(accountService.update(account));
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
            Account account = new Account();
            account.setName(name);
            return account;
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
