package adeo.leroymerlin.cdp.controller;

import adeo.leroymerlin.cdp.entity.Event;
import adeo.leroymerlin.cdp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<Event> findEvents() {
        return eventService.getEvents();
    }

    @RequestMapping(value = "/search/{query}", method = RequestMethod.GET)
    public ResponseEntity<List<Event>> findEvents(@PathVariable String query) {
        return ResponseEntity.ok(eventService.getFilteredEvents(query));
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteEvent(@PathVariable Long id) {
        eventService.delete(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateEvent(@PathVariable Long id, @RequestBody Event event) {

        Optional<Event> updated = eventService.update(id, event);
        if (!updated.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
