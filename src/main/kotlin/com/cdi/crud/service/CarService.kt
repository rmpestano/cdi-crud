package com.cdi.crud.service;

import com.cdi.crud.model.Car
import com.cdi.crud.repository.CarRepository
import org.apache.deltaspike.jpa.api.transaction.Transactional
import javax.enterprise.context.Dependent
import javax.inject.Inject

/**
 * Created by rmpestano on 7/22/16.
 */
@Transactional
@Dependent
open class CarService {

    @field:Inject
    private lateinit var carRepository: CarRepository

    open fun save(car: Car) : Car{
        return carRepository.save(car)
    }
}
