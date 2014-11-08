package com.cdi.crud.test.dbunit;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/dbunit")
public class DBUnitRest {


    @GET
    @Path("create/{dataset}")
    public Response createDataset(@PathParam("dataset") String dataset) {
        try {
            DBUnitUtils.createDataset(dataset);//i feel like going in circles
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).entity("Could not create dataset.\nmessage:" + e.getMessage() + "\ncause:" + e.getCause()).build();
        }
        return Response.ok("dataset created sucessfully").build();
    }

    @GET
    @Path("delete/{dataset}")
    public Response deleteDataset(@PathParam("dataset") String dataset) {
        try {
            DBUnitUtils.deleteDataset(dataset);//i feel like going in circles
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).entity("Could not delete dataset.\nmessage:" + e.getMessage() + "\ncause:" + e.getCause()).build();
        }
        return Response.ok("dataset deleted sucessfully").build();
    }

}
