package com.cdi.crud

import com.cdi.crud.model.Car
import com.cdi.crud.repository.CarRepository
import com.cdi.crud.service.CarService
import com.github.dbunit.rules.cdi.api.UsingDataSet
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.persistence.EntityManager

/**
 * Created by rmpestano on 6/5/16.
 */

@RunWith(CdiTestRunner::class)
open class ServiceTest {

    @field:Inject
    lateinit var carService: CarService;


    @Test
    open fun shouldInsertCar() {
        val car = Car(null,"CarTest","CarModel",2500.5,null)
        val carInserted = carService.save(car)
        assertThat(carInserted).isNotNull()
        assertThat(carInserted.id).isNotNull();

    }



}