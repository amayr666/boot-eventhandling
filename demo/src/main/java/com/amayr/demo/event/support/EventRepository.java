package com.amayr.demo.event.support;

import com.amayr.demo.event.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findEventByAccountId(String accountId);
}
