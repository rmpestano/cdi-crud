package com.cdi.crud.service;

import com.cdi.crud.model.Movie;
import com.cdi.crud.persistence.TenantType;
import com.cdi.crud.qualifier.Tenant;

import javax.ejb.Stateless;

/**
 * Created by rmpestano on 9/8/14.
 */
@Stateless
@Tenant(TenantType.MOVIE)
public class MovieService extends CrudService<Movie> {



    //movie specific business logic

	
}
