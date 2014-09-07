package com.cdi.crud.model;

import org.primefaces.model.SortOrder;

import java.util.Map;

/**
 * Created by rmpestano on 9/7/14.
 */
public class Filter<T extends BaseEntity> {
    private T entity;
    private int first;
    private int pageSize;
    private String sortField;
    private SortOrder sortOrder;
    private Map<String, Object> params;


    public Filter() {
    }

    public Filter(T entity) {
        this.entity = entity;
    }

    public void setFirst(int first) {
        this.first = first;
    }

    public int getFirst() {
        return first;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
