package com.amayr.demo.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "event")
public class Event {
    @Id
    private String id;
    @JsonIgnore
    private String accountId;
    @Indexed(expireAfter = "P30D")
    private LocalDateTime happenedAt;
    private String type;

    public Event(String accountId, String type) {
        Assert.isTrue(!StringUtils.isBlank(accountId), "AccountId must not be empty!");
        Assert.isTrue(!StringUtils.isBlank(type), "Type must not be empty!");

        this.accountId = accountId;
        this.happenedAt = LocalDateTime.now();
        this.type = type;
    }
}
