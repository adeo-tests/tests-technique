package adeo.leroymerlin.cdp.service;

import adeo.leroymerlin.cdp.entity.Event;
import adeo.leroymerlin.cdp.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);
    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAllBy();
    }

    public void delete(Long id) {
        eventRepository.delete(id);
    }

    public List<Event> getFilteredEvents(String query) {

        return Optional.ofNullable(eventRepository.findAllBy()).orElseGet(Collections::emptyList).stream()
                .filter(e -> e.getBands().stream()
                        .anyMatch(b -> b.getMembers().stream()
                                .anyMatch(m -> m.getName().contains(query))
                        )
                )
                .collect(Collectors.toList());
    }


    public Optional<Event> update(Long id, Event event) {
        try {
            eventRepository.save(Optional.ofNullable(eventRepository.findOne(id))
                    .map(existing -> mapper(existing, event)).orElseThrow(() -> new EntityNotFoundException(
                            "Event with id [" + id + "] was not found")));
            logger.debug("successfully changes for event with id {}", id);
            return Optional.of(event);
        } catch (DataAccessException e) {
            logger.error("failed to save changes for event with id {}, raison: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    private static Event mapper(Event oldState, Event newState) {
        oldState.setNbStars(newState.getNbStars());
        oldState.setComment(newState.getComment());
        return oldState;
    }

}
