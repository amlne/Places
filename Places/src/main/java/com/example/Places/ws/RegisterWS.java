package com.example.Places.ws;

import com.example.Places.dao.DAOFactory;
import com.example.Places.res.AccountResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/register")
public class RegisterWS {

    private AccountResource value;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/add")
    public Response addAccount(AccountResource account) {
        if (DAOFactory.getAccountDAO().create(account) != null)
            return Response.status(Response.Status.CREATED).entity(null).build();
        else
            return Response.status(404).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/checkUsername/{username}")
    public Response checkUsernameExist(@PathParam("username") String username) {
        if (DAOFactory.getAccountDAO().sameUsername(username))
            return Response.status(Response.Status.NO_CONTENT).entity(null).build();
        return Response.status(404).entity(null).build();
    }
}
