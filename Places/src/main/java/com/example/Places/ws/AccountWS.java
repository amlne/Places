package com.example.Places.ws;

import com.example.Places.dao.DAOFactory;
import com.example.Places.res.AccountResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/app/accounts")
public class AccountWS {

    private AccountResource value;
    private List<String> values;
    private String strValue;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{regex}/search")
    public Response search(@PathParam("regex") String regex) {
        strValue = DAOFactory.getAccountDAO().find(regex).getUsername();

        if (strValue != null) {
            return Response.status(Response.Status.OK).entity(strValue).build();
        } else
            return Response.status(404).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{regex}/searchFuzzy")
    public Response searchFuzzy(@PathParam("regex") String regex) {
        values = DAOFactory.getAccountDAO().findByRegex(regex);

        if (values != null) {
            return Response.status(Response.Status.OK).entity(values).build();
        } else
            return Response.status(404).entity(null).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/update")
    public Response updateAccount(@PathParam("username") String username, AccountResource account) {
        String id = DAOFactory.getAccountDAO().getId(username);

        if (id != null) {
            value = DAOFactory.getAccountDAO().update(account, id);
            if (value != null)
                return Response.status(Response.Status.NO_CONTENT).entity(null).build();
        }
        return Response.status(404).entity(null).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}")
    public Response deleteAccount(@PathParam("username") String username, String password) {
        value = DAOFactory.getAccountDAO().find(username);

        if (value != null) {
            if (password.equals(value.getPassword())) {
                if (DAOFactory.getAccountDAO().delete(DAOFactory.getAccountDAO().getId(username)) != null) {
                    return Response.status(Response.Status.NO_CONTENT).entity(null).build();
                }
            }
        }
        return Response.status(404).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/followersIds")
    public Response getMyFollowersId(@PathParam("username") String username) {
        String id = DAOFactory.getAccountDAO().getId(username);

        if (id != null) {
            String result = DAOFactory.getAccountDAO().findById(id).getFollowers();
            if (result != null)
                return Response.status(201).entity(result).build();
            else
                Response.status(401).entity(null).build();
        }
        return Response.status(405).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/followers")
    public Response getMyFollowers(@PathParam("username") String username) {
        String id = DAOFactory.getAccountDAO().getId(username);

        if (id != null) {
            values = DAOFactory.getAccountDAO().getFollowersusernameAsList(id);
            if (values != null)
                return Response.status(Response.Status.OK).entity(values).build();
        }
        return Response.status(401).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/subscriptionsId")
    public Response getMySubscriptionsId(@PathParam("username") String username) {
        String id = DAOFactory.getAccountDAO().getId(username);

        if (id != null) {
            strValue = DAOFactory.getAccountDAO().findById(id).getSubscriptions();
            if (strValue != null)
                return Response.status(200).entity(strValue).build();
        }
        return Response.status(404).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/subscriptions")
    public Response getMySubscriptions(@PathParam("username") String username) {
        String id = DAOFactory.getAccountDAO().getId(username);

        if (id != null) {
            values = DAOFactory.getAccountDAO().getMySubscriptionsUsernameAsList(id);
            if (values != null)
                return Response.status(201).entity(values).build();
        }
        return Response.status(401).entity(null).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/addAccess/{authorizedUser}")
    public Response addAuthorization(@PathParam("username") String username, @PathParam("authorizedUser") String authorizedUser) {
        DAOFactory.getAccountDAO().autoriserSomeone(username, authorizedUser);
        return Response.status(202).entity(null).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/removeAccess/{unauthorizedUser}")
    public Response removeAuthorization(@PathParam("username") String username, @PathParam("unauthorizedUser") String unauthorizedUser) {
        DAOFactory.getAccountDAO().deleteAutorisation(unauthorizedUser, username);
        return Response.status(202).entity(null).build();
    }
}
