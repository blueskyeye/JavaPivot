package com.pivothy.source;

import com.pivothy.annotation.FieldAnn;

public class SampleEntity {
	@FieldAnn(alias = "Name", sort = 1)
    private String name;

    @FieldAnn(alias = "Age", sort = 2)
    private int age;
    
    public SampleEntity() {
    	
    }
    
    public SampleEntity(String name,int age) {
    	this.name=name;
    	this.age = age;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
    
    
}
