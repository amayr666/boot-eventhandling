package com.amayr.demo.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Document(collection = "event")
public class Event {
    @Id
    private String id;
    @JsonIgnore
    private String accountId;
    @Indexed(expireAfter = "P30D")
    private LocalDateTime happenedAt;
    private String type;

    public Event(@NotBlank String accountId, @NotBlank String type) {
        this.accountId = accountId;
        this.happenedAt = LocalDateTime.now();
        this.type = type;
    }
}
