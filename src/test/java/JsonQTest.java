import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.africapoa.fn.ds.JsonQ;
import org.junit.jupiter.api.Test;
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

        result = jsonQ.get("$.items[*].value");
        assertEquals("[10,20,30]", result.toString().replaceAll("\n\\s*",""));
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

        result = jsonQ.get("$['a-ge']");
        assertEquals(30, (Integer) result.val());
    }

    @Test
    public void testGetWithArraySlices() {
        String json = "{\"numbers\": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.numbers[:3]");
        assertEquals("[0,1,2]", result.toString().replaceAll("\n\\s*",""));

        result = jsonQ.get("$.numbers[7:]");
        assertEquals("[7,8,9]", result.toString().replaceAll("\n\\s*",""));

        result = jsonQ.get("$.numbers[::2]");
        assertEquals("[0,2,4,6,8]", result.toString().replaceAll("\n\\s*",""));
    }

    @Test
    public void testGetWithFilterExpression() {
        String json = "{\"items\": [{\"name\": \"item1\", \"value\": 10}, {\"name\": \"item2\", \"value\": 20}, {\"name\": \"item3\", \"value\": 30}]}";
        JsonQ jsonQ = JsonQ.fromJson(json);

        JsonQ result = jsonQ.get("$.items[?(@.value > 15)].name");
        assertEquals("[\"item2\",\"item3\"]", result.toString().replaceAll("\n\\s*",""));
    }
}
