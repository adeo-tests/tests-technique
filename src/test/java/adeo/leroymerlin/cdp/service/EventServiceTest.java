package adeo.leroymerlin.cdp.service;


import adeo.leroymerlin.cdp.entity.Event;
import adeo.leroymerlin.cdp.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
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
        String list = "[\n  {\n    \"title\": \"GrasPop Metal Meeting\",\n    \"imgUrl\": \"img/1000.jpeg\",\n    \"bands\": [\n      {\n        \"name\": \"Metallica\",\n        \"members\": [\n          {\n            \"name\": \"Queen Anika Walsh\"\n          },\n          {\n            \"name\": \"Queen Anika Twice\"\n          }\n        ]\n      }\n    ]\n  }\n]";
        TypeReference<List<Event>> tRef = new TypeReference<List<Event>>() {
        };
        List<Event> mo = new ObjectMapper().readValue(list, tRef);
        when(eventRepository.findAllBy()).thenReturn(mo);

        //WHEN
        List result = eventService.getEvents();
        //THEN

        assertEquals(1, result.size());
    }


    @DisplayName("test to search Events by query and should return empty result")
    @Test
    void testSearchEventByQuery_shouldReturnEmptyResult() throws IOException {

        //GIVEN
        String list = "[\n  {\n    \"title\": \"GrasPop Metal Meeting\",\n    \"imgUrl\": \"img/1000.jpeg\",\n    \"bands\": [\n      {\n        \"name\": \"Metallica\",\n        \"members\": [\n          {\n            \"name\": \"Queen Anika Walsh\"\n          },\n          {\n            \"name\": \"Queen Anika Twice\"\n          }\n        ]\n      }\n    ]\n  }\n]";
        TypeReference<List<Event>> tRef = new TypeReference<List<Event>>() {
        };
        List<Event> mo = new ObjectMapper().readValue(list, tRef);
        when(eventRepository.findAllBy()).thenReturn(mo);

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
        String list = "[\n  {\n    \"title\": \"GrasPop Metal Meeting\",\n    \"imgUrl\": \"img/1000.jpeg\",\n    \"bands\": [\n      {\n        \"name\": \"Metallica\",\n        \"members\": [\n          {\n            \"name\": \"Queen Anika Walsh\"\n          },\n          {\n            \"name\": \"Queen Anika Twice\"\n          }\n        ]\n      }\n    ]\n  }\n]";
        TypeReference<List<Event>> tRef = new TypeReference<List<Event>>() {
        };
        List<Event> mo = new ObjectMapper().readValue(list, tRef);
        when(eventRepository.findAllBy()).thenReturn(mo);

        //WHEN
        List result = eventService.getFilteredEvents("Queen Anika Walsh");
        //THEN
        verify(eventRepository, times(1)).findAllBy();
        assertEquals(1, result.size());
    }

    @DisplayName("test to search events by query")
    @Disabled
    @Test
    void testRetrieveEventByBrandsMemberName() {
        fail();
    }


    @Test
    @Disabled("Disabled test update Event with comment")
    void testUpdateEventWithComment() {
        fail();
    }

    @Test
    @Disabled("Disabled test update Event with nb stars")
    void testUpdateEventWithNBStars() {
        fail();
    }
}