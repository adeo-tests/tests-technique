package adeo.leroymerlin.cdp.service;


import adeo.leroymerlin.cdp.entity.Event;
import adeo.leroymerlin.cdp.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataAccessException;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        initMocks(this);
        eventService = new EventService(eventRepository);
    }

    @DisplayName("test to retrieve all Events")
    @Test
    void testRetrieveAllEvent() throws IOException {

        //GIVEN
        String jsonString = "[\n  {\n    \"title\": \"GrasPop Metal Meeting\",\n    \"imgUrl\": \"img/1000.jpeg\",\n    \"bands\": [\n      {\n        \"name\": \"Metallica\",\n        \"members\": [\n          {\n            \"name\": \"Queen Anika Walsh\"\n          },\n          {\n            \"name\": \"Queen Anika Twice\"\n          }\n        ]\n      }\n    ]\n  }\n]";
        TypeReference<List<Event>> tRef = new TypeReference<List<Event>>() {
        };
        List<Event> events = new ObjectMapper().readValue(jsonString, tRef);
        when(eventRepository.findAllBy()).thenReturn(events);

        //WHEN
        List result = eventService.getEvents();
        //THEN

        assertEquals(1, result.size());
    }


    @DisplayName("test to search Events by query and should return empty result")
    @Test
    void testSearchEventByQuery_shouldReturnEmptyResult() throws IOException {

        //GIVEN
        String jsonString = "[\n  {\n    \"title\": \"GrasPop Metal Meeting\",\n    \"imgUrl\": \"img/1000.jpeg\",\n    \"bands\": [\n      {\n        \"name\": \"Metallica\",\n        \"members\": [\n          {\n            \"name\": \"Queen Anika Walsh\"\n          },\n          {\n            \"name\": \"Queen Anika Twice\"\n          }\n        ]\n      }\n    ]\n  }\n]";
        TypeReference<List<Event>> tRef = new TypeReference<List<Event>>() {
        };
        List<Event> events = new ObjectMapper().readValue(jsonString, tRef);
        when(eventRepository.findAllBy()).thenReturn(events);

        //WHEN
        List result = eventService.getFilteredEvents("not Found");
        //THEN
        verify(eventRepository, times(1)).findAllBy();
        assertEquals(0, result.size());
    }

    @DisplayName("test to search Events by query and should return not empty result")
    @Test
    void testSearchEventByQuery_shouldReturnNonEmptyResult() throws IOException {

        //GIVEN
        String jsonString = "[\n  {\n    \"title\": \"GrasPop Metal Meeting\",\n    \"imgUrl\": \"img/1000.jpeg\",\n    \"bands\": [\n      {\n        \"name\": \"Metallica\",\n        \"members\": [\n          {\n            \"name\": \"Queen Anika Walsh\"\n          },\n          {\n            \"name\": \"Queen Anika Twice\"\n          }\n        ]\n      }\n    ]\n  }\n]";
        TypeReference<List<Event>> tRef = new TypeReference<List<Event>>() {
        };
        List<Event> events = new ObjectMapper().readValue(jsonString, tRef);
        when(eventRepository.findAllBy()).thenReturn(events);

        //WHEN
        List result = eventService.getFilteredEvents("Queen Anika Walsh");
        //THEN
        verify(eventRepository, times(1)).findAllBy();
        assertEquals(1, result.size());
    }


    @Test
    @DisplayName("test update Event with comment or nb stars")
    void testUpdateEventWithCommentOrNbStars() {
        //GIVEN
        String expectedComment = "My comment";
        Event event = new Event();
        event.setComment(expectedComment);
        event.setNbStars(1);
        when(eventRepository.findOne(any())).thenReturn(event);
        //WHEN
        Event updated = eventService.update(1L, event).get();
        //THEN
        assertEquals(expectedComment, updated.getComment());
        assertEquals(1, updated.getNbStars());

    }

    @Test
    @DisplayName("test update Event should return error")
    void testUpdateEvent_shouldReturnError_whenEventNotFound() {
        //GIVEN
        when(eventRepository.findOne(any())).thenReturn(null);
        //WHEN
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            eventService.update(1L, new Event());
        });

        //THEN
        String expectedMessage = "Event with id [1] was not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test()//(= DataAccessException.class)
    @DisplayName("test update Event should return empty")
    void testUpdateEvent_shouldReturnEmpty_whenDataNotRecognized() {
        //GIVEN
        Event givenEvent = new Event();
        givenEvent.setComment("comment");
        givenEvent.setNbStars(1);
        when(eventRepository.findOne(any())).thenReturn(givenEvent);
        when(eventRepository.save(any(Event.class))).thenThrow(new DataAccessException("can't save Event " + givenEvent) {
        });

        //WHEN
        Optional<Event> event = eventService.update(1L, givenEvent);

        //THEN
        assertEquals(false, event.isPresent());
    }
}