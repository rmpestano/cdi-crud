package com.cdi.crud.persistence;

import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@RequestScoped
public class TenantController {

	@PersistenceContext(unitName="CarPU")
	EntityManager carEm;
	
	@PersistenceContext(unitName="MoviePU")
	EntityManager movieEm;

	
	public EntityManager getTenant(TenantType type){
		switch (type) {
		case CAR:
			return carEm;
		case MOVIE:
			return movieEm;
			default:{
				Logger.getLogger(getClass().getCanonicalName()).info("no tenant provided, resolving to CarPU");
				return carEm;//default datasource
			}
		}
	}

}
