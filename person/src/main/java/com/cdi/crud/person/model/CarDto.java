package com.cdi.crud.person.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by rmpestano on 12/27/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarDto {
    private String model;
    private String name;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
