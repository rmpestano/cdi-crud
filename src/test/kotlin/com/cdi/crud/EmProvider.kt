package com.cdi.crud

import com.github.dbunit.rules.util.EntityManagerProvider
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Produces
import javax.persistence.EntityManager

/**
 * Created by rmpestano on 7/9/16.
 */

@ApplicationScoped
open class EmProvider {

   @Produces
   open fun produce() : EntityManager {
        return EntityManagerProvider.em("CrudTestDB")
    }
}