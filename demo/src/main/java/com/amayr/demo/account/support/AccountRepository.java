package com.amayr.demo.account.support;

import com.amayr.demo.account.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

interface AccountRepository extends MongoRepository<Account, String> {
}
