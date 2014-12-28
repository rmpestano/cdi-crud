/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cdi.crud.person.model;

import com.cdi.crud.commons.model.BaseEntity;

import javax.persistence.*;

/**
 * @author rmpestano
 */
@Entity
@Table(name = "person")
public class Person implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "car")
    private String car;

    public Person() {
    }

    public Person name(String name) {
        this.name = name;
        return this;
    }

    public Person car(String car) {
        this.car = car;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Person other = (Person) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    public boolean hasCar() {
        return car != null && !"".equals(car.trim());
    }

    public boolean hasName() {
        return name != null && !"".equals(name.trim());
    }


}
