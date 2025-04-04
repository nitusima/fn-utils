import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.africapoa.fn.ds.JsonQ;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonQTest {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Test
    public void testGetWithSimpleJson() {
        String json = "{\"name\": \"John\", \"age\": 30}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.name");
        assertEquals("John", result.val());

        result = jsonQ.get("$.age");
        assertEquals(30, (Double) result.val());
    }

    @Test
    public void testGetWithNestedJson() {
        String json = "{\"person\": {\"name\": \"John\", \"address\": {\"city\": \"New York\", \"zip\": \"10001\"}}}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.person.name");
        assertEquals("John", result.val());

        result = jsonQ.get("$.person.address.city");
        assertEquals("New York", result.val());

        result = jsonQ.get("$.person.address.zip");
        assertEquals("10001", result.val());
    }

    @Test
    public void testGetWithArrayJson() {
        String json = "{\"people\": [{\"name\": \"John\"}, {\"name\": \"Jane\"}, {\"name\": \"Doe\"}]}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.people[0].name");
        assertEquals("John", result.val());

        result = jsonQ.get("$.people[1].name");
        assertEquals("Jane", result.val());

        result = jsonQ.get("$.people[2].name");
        assertEquals("Doe", result.val());
    }

    @Test
    public void testGetWithWildcardJson() {
        String json = "{\"items\": [{\"name\": \"item1\", \"value\": 10}, {\"name\": \"item2\", \"value\": 20}, {\"name\": \"item3\", \"value\": 30}]}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.items[*].name");
        assertEquals("[\"item1\",\"item2\",\"item3\"]", result.toString().replaceAll("\n\\s*",""));

        assertEquals("[10,20,30]", jsonQ.integers("$.items[*].value").toString().replaceAll("\n*\\s*",""));
    }

    @Test
    public void testGetWithDeeplyNestedJson() {
        String json = "{ \"a\": { \"b\": { \"c\": { \"d\": { \"e\": \"value\" } } } } }";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.a.b.c.d.e");
        assertEquals("value", result.val());
    }

    @Test
    public void testGetWithMixedContentJson() {
        String json = "{\"data\": {\"items\": [{\"id\": 1, \"name\": \"item1\"}, {\"id\": 2, \"name\": \"item2\"}]}}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.data.items[0].name");
        assertEquals("item1", result.val());

        result = jsonQ.get("$.data.items[1].name");
        assertEquals("item2", result.val());
    }

    @Test
    public void testGetWithNonExistingPath() {
        String json = "{\"name\": \"John\"}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.nonExisting");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetWithSpecialCharactersInKeys() {
        String json = "{\"na.me\": \"John\", \"a-ge\": 30}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$['na.me']");
        assertEquals("John", result.val());
        assertEquals(30,  jsonQ.asInt("$['a-ge']"));
    }

    @Test
    public void testGetWithArraySlices() {
        String json = "{\"numbers\": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        List<Integer> result = jsonQ.integers("$.numbers[:3]");
        assertEquals(List.of(0,1,2), result);

        result = jsonQ.integers("$.numbers[6:]");
        assertEquals(List.of(6,7,8,9), result);

        result = jsonQ.integers("$.numbers[0:6]");
        assertEquals(List.of(0,1,2,3,4,5), result);
    }

    @Test
    public void testGetWithFilterExpression() {
        String json = "{\"items\": [{\"name\": \"item1\", \"value\": 10}, {\"name\": \"item2\", \"value\": 20}, {\"name\": \"item3\", \"value\": 30}]}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.items[?(@.value > 15)].name");
        assertEquals("[\"item2\",\"item3\"]", result.toString().replaceAll("\n\\s*",""));
    }
    @Test
    public void testGetWithEmptyJson() {
        String json = "{}";
        JsonQ jsonQ = JsonQ.fromJson(json);
        JsonQ result = jsonQ.get("$.name");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetWithEmptyArray() {
        String json = "[]";
        JsonQ jsonQ = JsonQ.fromJson(json);
        JsonQ result = jsonQ.get("$[0]");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetWithNullValue() {
        String json = "{\"name\": null}";
        JsonQ jsonQ = JsonQ.fromJson(json);
        JsonQ result = jsonQ.get("$.name");
        assertNull(result.val());
    }

    @Test
    public void testGetWithPartiallyNonExistingPath() {
        String json = "{\"person\": {\"name\": \"John\"}}";
        JsonQ jsonQ = JsonQ.fromJson(json);
        JsonQ result = jsonQ.get("$.person.address.city");
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetWithIndexOutOfBounds() {
        String json = "{\"people\": [{\"name\": \"John\"}, {\"name\": \"Jane\"}]}";
        JsonQ jsonQ = JsonQ.fromJson(json);
        try{
            JsonQ result = jsonQ.get("$.people[2].name");
            assertTrue(result.isEmpty());
        }
        catch (Exception  e){
            assertInstanceOf(IndexOutOfBoundsException.class, e);
        }
    }

    @Test
    public void testBooleanValues() {
        String json = "{\"isActive\": true, \"isVerified\": false}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.isActive");
        assertEquals(true, result.val());

        result = jsonQ.get("$.isVerified");
        assertEquals(false, result.val());
    }

    @Test
    public void testNumericTypes() {
        String json = "{\"intVal\": 10, \"doubleVal\": 10.5}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        assertEquals(10, jsonQ.asInt("$.intVal"));
        assertEquals(10.5, (Double) jsonQ.get("$.doubleVal").val());
    }
    @Test
    public void testEscapedCharacters() {
        String json = "{\"quote\": \"He said, \\\"Hello\\\"\"}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.quote");
        assertEquals("He said, \"Hello\"", result.val());
    }
    @Test
    public void testKeysWithSpaces() {
        String json = "{\"first name\": \"Alice\", \"last-name\": \"Smith\"}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$['first name']");
        assertEquals("Alice", result.val());

        result = jsonQ.get("$['last-name']");
        assertEquals("Smith", result.val());
    }

    @Test
    public void testDeeplyNestedArrayStructure() {
        String json = "{\"groups\": [{\"members\": [{\"name\": \"Alice\"}, {\"name\": \"Bob\"}]}]}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.groups[0].members[1].name");
        assertEquals("Bob", result.val());
    }
    @Test
    public void testNegativeIndices() {
        String json = "{\"numbers\": [0, 1, 2, 3, 4]}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        // Assuming -1 refers to the last element.
        JsonQ result = jsonQ.get("$.numbers[-1]");
        assertEquals(4, result.asInt());
    }

    @Test
    public void testInvalidJsonPath() {
        String json = "{\"name\": \"John\"}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        // Depending on implementation, you may either catch an exception or expect an empty result.
        try {
            JsonQ result = jsonQ.get("$.name.ew"); // Invalid path syntax
            assertTrue(result.isEmpty());
        } catch (Exception e) {
            // Optionally, assert the type of exception if expected.
            assertInstanceOf(IllegalArgumentException.class, e);
        }
    }

    @Test
    public void testComplexFilterExpression() {
        String json = "{\"items\": ["
                + "{\"name\": \"item1\", \"value\": 10, \"active\": true},"
                + "{\"name\": \"item2\", \"value\": 20, \"active\": false},"
                + "{\"name\": \"item3\", \"value\": 30, \"active\": true}"
                + "]}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        // Example: select items that are active and have value > 15.
        JsonQ result = jsonQ.get("$.items[?(@.value > 15 && @.active == true)].name");
        assertEquals("item3", result.val());
    }


}
