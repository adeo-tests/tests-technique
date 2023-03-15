package adeo.leroymerlin.cdp.service;

import adeo.leroymerlin.cdp.entity.Event;
import adeo.leroymerlin.cdp.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
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
                .filter(event -> !CollectionUtils.isEmpty(event.getBands()) && event.getBands().stream()
                        .anyMatch(band -> !CollectionUtils.isEmpty(band.getMembers()) && band.getMembers().stream()
                                .anyMatch(member -> member.getName().contains(query))
                        )
                )
                .collect(Collectors.toList());
    }


    public Optional<Event> update(Long id, Event event) {
        try {
            Event newEventState = Optional.ofNullable(eventRepository.findOne(id))
                    .map(eventFounded -> eventMapper.apply(eventFounded, event)).orElseThrow(() -> new EntityNotFoundException(
                            "Event with id [" + id + "] was not found"));
            eventRepository.save(newEventState);
            logger.debug("successfully changes for event with id {}", id);
            return Optional.of(event);
        } catch (DataAccessException e) {
            logger.error("failed to save changes for event with id {}, raison: {}", id, e.getMessage());
            return Optional.empty();
        }
    }


    private BiFunction<Event, Event, Event> eventMapper = (oldState, newState) -> {
        oldState.setNbStars(newState.getNbStars());
        oldState.setComment(newState.getComment());
        return oldState;
    };



}
