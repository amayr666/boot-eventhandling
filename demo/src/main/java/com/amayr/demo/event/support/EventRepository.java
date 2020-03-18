package com.amayr.demo.event.support;

import com.amayr.demo.event.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Set;

public interface EventRepository extends MongoRepository<Event, String> {
    Set<Event> findEventByAccountId(String accountId);
}
