/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cdi.crud.person.service;

import com.cdi.crud.person.model.Person;
import com.cdi.crud.commons.CrudService;
import com.cdi.crud.commons.exception.CustomException;
import com.cdi.crud.commons.model.Filter;
import com.cdi.crud.commons.security.Admin;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;

import javax.ejb.Stateless;

/**
 * @author rmpestano
 *         <p/>
 *         Car Business logic
 */
@Stateless
public class PersonService extends CrudService<Person> {


    @Override
    public Criteria configPagination(Filter<Person> filter) {
        if (filter.hasParam("id")) {
            crud().eq("id",
                    Long.parseLong(filter.getParam("id").toString()));
        }

        // see index.xhtml 'model' column facet name filter
        if (filter.getEntity() != null) {
            crud().ilike("car", filter.getEntity().getCar(), MatchMode.ANYWHERE);
            crud().ilike("name", filter.getEntity().getName(), MatchMode.ANYWHERE);
        }
        return crud().getCriteria();
    }

    @Override
    public void beforeInsert(Person person) {
        if (!person.hasName()) {
            throw new CustomException("Person name cannot be empty");
        }
        if (!person.hasCar()) {
            throw new CustomException("Person car cannot be empty");
        }

        if (crud().eq("name", person.getName()).ne("id", person.getId()).count() > 0) {
            throw new CustomException("Person name must be unique");
        }
    }

    @Override
    public void beforeUpdate(Person entity) {
        this.beforeInsert(entity);
    }


    @Override
    @Admin
    public void remove(Person person) {
        super.remove(person);
    }
}
