package com.example.Places.ws;

import com.example.Places.dao.DAOFactory;
import com.example.Places.res.ImageRessource;
import com.example.Places.res.PlaceResource;
import com.example.Places.stockage.GestionnaireImages;
import com.google.gson.Gson;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/app/place")
public class PlaceWS {

    private PlaceResource value;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{placeId}")
    public Response getPlaceById(@PathParam("placeId") String placeId) {
        value = DAOFactory.getPlaceDAO().findById(placeId);

        if (value != null)
            return Response.status(201).entity(value).build();
        else
            return Response.status(404).entity(null).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{mapId}/getPlacesOf")
    public List<PlaceResource> getPlacesOf(@PathParam("mapId") String mapId) {
        return DAOFactory.getPlaceDAO().getPlacesByMapId(mapId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{mapId}/add")
    public PlaceResource addPlace(@PathParam("mapId") String mapId, PlaceResource place) {
        place.setMapId(mapId);
        return DAOFactory.getPlaceDAO().create(place);
    }

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Path("/add")
    public Response addPlace2(
            @FormDataParam("placePictures") List<FormDataBodyPart> bodyParts,
            @FormDataParam("place") String place) {

        Gson gson = new Gson();
        PlaceResource placeResource = gson.fromJson(place, PlaceResource.class);
        PlaceResource placeResult = DAOFactory.getPlaceDAO().create(placeResource);

        if (placeResult != null) {
            for (int i = 0; i < bodyParts.size(); i++) {
                BodyPartEntity bodyPartEntity = (BodyPartEntity) bodyParts.get(i).getEntity();
                String fileName = bodyParts.get(i).getContentDisposition().getFileName();

                ImageRessource cmp = new ImageRessource(placeResult.getPlaceId());
                ImageRessource imageResult = DAOFactory.getImageDAO().create(cmp);

                if (imageResult != null) {
                    placeResult.setPictures(placeResult.getPictures() + ":" + imageResult.getIdImage());
                    GestionnaireImages.handlePicture(bodyPartEntity.getInputStream(), imageResult.getIdImage());
                }
            }
            DAOFactory.getPlaceDAO().update(placeResult, placeResult.getPlaceId());

            return Response.status(Response.Status.OK).entity(" image ajouté").build();
        } else
            return Response.status(404).entity(null).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/update")
    public Response updatePlace(PlaceResource place) {
        value = DAOFactory.getPlaceDAO().update(place, place.getPlaceId());

        if (value != null)
            return Response.status(201).entity(value).build();
        else
            return Response.status(404).entity(null).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/delete")
    public Response deletePlace(PlaceResource place) {
        value = DAOFactory.getPlaceDAO().update(place, place.getPlaceId());

        if (value != null)
            return Response.status(201).entity(value).build();
        else
            return Response.status(404).entity(null).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{placeId}/update")
    public PlaceResource updatePlace(@PathParam("placeId") String placeId, PlaceResource place) {
        return DAOFactory.getPlaceDAO().update(place, placeId);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{placeId}")
    public boolean deletePlace(@PathParam("placeId") String placeId) {
        DAOFactory.getPlaceDAO().delete(placeId);
        return true;
    }
}
