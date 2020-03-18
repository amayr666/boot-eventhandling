package com.amayr.demo.account;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@Document(collection = "account")
public class Account {
    @Id
    private String id;
    @Indexed(unique = true)
    @NotBlank
    private String name;
}
