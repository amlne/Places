package com.example.Places.dao;

import com.example.Places.res.PlaceResource;
import io.searchbox.core.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlaceDAO extends DAO<PlaceResource> {

    public PlaceDAO() {

    }

    @Override
    public PlaceResource create(PlaceResource obj) {
        DocumentResult documentResult = null;
        try {
            documentResult = DAOFactory.getElasticSearchDAO().getClient().execute(new Index.Builder(obj).index("place")
                    .type("GL").build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        obj.setPlaceId(documentResult.getId());
        update(obj,documentResult.getId());
        return obj;
    }

    @Override
    public String delete(String id) {

        try {
            DAOFactory.getElasticSearchDAO().getClient().execute(new Delete.Builder(id)
                    .index("place")
                    .type("GL")
                    .build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return id;
    }

    @Override
    public PlaceResource update(PlaceResource obj, String idObject) {
        try {
            DAOFactory.getElasticSearchDAO().getClient().execute(new Index.Builder(obj).index("place")
                    .type("GL").id(idObject).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    protected PlaceResource find(String id) {
        return null;
    }


    public List<PlaceResource> getPlaces() {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("place")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<PlaceResource> placeArrayList = new ArrayList<>();

        try {
            List<SearchResult.Hit<PlaceResource, Void>> hits = result.getHits(PlaceResource.class);

            for (SearchResult.Hit<PlaceResource, Void> hit : hits) {
                placeArrayList.add(hit.source);
            }
        } catch (Exception e) {

        }

        return placeArrayList;
    }


    public PlaceResource findById(String placeID) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", placeID));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("place")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<SearchResult.Hit<PlaceResource, Void>> hits = result.getHits(PlaceResource.class);

            if (hits.size() != 0)
                return hits.get(0).source;

        } catch (Exception e) {

        }

        return null;
    }


    public List<PlaceResource> getPlacesByMapId(String idMap) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("mapId", idMap));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("place")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<PlaceResource> placeArrayList = new ArrayList<>();

        try {
            List<SearchResult.Hit<PlaceResource, Void>> hits = result.getHits(PlaceResource.class);

            for (SearchResult.Hit<PlaceResource, Void> hit : hits) {
                placeArrayList.add(hit.source);
            }
        } catch (Exception e) {

        }

        return placeArrayList;
    }


}
