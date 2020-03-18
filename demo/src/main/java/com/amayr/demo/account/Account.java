package com.amayr.demo.account;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "account")
public class Account {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;

    public Account(String name) {
        updateName(name);
    }

    public void updateName(String name) {
        Assert.isTrue(!StringUtils.isBlank(name), "Name must not be emtpy!");

        if (!StringUtils.isBlank(name)) {
            this.name = name;
        }
    }
}
