package com.pivothy.source;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.pivothy.field.DataField;
import com.pivothy.source.MapFieldMapper;

public class MapFieldMapperTest {
	@Test
    public void testMapFields() {
        MapFieldMapper mapper = new MapFieldMapper();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("field1", "value1");
        dataMap.put("field2", "value2");

        List<DataField> fields = mapper.mapFields(dataMap);
        assertEquals(2, fields.size());
        assertEquals("field1", fields.get(0).getFieldName());
        assertEquals("field2", fields.get(1).getFieldName());
    }
}
