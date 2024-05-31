package com.pivothy.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.pivothy.field.DataField;
import com.pivothy.service.FieldMapper;
import com.pivothy.source.DataSourceMgr;
import com.pivothy.source.MapFieldMapper;

public class DataSourceMgrTest {
	private DataSourceMgr<Map<String, Object>> mapDataSourceMgr;
    private DataSourceMgr<SampleEntity> entityDataSourceMgr;
    @Before
    public void setUp() {
        mapDataSourceMgr = new DataSourceMgr<>(Collections.emptyList());
        
        List<SampleEntity> sampleData = new ArrayList<>();
        sampleData.add(new SampleEntity("John", 30));
        sampleData.add(new SampleEntity("Jane", 25));
        entityDataSourceMgr = new DataSourceMgr<>(sampleData, SampleEntity.class);
    }

    @Test
    public void testGetDataFieldForMapDataSource() {
    	List<Map<String, Object>> mockData = MockDataSource.getMapList();
    	mapDataSourceMgr = new DataSourceMgr<>(mockData);
        // Test non-existing field (should throw an exception)
        try {
            mapDataSourceMgr.getDataField("non_existing_field");
        } catch (Exception e) {
            assertEquals("non_existing_field字段信息不存在！", e.getMessage());
        }
    }

    @Test
    public void testGetDataFieldForEntityDataSource() {
        // Test existing field
        DataField dataField = entityDataSourceMgr.getDataField("name");
        assertNotNull(dataField);
        assertEquals("name", dataField.getFieldName());

        // Test non-existing field (should throw an exception)
        try {
            entityDataSourceMgr.getDataField("non_existing_field");
        } catch (Exception e) {
            assertEquals("non_existing_field字段信息不存在！", e.getMessage());
        }
    }
	@Test
	public void testInitializationWithMapFieldMapper() {
		// Arrange: 创建一个空的数据源列表
		List<Map<String, Object>> dataSource = new ArrayList<>();

		// Act: 使用 MapFieldMapper 初始化 DataSourceMgr
		DataSourceMgr<Map<String, Object>> dataSourceMgr = new DataSourceMgr<>(dataSource);

		// Assert: 验证 FieldMapper 是否是 MapFieldMapper 类型
		FieldMapper fieldMapper = dataSourceMgr.getFieldMapper();
		assertTrue(fieldMapper instanceof MapFieldMapper);
	}

	@Test
	public void testGetMappingFields() {
		// Arrange: 创建一个包含模拟数据的数据源列表
		List<Map<String, Object>> dataSource = new ArrayList<>();
		dataSource.add(new HashMap<String, Object>() {
			{
				put("name", "John Doe");
				put("age", 30);
			}
		});
		dataSource.add(new HashMap<String, Object>() {
			{
				put("name", "Jane Smith");
				put("age", 25);
			}
		});

		// Act: 使用 MapFieldMapper 初始化 DataSourceMgr 并获取字段列表
		//MockDataSource.getMapList()
		DataSourceMgr<Map<String, Object>> dataSourceMgr = new DataSourceMgr<>(dataSource);
		List<DataField> fields = dataSourceMgr.getFieldList();
		Map<String, DataField> fieldMap = dataSourceMgr.getFieldMap();

		// Assert: 验证字段列表是否正确
		assertEquals(2, fields.size());
		assertEquals("name", fields.get(0).getFieldName());
		assertEquals("age", fields.get(1).getFieldName());
		assertEquals("name", fieldMap.get("name").getFieldName());
		assertEquals("age", fieldMap.get("age").getFieldName());
	}
	@Test
    public void testEntityFieldMapper_withSampleEntity() {
        // Arrange: 创建 SampleEntity 对象的列表作为数据源
        List<SampleEntity> dataSource = new ArrayList<>();
        dataSource.add(new SampleEntity("John Doe", 30));
        dataSource.add(new SampleEntity("Jane Smith", 25));

        // Act: 使用 SampleEntity 类初始化 DataSourceMgr 并获取字段映射器
        DataSourceMgr<SampleEntity> dataSourceMgr = new DataSourceMgr<>(dataSource, SampleEntity.class);
        FieldMapper fieldMapper = dataSourceMgr.getFieldMapper();

        // 获取字段列表
        List<DataField> fields = fieldMapper.mapFields(dataSource.get(0));

        // Assert: 验证字段列表是否正确
        assertEquals(2, fields.size());
        assertTrue(fields.stream().anyMatch(field -> "Name".equals(field.getFieldAnn().alias())));
        assertTrue(fields.stream().anyMatch(field -> "Age".equals(field.getFieldAnn().alias())));

        // 验证字段排序
        assertEquals(1, fields.stream().filter(field -> "Name".equals(field.getFieldAnn().alias())).findFirst().get().getFieldAnn().sort());
        assertEquals(2, fields.stream().filter(field -> "Age".equals(field.getFieldAnn().alias())).findFirst().get().getFieldAnn().sort());
    }
}
