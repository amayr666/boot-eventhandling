package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> insert(@RequestBody Account account) {
        return ResponseEntity.ok(accountService.insert(account));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id, @RequestBody Account account) {
        if (!id.equals(account.getId())) {
            return ResponseEntity.badRequest().body(
                    String.format("Trying to update '%s' with id '%s' but object for id '%s' is given in the put body",
                            Account.class.getName(), id, account.getId()));
        }
        return ResponseEntity.ok(accountService.update(account));
    }
}
