/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cdi.crud;


import com.cdi.crud.model.BaseEntity;
import com.cdi.crud.util.Assert;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.primefaces.model.SortOrder;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author rmpestano Helper class to crud an entity
 */
public class Crud<T extends BaseEntity> implements Serializable {

    @PersistenceContext
    private EntityManager entityManager;
    protected Class<T> entityClass;
    private Criteria criteria;
    private Session session;
    private Logger log;

    public Class<T> getEntityClass() {
        if (entityClass == null) {
            //only works if one extends BaseDao, we will take care of it with CDI
            entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        return entityClass;
    }

    @Inject
    public void Crud(InjectionPoint ip) {
        if (ip != null && ip.getType() != null) {
            try {
                ParameterizedType type = (ParameterizedType) ip.getType();
                Type[] typeArgs = type.getActualTypeArguments();
                Class<T> entityClass = (Class<T>) typeArgs[0];
                this.entityClass = entityClass;
                log = Logger.getLogger(getClass().getName());
            } catch (Exception e) {
                throw new IllegalArgumentException("provide entity class at injection point eg: @Inject BaseDao<Car> carDao");
            }

        } else {
            throw new IllegalArgumentException(
                    "Provide entity at injection point ex: @Inject Crud<Entity> crud");
        }
    }

    // buider methods
    public Crud<T> example(T entity) {
        if (entity != null) {
            getCriteria().add(Example.create(entity));
        } else {
            log.warning("cannot create example for a null entity.");
            return this;
        }
        return this;
    }

    public Crud<T> example(T entity, List<String> excludeProperties) {
        Example example = null;
        if (entity != null) {
            example = Example.create(entity);
        } else {
            log.warning("cannot create example for a null entity.");
            return this;
        }
        if (Assert.hasElements(excludeProperties)) {
            for (String exclude : excludeProperties) {
                example.excludeProperty(exclude);
            }
        }
        getCriteria().add(example);
        return this;
    }

    public Crud<T> example(T entity, MatchMode mode) {
        if (entity != null) {
            getCriteria().add(Example.create(entity).enableLike(mode));
        } else {
            log.warning("cannot create example for a null entity.");
            return this;
        }
        return this;
    }

    public Crud<T> example(T entity, MatchMode mode, List<String> excludeProperties) {
        Example example = null;
        if (entity != null) {
            example = Example.create(entity).enableLike(mode);
        } else {
            log.warning("cannot create example for a null entity.");
            return this;
        }
        if (Assert.hasElements(excludeProperties)) {
            for (String exclude : excludeProperties) {
                example.excludeProperty(exclude);
            }
        }
        getCriteria().add(example);
        return this;
    }

    public Crud<T> maxResult(Integer maxResult) {
        getCriteria().setMaxResults(maxResult);
        return this;
    }

    public Crud<T> firstResult(Integer firstResult) {
        getCriteria().setFirstResult(firstResult);
        return this;
    }

    public Crud<T> criteria(Criteria criteria) {
        this.criteria = criteria;
        return this;
    }

    public Crud<T> criteria() {
        this.criteria = getCriteria();
        return this;
    }

    public Crud<T> projection(Projection projection) {
        getCriteria().setProjection(projection);
        return this;
    }

    //nullsafe restrictions
    public Crud eq(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.eq(property, value));
        }
        return this;
    }

    public Crud ne(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.ne(property, value));
        }
        return this;
    }

    public Crud not(Criterion criterion) {
        if (criterion != null) {
            getCriteria().add(Restrictions.not(criterion));
        }
        return this;
    }

    public Crud ilike(String property, String value, MatchMode matchMode) {
        if (value != null) {
            getCriteria().add(Restrictions.ilike(property, value.toString(), matchMode));
        }
        return this;
    }

    public Crud ilike(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.ilike(property, value));
        }
        return this;
    }

    public Crud like(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.like(property, value));
        }
        return this;
    }

    public Crud like(String property, String value, MatchMode matchMode) {
        if (value != null) {
            getCriteria().add(Restrictions.like(property, value, matchMode));
        }
        return this;
    }

    public Crud ge(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.ge(property, value));
        }
        return this;
    }

    public Crud le(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.le(property, value));
        }
        return this;
    }

    public Crud between(String property, Calendar dtIni, Calendar dtEnd) {
        if (dtIni != null && dtEnd != null) {
            dtIni.set(Calendar.HOUR, 0);
            dtIni.set(Calendar.MINUTE, 0);
            dtIni.set(Calendar.SECOND, 0);
            dtEnd.set(Calendar.HOUR, 23);
            dtEnd.set(Calendar.MINUTE, 59);
            dtEnd.set(Calendar.SECOND, 59);
            getCriteria().add(Restrictions.between(property, dtIni, dtEnd));
        }
        return this;
    }

    public Crud between(String property, Integer ini, Integer end) {
        if (ini != null && end != null) {
            getCriteria().add(Restrictions.between(property, ini, end));
        }
        return this;
    }

    public Crud in(String property, List<?> list) {
        if (Assert.hasElements(list)) {
            getCriteria().add(Restrictions.in(property, list));
        }
        return this;
    }

    public Crud or(Criterion... criterions) {
        if (criterions != null) {
            getCriteria().add(Restrictions.or(criterions));
        }
        return this;
    }

    public Crud or(Criterion lhs, Criterion rhs) {
        if (lhs != null && rhs != null) {
            getCriteria().add(Restrictions.or(lhs, rhs));
        }
        return this;
    }

    public Crud and(Criterion... criterios) {
        if (criterios != null) {
            getCriteria().add(Restrictions.and(criterios));
        }
        return this;
    }

    public Crud and(Criterion lhs, Criterion rhs) {
        if (lhs != null && rhs != null) {
            getCriteria().add(Restrictions.and(lhs, rhs));
        }
        return this;
    }

    public Crud join(String property, String alias) {
        getCriteria().createAlias(property, alias);
        return this;
    }

    public Crud join(String property, String alias, JoinType type) {
        getCriteria().createAlias(property, alias, type);
        return this;
    }

    public Crud addCriterion(Criterion criterion) {
        if (criterion != null) {
            getCriteria().add(criterion);
        }
        return this;
    }

    public Crud isNull(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isNull(property));
        }
        return this;
    }

    public Crud isNotNull(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isNotNull(property));
        }
        return this;
    }

    public Crud isEmpty(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isEmpty(property));
        }
        return this;
    }

    public Crud isNotEmpty(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isNotEmpty(property));
        }
        return this;
    }

    // find
    /**
     * @return an unique entry from table represented by
     * {@link Crud#entityClass} based on
     * current crudCrud#criteria and its restrictions
     * @throws javax.persistence.NonUniqueResultException in case current
     * {@link Crud#criteria} doesn't match an
     * unique entry
     */
    public T find() {
        T result = (T) getCriteria().uniqueResult();
        resetCriteria();
        return result;
    }

    // list
    /**
     * @return all entries from the table represented by
     * {@link Crud#entityClass} based on
     * current {@link Crud#criteria} and its
     * restrictions
     */
    public List<T> list() {
        List<T> result = getCriteria().list();
        resetCriteria();
        return result;
    }

    /**
     * @return all entries from table represented by
     * {@link Crud#entityClass}
     */
    public List<T> listAll() {
        resetCriteria();
        List<T> result = getCriteria().list();
        return result;
    }

    // count
    /**
     * @return number of entries from table represented by
     * {@link Crud#entityClass} based on
     * current @link{ Crud#criteria} and its restrictions
     */
    public int count() {
        getCriteria().setProjection(Projections.count(getSession()
                .getSessionFactory().getClassMetadata(getEntityClass())
                .getIdentifierPropertyName()));
        Long result = (Long) getCriteria().uniqueResult();
        resetCriteria();
        return result.intValue();
    }

    /**
     * @return number of entries from table represented by
     * {@link Crud#entityClass}
     */
    public int countAll() {
        resetCriteria();
        int result = projection(Projections.rowCount()).firstResult(0)
                .maxResult(1).count();
        resetCriteria();
        return result;
    }

    // hibernate session shortcuts
    public T load(Serializable id) {
        return (T) this.getSession().load(getEntityClass(), id);
    }

    public T get(Serializable id) {
        return (T) this.getSession().get(getEntityClass(), id);
    }

    public void save(T entity) {
        this.getSession().save(entity);
    }

    public T merge(T entity) {
        return (T) getSession().merge(entity);
    }

    public void update(T entity) {
        this.getSession().update(entity);
    }

    public void delete(T entity) {
        this.getSession().delete(this.get((Serializable) entity.getId()));
    }

    public T refresh(T entity) {
        this.getSession().refresh(entity);
        return entity;
    }

    public void saveOrUpdate(T entity) {
        this.getSession().saveOrUpdate(entity);
    }

    private void resetCriteria() {
        criteria = null;
    }

    //getter & setters
    public Session getSession() {
        if (session == null || !session.isOpen()) {
            session = getEntityManager().unwrap(Session.class);
        }
        return session;
    }

    public Criteria getCriteria() {
        if (criteria == null) {
            criteria = getSession().createCriteria(getEntityClass());
        }
        return criteria;
    }

    public Criteria getCriteria(boolean reset) {
        Criteria copy = getCriteria();
        if (reset) {
            criteria = null;
        }
        return copy;
    }

    public List<T> findWithNamedQuery(String namedQueryName) {
        return this.entityManager.createNamedQuery(namedQueryName).getResultList();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    

}
