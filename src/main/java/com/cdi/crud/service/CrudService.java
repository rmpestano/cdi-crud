package com.cdi.crud.service;

import com.cdi.crud.Crud;
import com.cdi.crud.model.BaseEntity;
import com.cdi.crud.model.Car;
import com.cdi.crud.model.Filter;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.primefaces.model.SortOrder;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;

/**
 * Created by rmpestano on 9/7/14.
 */
@Dependent
public abstract class CrudService<T extends BaseEntity> {

    @Inject
    private Crud<T> crud;


    public Crud<T> crud() {
        return crud;
    }

    public void insert(T entity) {
        if (entity == null) {
            throw new RuntimeException("Car should not be null");
        }

        if (entity.getId() != null) {
            throw new RuntimeException("Car should be transient");
        }

        crud().save(entity);
    }

    public void remove(T entity) {
        if (entity == null) {
            throw new RuntimeException("Car should not be null");
        }

        if (entity.getId() == null) {
            throw new RuntimeException("Car should not be transient");
        }
        crud().delete(entity);
    }

    public void update(T entity) {
        if (entity == null) {
            throw new RuntimeException("Emtity should not be null");
        }

        if (entity.getId() == null) {
            throw new RuntimeException("Entity should not be transient");
        }
        crud().update(entity);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public List<T> listAll() {
        return crud().listAll();
    }

    public T findByExample(T example) {
        return crud().example(example).find();
    }


    public List<T> paginate(Filter<T> filter) {
        Criteria criteria = crud().criteria(configPagination(filter)).getCriteria();
        String sortField = filter.getSortField();
        if (sortField != null) {
            if (filter.getSortOrder().equals(SortOrder.UNSORTED)) {
                filter.setSortOrder(SortOrder.ASCENDING);
            }
            if (filter.getSortOrder().equals(SortOrder.ASCENDING)) {
                criteria.addOrder(Order.asc(sortField));
            } else {
                criteria.addOrder(Order.desc(sortField));
            }
        }
        return crud().criteria(criteria).firstResult(filter.getFirst()).maxResult(filter.getPageSize()).list();
    }

    public int count(Filter<T> filter) {
        return crud().criteria(configPagination(filter)).count();
    }

    /**
     * usually overriden in concrete service
     *
     * @param filter
     * @return
     */
    public Criteria configPagination(Filter<T> filter) {
        if (filter.getEntity() != null) {
            return crud().criteria().example(filter.getEntity()).getCriteria();
        } else {
            return crud().getCriteria();
        }

    }
}
