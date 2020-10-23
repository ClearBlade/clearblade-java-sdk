package com.clearblade.java.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;


public class QueryTests {

    @Test
    void fetchCallsDoneWithResponseAndDoneWithItems() throws ClearBladeException {
        Query spyQuery = spy(Query.class);
        QueryResponse mockResponse = mock(QueryResponse.class);
        DataCallback mockCallback = mock(DataCallback.class);

        doReturn(mockResponse).when(spyQuery).doFetch();

        spyQuery.fetch(mockCallback);

        verify(mockCallback, times(1)).done(mockResponse);
        verify(mockCallback, times(1)).done(mockResponse.getDataItems());
    }

    @Test
    void parseItemArrayWithNoItemsReturnsEmptyArray() {
        String rawJson = "[]";

        Item[] items = Query.parseItemArrayWith(rawJson, "", false);

        assertEquals(0, items.length);
    }

    @Test
    void parseItemArrayWithOneItemReturnsArrayWithOneItem() {
        String rawJson = "[ { \"foo\": \"bar\" } ]";

        Item[] items = Query.parseItemArrayWith(rawJson, "", false);

        assertEquals(1, items.length);
        assertEquals("bar", items[0].getString("foo"));
    }

    @Test
    void parseItemArrayWithTwoItemsReturnsArrayWithTwoItems() {
        String rawJson = "[ { \"foo\": \"bar\" }, { \"bar\": \"baz\" } ]";

        Item[] items = Query.parseItemArrayWith(rawJson, "", false);

        assertEquals(2, items.length);
        assertEquals("bar", items[0].getString("foo"));
        assertEquals("baz", items[1].getString("bar"));
    }

    @Test
    void parseItemArrayWithNonObjectOrEmptyObjectItemsSkipsThoseItems() {
        String rawJson = "[ 1, { \"foo\": \"bar\" }, \"qux\", { \"bar\": \"baz\" }, {} ]";

        Item[] items = Query.parseItemArrayWith(rawJson, "", false);

        assertEquals(2, items.length);
        assertEquals("bar", items[0].getString("foo"));
        assertEquals("baz", items[1].getString("bar"));
    }

    @Test
    void parseItemArrayWithEmptyStringFails() {
        assertThrows(NullPointerException.class, () -> {
            String rawJson = "";

            Item[] items = Query.parseItemArrayWith(rawJson, "", false);
        });
    }

}
