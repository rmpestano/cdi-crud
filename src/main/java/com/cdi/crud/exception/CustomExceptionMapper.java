package com.cdi.crud.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rmpestano on 12/20/14.
 */
@Provider
public class CustomExceptionMapper implements ExceptionMapper<CustomException> {


    @Override
    public Response toResponse(CustomException e) {
        Map map = new HashMap();
        map.put("message",e.getMessage());

        if(e.getMessage().equals("Access forbidden")) {//TODO create specific exception and its mapper
            return Response.status(Response.Status.FORBIDDEN).type(MediaType.APPLICATION_JSON).entity(map).build();
        }
        if(e.getMessage().equals("Access denied")) {//TODO create specific exception and its mapper
            return Response.status(Response.Status.UNAUTHORIZED).type(MediaType.APPLICATION_JSON).entity(map).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).type(MediaType.APPLICATION_JSON).entity(map).build();
    }

}
