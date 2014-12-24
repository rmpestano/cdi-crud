package com.cdi.crud.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.logging.Logger;

public class TenantController implements Serializable{

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
