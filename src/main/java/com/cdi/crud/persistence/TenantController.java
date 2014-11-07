package com.cdi.crud.persistence;

import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


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

    @PreDestroy
    public void dispose(){
        if(carEm.isOpen()){
            carEm.close();
        }
        if(movieEm.isOpen()){
            movieEm.close();
        }
    }


}
