package com.cdi.crud.rest

import com.cdi.crud.model.Car
import com.cdi.crud.service.CarService
import javax.inject.Inject as In
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by rmpestano on 7/22/16.
 */
@Path("cars")
@Produces(MediaType.APPLICATION_JSON)
open class CarRest {

    @In
    lateinit var carService : CarService


    /**
     * @description finds a car based on its ID
     * @responseType com.cdi.crud.model.Car
     * @param id car ID
     * @status 404 car not found
     * @status 304 not modified
     * @status 200 car found successfully
     */
    @GET
    @Path("/{id:[0-9][0-9]*}")
    fun get(@PathParam("id") id: Long): Response {
        return Response.ok().build()
    }

}