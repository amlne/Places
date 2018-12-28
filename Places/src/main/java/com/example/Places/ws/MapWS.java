package com.example.Places.ws;

import com.example.Places.dao.DAOFactory;
import com.example.Places.res.MapResource;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/app/maps")
public class MapWS {

    private List<MapResource> value;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/add")
    public Response addMap(@PathParam("username") String username, MapResource map) {
        map.setOwner(username);

        if (DAOFactory.getMapDAO().create(map) != null)
            return Response.status(Response.Status.CREATED).entity(null).build();
        else
            return Response.status(404).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}")
    public Response getMyMaps(@PathParam("username") String username) {
        value = DAOFactory.getMapDAO().getMyMapsByUsername(username);

        if (value != null)
            return Response.status(Response.Status.OK).entity(value).build();
        else
            return Response.status(404).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/sharedMapsOfMyFriends")
    public Response sharedAndPublicMapsOfMyFriends(@PathParam("username") String username) {
        value = DAOFactory.getMapDAO().sharedAndPublicMapsOfMyFriends(username);
        if (value != null)
            return Response.status(200).entity(value).build();
        return Response.status(404).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/{myAccount}/sharedMapsOfSomeone")
    public Response sharedMapsOfSomeone(@PathParam("username") String username, @PathParam("myAccount") String me) {
        value = DAOFactory.getMapDAO().mapsOfSomeoneByStatut(username, me, 2);
        if (value != null)
            return Response.status(200).entity(value).build();
        return Response.status(404).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{username}/publicMapsOfSomeone")
    public Response publicMapsOfSomeone(@PathParam("username") String username) {
        value = DAOFactory.getMapDAO().mapsOfSomeoneByStatut(username, null, 0);
        if (value != null)
            return Response.status(200).entity(value).build();
        return Response.status(404).entity(null).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{mapId}/update")
    public Response updateMap(@PathParam("mapId") String mapId, MapResource map) {
        DAOFactory.getMapDAO().update(map, mapId);
        return Response.status(202).entity(null).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{mapId}")
    public Response deleteMap(@PathParam("mapId") String mapId) {
        DAOFactory.getMapDAO().delete(mapId);
        return Response.status(202).entity(null).build();
    }
}
