package com.cdi.crud.service;

import javax.ejb.Stateless;

import com.cdi.crud.model.Movie;
import com.cdi.crud.persistence.TenantType;
import com.cdi.crud.qualifier.Tenant;

/**
 * Created by rmpestano on 9/8/14.
 */
@Stateless
@Tenant(TenantType.MOVIE)
public class MovieService extends CrudService<Movie> {

	public void initDatabase() {
		if (crud().countAll() == 0) {
			for (int i = 1; i <= 10; i++) {
				Movie m = new Movie("Movie" + i);
				insert(m);
			}
		}
		
	}

    //movie specific business logic

	
}
