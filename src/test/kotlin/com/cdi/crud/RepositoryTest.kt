package com.cdi.crud

import com.cdi.crud.repository.CarRepository
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
open class RepositoryTest {

    @field:Inject
    lateinit var carRepository:CarRepository;


    @Test
    @UsingDataSet("car.yml")
    open fun shouldListCars() {
        val cars = carRepository.findAll()
        assertThat(cars).hasSize(4)
    }



}