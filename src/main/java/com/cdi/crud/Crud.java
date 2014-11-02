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
 *
 * Make sure you have a transcation before calling Insert/update or remove.
 * I did not make it transactional to be compatible with JavaEE6
 * Also cannot be an EJB itself cause EJBs may be shared between clients and entityClass
 * may be lost.
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
                throw new IllegalArgumentException("provide entity class at injection point eg: @Inject Crud<Entity> crud");
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

    public Crud projection(Projection projection) {
        getCriteria().setProjection(projection);
        return this;
    }

    //nullsafe restrictions
    public Crud<T> eq(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.eq(property, value));
        } else{
            log.warning("ignoring eq restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> ne(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.ne(property, value));
        }else{
            log.warning("ignoring ne restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> not(Criterion criterion) {
        if (criterion != null) {
            getCriteria().add(Restrictions.not(criterion));
        }
        return this;
    }

    public Crud<T> ilike(String property, String value, MatchMode matchMode) {
        if (value != null) {
            getCriteria().add(Restrictions.ilike(property, value.toString(), matchMode));
        }else{
           log.warning("ignoring ilike restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> ilike(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.ilike(property, value));
        } else{
            log.warning("ignoring ilike restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> like(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.like(property, value));
        } else{
            log.warning("ignoring like restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> like(String property, String value, MatchMode matchMode) {
        if (value != null) {
            getCriteria().add(Restrictions.like(property, value, matchMode));
        } else{
            log.warning("ignoring like restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> ge(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.ge(property, value));
        }  else{
            log.warning("ignoring ge restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> le(String property, Object value) {
        if (value != null) {
            getCriteria().add(Restrictions.le(property, value));
        }else{
            log.warning("ignoring le restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> between(String property, Calendar dtIni, Calendar dtEnd) {
        if (dtIni != null && dtEnd != null) {
            dtIni.set(Calendar.HOUR, 0);
            dtIni.set(Calendar.MINUTE, 0);
            dtIni.set(Calendar.SECOND, 0);
            dtEnd.set(Calendar.HOUR, 23);
            dtEnd.set(Calendar.MINUTE, 59);
            dtEnd.set(Calendar.SECOND, 59);
            getCriteria().add(Restrictions.between(property, dtIni, dtEnd));
        }  else{
            log.warning("ignoring between restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> between(String property, Integer ini, Integer end) {
        if (ini != null && end != null) {
            getCriteria().add(Restrictions.between(property, ini, end));
        }  else{
            log.warning("ignoring between restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> between(String property, Double ini, Double end) {
        if (ini != null && end != null) {
            getCriteria().add(Restrictions.between(property, ini, end));
        }  else{
            log.warning("ignoring between restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> in(String property, List<?> list) {
        if (Assert.hasElements(list)) {
            getCriteria().add(Restrictions.in(property, list));
        }  else{
            log.warning("ignoring in restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> or(Criterion... criterions) {
        if (criterions != null) {
            getCriteria().add(Restrictions.or(criterions));
        } else{
            log.warning("ignoring or restriction due to null criterion");
        }
        return this;
    }

    public Crud<T> or(Criterion lhs, Criterion rhs) {
        if (lhs != null && rhs != null) {
            getCriteria().add(Restrictions.or(lhs, rhs));
        } else{
            log.warning("ignoring or restriction due to null criterion");
        }
        return this;
    }

    public Crud<T> and(Criterion... criterios) {
        if (criterios != null) {
            getCriteria().add(Restrictions.and(criterios));
        }  else{
            log.warning("ignoring and restriction due to null criterion");
        }
        return this;
    }

    public Crud<T> and(Criterion lhs, Criterion rhs) {
        if (lhs != null && rhs != null) {
            getCriteria().add(Restrictions.and(lhs, rhs));
        }  else{
            log.warning("ignoring and restriction due to null value on criterion");
        }
        return this;
    }

    public Crud<T> join(String property, String alias) {
        getCriteria().createAlias(property, alias);
        return this;
    }

    public Crud<T> join(String property, String alias, JoinType type) {
        getCriteria().createAlias(property, alias, type);
        return this;
    }

    public Crud<T> addCriterion(Criterion criterion) {
        if (criterion != null) {
            getCriteria().add(criterion);
        } else{
            log.warning("ignoring addCriterion due to null criterion");
        }
        return this;
    }

    public Crud<T> isNull(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isNull(property));
        }  else{
            log.warning("ignoring isnull restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> isNotNull(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isNotNull(property));
        } else{
            log.warning("ignoring isNotNull restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> isEmpty(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isEmpty(property));
        } else{
            log.warning("ignoring isEmpty restriction due to null value on property "+property);
        }
        return this;
    }

    public Crud<T> isNotEmpty(String property) {
        if (property != null) {
            getCriteria().add(Restrictions.isNotEmpty(property));
        } else{
            log.warning("ignoring isNotEmpty restriction due to null value on property "+property);
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
        Long result = (Long) getCriteria().setProjection(Projections.count(getSession()
                .getSessionFactory().getClassMetadata(getEntityClass())
                .getIdentifierPropertyName())).uniqueResult();
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

    public List<T> findWithNamedQuery(String namedQueryName) {
        return this.entityManager.createNamedQuery(namedQueryName).getResultList();
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public Crud<T> initCriteria() {
        criteria = getSession().createCriteria(getEntityClass());
        return this;
    }

    public Crud<T> addOrderAsc(String property) {
        getCriteria().addOrder(Order.asc(property));
        return this;
    }

    public Crud<T> addOrderDesc(String property) {
        getCriteria().addOrder(Order.desc(property));
        return this;
    }
}
