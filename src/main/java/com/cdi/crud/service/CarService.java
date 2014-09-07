/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cdi.crud.service;

import com.cdi.crud.model.Car;
import com.cdi.crud.model.Filter;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.List;

/**
 *
 * @author rmpestano
 *
 * Car Business logic
 */
@Stateless
public class CarService extends CrudService<Car> {

//    @Inject
//    Crud<Car> carCrud;
    //put here specific car business here


    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<Car> listByModel(String model){
        return crud().ilike("model", model, MatchMode.ANYWHERE).list();

    }


    @Override
    public Criteria configPagination(Filter<Car> filter) {
        if(filter.getParams().get("id") != null){
            crud().criteria().eq("id",Integer.parseInt((String)filter.getParams().get("id")));
        }

        //see index.xhtml 'model' column facet name filter
        if(filter.getEntity() != null && filter.getEntity().getModel() != null && !"".equals(filter.getEntity().getModel())){
            crud().criteria().eq("model",filter.getEntity().getModel());
        }

        //see index.xhtml 'price' column facet name filter
        if(filter.getEntity() != null && filter.getEntity().getPrice() != null){
            crud().criteria().eq("price",filter.getEntity().getPrice());
        }
        return crud().getCriteria();
    }

    public List<String> getModels(String query) {
        return crud().criteria().ilike("model",query,MatchMode.ANYWHERE).projection(Projections.property("model")).list();
    }
}
