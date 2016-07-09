package com.cdi.crud

import com.cdi.crud.model.Car
import com.github.dbunit.rules.DBUnitRule
import com.github.dbunit.rules.api.dataset.DataSet
import com.github.dbunit.rules.cdi.api.UsingDataSet
import org.apache.deltaspike.testcontrol.api.junit.CdiTestRunner
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.persistence.EntityManager

/**
 * Created by rmpestano on 6/5/16.
 */

@RunWith(CdiTestRunner::class)
open class JpaTest {

    @field:Inject
    lateinit var em:EntityManager;


    @UsingDataSet("car.yml")
    @Test
    open fun shouldListCars() {
        val cars = em.createQuery("select c from Car c").resultList
        assertThat(cars).hasSize(4)
    }



}