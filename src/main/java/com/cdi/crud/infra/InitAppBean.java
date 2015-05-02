package com.cdi.crud.infra;

import com.cdi.crud.model.Car;
import com.cdi.crud.service.CarService;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 * Created by pestano on 02/05/15.
 */
@Singleton
@Startup
public class InitAppBean {

    @Inject
    CarService carService;

    @PostConstruct
    public void init(){
        if (carService.crud().countAll() == 0) {
            for (int i = 1; i <= 10; i++) {
                Car c = new Car().name("name " + i).model("model " + i).price((double) (i * 100));
                carService.insert(c);
            }
        }
    }
}
