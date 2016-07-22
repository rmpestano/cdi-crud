package com.cdi.crud.repository

import com.cdi.crud.model.Car
import org.apache.deltaspike.data.api.AbstractEntityRepository
import org.apache.deltaspike.data.api.Repository
import org.apache.deltaspike.data.api.criteria.CriteriaSupport

/**
 * Created by rmpestano on 6/10/16.
 *
 * Gather database related methods
 */
@Repository
abstract class CarRepository : AbstractEntityRepository<Car, Long>(), CriteriaSupport<Car> {

}