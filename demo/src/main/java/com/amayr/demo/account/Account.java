package com.amayr.demo.account;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "account")
public class Account {
    @Id
    private String id;
    @Indexed(unique = true)
    private String name;
}
