package com.cdi.crud.car.rest;

import com.cdi.crud.car.model.Car;
import com.cdi.crud.car.service.CarService;
import com.cdi.crud.commons.model.Filter;
import com.cdi.crud.commons.rest.RestSecured;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

/**
 *
 */
@Stateless
@Path("/cars")
@Produces("application/json;charset=utf-8")
public class CarEndpoint {

    @Inject
    CarService carService;

    /**
     * @description creates a new car
     * @status 400 Car model cannot be empty
     * @status 400 Car name cannot be empty
     * @status 400 Car name must be unique
     * @status 201 Car created successfully
     */
    @POST
    @Consumes("application/json")
    public Response create(Car entity) {
        carService.insert(entity);
        return Response.created(UriBuilder.fromResource(CarEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
    }

    /**
     * @description deletes a car based on its ID
     * @param user name of the user to log in
     * @param id car ID
     * @status 401 only authorized users can access this resource
     * @status 403 only authenticated users can access this resource
     * @status 404 car not found
     * @status 204 Car deleted successfully
     */
    @DELETE
    @Path("/{id:[0-9][0-9]*}")
    @RestSecured
    public Response deleteById(@HeaderParam("user") String user, @PathParam("id") Integer id) {
        Car entity = carService.findById(id);
        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        carService.remove(entity);
        return Response.noContent().build();
    }

    /**
     * @description finds a car based on its ID
     * @responseType com.cdi.crud.car.model.Car
     * @param id car ID
     * @status 404 car not found
     * @status 200 car found successfully
     */
    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response findById(@PathParam("id") Integer id) {
        Car entity;
        try {
            entity = carService.findById(id);
        } catch (NoResultException nre) {
            entity = null;
        }

        if (entity == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    /**
     * @requiredParams startPosition, maxResult, minPrice, maxPrice
     * @param startPosition initial list position
     * @param maxResult number of elements to retrieve
     * @param minPrice minimum car price
     * @param maxPrice maximum car price
     * @param model list cars with given model
     * @param name list cars with given name
     */
    @GET
    @Path("list")
    public List<Car> list(@QueryParam("start") @DefaultValue("0") Integer startPosition,
                          @QueryParam("max") @DefaultValue("10") Integer maxResult,
                          @QueryParam("model") String model,
                          @QueryParam("name") String name,
                          @QueryParam("minPrice") Double minPrice,
                          @QueryParam("maxPrice") Double maxPrice) {
        Filter<Car> filter = new Filter<>();
        Car car = new Car();
        filter.setEntity(car);
        if(model != null){
            filter.getEntity().model(model);
        }
        if(name != null){
            filter.getEntity().name(name);
        }
        if(minPrice != null){
          filter.addParam("minPrice",minPrice);
        }
       if(maxPrice != null){
         filter.addParam("maxPrice",maxPrice);

       }
       filter.setFirst(startPosition).setPageSize(maxResult);
       final List<Car> results = carService.paginate(filter);
       return results;
    }

    /**
     * @description counts number of cars
     */
    @GET
    @Path("count")
    public Response count(@QueryParam("model") String model,
                            @QueryParam("name") String name) {
        Filter<Car> filter = new Filter<>();
        Car car = new Car();
        filter.setEntity(car);
        if(model != null){
            filter.getEntity().model(model);
        }
        if(name != null){
            filter.getEntity().name(name);
        }
        return Response.ok(carService.count(filter)).build();
    }

    /**
    * @status 400 Car model cannot be empty
    * @status 400 Car name cannot be empty
    * @status 400 Car name must be unique
    * @status 400 No Car informed to be updated
    * @status 404 No Car found with the given ID
    * @status 409 id passed in parameter is different from the Car to update
    * @status 204 Car updated successfully
    */
    @PUT
    @Path("/{id:[0-9][0-9]*}")
    @Consumes("application/json")
    public Response update(@PathParam("id") Integer id,  Car entity) {
        if (entity == null) {
            return Response.status(Status.BAD_REQUEST).build();
        }
        if (!id.equals(entity.getId())) {
            return Response.status(Status.CONFLICT).entity(entity).build();
        }
        if (carService.crud().eq("id",id).count() == 0) {
            return Response.status(Status.NOT_FOUND).build();
        }
        try {
            carService.update(entity);
        } catch (OptimisticLockException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getEntity()).build();
        }

        return Response.noContent().build();
    }
}
