package com.pivothy.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.pivothy.field.DataField;
import com.pivothy.source.EntityFieldMapper;

public class EntityFieldMapperTest {
	@Test
	public void testMapFields() throws NoSuchFieldException, SecurityException {
		EntityFieldMapper mapper = new EntityFieldMapper(SampleEntity.class);
		SampleEntity entity = new SampleEntity();

		List<DataField> fields = mapper.mapFields(entity);
		assertEquals(2, fields.size());

		DataField field1 = fields.stream().filter(f -> "name".equals(f.getFieldName())).findFirst().orElse(null);
		assertNotNull(field1);
		assertEquals("Name", field1.getFieldAnn().alias());
		assertEquals(1, field1.getFieldAnn().sort());

		DataField field2 = fields.stream().filter(f -> "age".equals(f.getFieldName())).findFirst().orElse(null);
		assertNotNull(field2);
		assertEquals("Age", field2.getFieldAnn().alias());
		assertEquals(2, field2.getFieldAnn().sort());
	}
}
