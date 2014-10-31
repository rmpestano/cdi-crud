package com.cdi.crud.service;

import com.cdi.crud.Crud;
import com.cdi.crud.model.BaseEntity;
import com.cdi.crud.model.Filter;
import com.cdi.crud.model.SortOrder;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Created by rmpestano on 9/7/14. A CRUD template to all services
 */
@Dependent
public abstract class CrudService<T extends BaseEntity> {

	@Inject
	private Crud<T> crud;

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Crud<T> crud() {
		return crud;
	}

	public void insert(T entity) {
		if (entity == null) {
			throw new RuntimeException("Entity cannot be null");
		}

		if (entity.getId() != null) {
			throw new RuntimeException("Entity must be transient");
		}

		crud().save(entity);
	}

	public void remove(T entity) {
		if (entity == null) {
			throw new RuntimeException("Entity cannot be null");
		}

		if (entity.getId() == null) {
			throw new RuntimeException("Entity cannot be transient");
		}
		crud().delete(entity);
	}

	public void remove(List<T> entities) {
		if (entities == null) {
			throw new RuntimeException("Entities cannot be null");
		}
		for (T t : entities) {
			this.remove(t);
		}
	}

	public void update(T entity) {
		if (entity == null) {
			throw new RuntimeException("Entity cannot be null");
		}

		if (entity.getId() == null) {
			throw new RuntimeException("Entity cannot be transient");
		}
		crud().update(entity);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<T> listAll() {
		return crud().listAll();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public T findById(Serializable id) {
		return crud().get(id);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public T findByExample(T example) {
		return crud().example(example).find();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public T findByExample(T example, MatchMode mode) {
		return crud().example(example, mode).find();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<T> paginate(Filter<T> filter) {
		crud().initCriteria();
		Criteria criteria = crud().criteria(configPagination(filter))
				.getCriteria();
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
		return crud().criteria(criteria).firstResult(filter.getFirst())
				.maxResult(filter.getPageSize()).list();
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int count(Filter<T> filter) {
		return crud().criteria(configPagination(filter)).count();
	}

	/**
	 * usually overriden in concrete service
	 * 
	 * @param filter
	 * @return
	 */
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Criteria configPagination(Filter<T> filter) {
		if (filter.getEntity() != null) {
			return crud().criteria().example(filter.getEntity()).getCriteria();
		} else {
			return crud().getCriteria();
		}

	}
}
