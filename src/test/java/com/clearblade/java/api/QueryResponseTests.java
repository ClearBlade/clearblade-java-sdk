package com.clearblade.java.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonSyntaxException;
import org.junit.jupiter.api.Test;


public class QueryResponseTests {

    @Test
    void parseJsonWithNoItemsSucceeds() {
        String rawJson = "{ \"CURRENTPAGE\": 0, \"PREVPAGEURL\": \"prev\", \"NEXTPAGEURL\": \"next\", \"TOTAL\": 0, \"DATA\": [] }";

        QueryResponse resp = QueryResponse.parseJson(rawJson);

        assertEquals(resp.getCurrentPage(), 0);
        assertEquals(resp.getPrevPageURL(), "prev");
        assertEquals(resp.getNextPageURL(), "next");
        assertEquals(resp.getTotalCount(), 0);
        assertEquals(resp.getData().size(), 0);
    }

    @Test
    void parseJsonWithOneItemSucceeds() {
        String rawJson = "{ \"CURRENTPAGE\": 0, \"NEXTPAGEURL\": \"next\", \"PREVPAGEURL\": \"prev\", \"TOTAL\": 1, \"DATA\": [ { \"a\": \"data\" } ] }";

        QueryResponse resp = QueryResponse.parseJson(rawJson);

        assertEquals(resp.getCurrentPage(), 0);
        assertEquals(resp.getPrevPageURL(), "prev");
        assertEquals(resp.getNextPageURL(), "next");
        assertEquals(resp.getTotalCount(), 1);
        assertEquals(resp.getData().size(), 1);
    }

    @Test
    void parseJsonWithTwoItemsSucceeds() {
        String rawJson = "{ \"CURRENTPAGE\": 0, \"NEXTPAGEURL\": \"next\", \"PREVPAGEURL\": \"prev\", \"TOTAL\": 2, \"DATA\": [ { \"a\": \"data\" }, { \"b\": \"data\" } ] }";

        QueryResponse resp = QueryResponse.parseJson(rawJson);

        assertEquals(resp.getCurrentPage(), 0);
        assertEquals(resp.getPrevPageURL(), "prev");
        assertEquals(resp.getNextPageURL(), "next");
        assertEquals(resp.getTotalCount(), 2);
        assertEquals(resp.getData().size(), 2);
    }

    @Test
    void parseJsonWitnBadRawJsonSyntaxFails() {
        String rawJson = "{ ";

        assertThrows(JsonSyntaxException.class, () -> {
            QueryResponse.parseJson(rawJson);
        });
    }
}
