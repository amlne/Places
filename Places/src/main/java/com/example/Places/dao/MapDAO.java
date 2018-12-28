package com.example.Places.dao;

import com.example.Places.res.MapResource;
import com.example.Places.res.PlaceResource;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapDAO extends DAO<MapResource> {

    public MapDAO() {

    }

    @Override
    public MapResource create(MapResource obj) {
        try {
            DAOFactory.getElasticSearchDAO().getClient().execute(new Index.Builder(obj).index("map")
                    .type("GL").build());
        } catch (IOException e) {
            return null;
        }
        return obj;
    }

    @Override
    public String delete(String id) {
        try {
            DAOFactory.getElasticSearchDAO().getClient().execute(new Delete.Builder(id)
                    .index("map")
                    .type("GL")
                    .build());
        } catch (IOException e) {
            return null;
        }

        List<PlaceResource> myPlaces = DAOFactory.getPlaceDAO().getPlacesByMapId(id);
        if (myPlaces.size() != 0){
            for (PlaceResource placeResource : myPlaces){
                DAOFactory.getPlaceDAO().delete(placeResource.getPlaceId());
            }
        }
        return id;
    }

    @Override
    public MapResource update(MapResource obj, String idObject) {
        try {
            DAOFactory.getElasticSearchDAO().getClient().execute(new Index.Builder(obj).index("map")
                    .type("GL").id(idObject).build());
        } catch (IOException e) {
            return null;
        }
        return obj;
    }

    @Override
    public MapResource find(String id) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", id));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("map")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<SearchResult.Hit<MapResource, Void>> hits = result.getHits(MapResource.class);

            if (hits.size() != 0)
                return hits.get(0).source;
        } catch (Exception ignored) {

        }

        return null;
    }

    public List<MapResource> getMyMapsByUsername(String username) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("owner", username));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("map")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index = 0;
        ArrayList<MapResource> mapArrayList = new ArrayList<>();

        try {
            List<SearchResult.Hit<MapResource, Void>> hits = result.getHits(MapResource.class);

            if (hits.size() == 0)
                return null;

            for (SearchResult.Hit<MapResource, Void> hit : hits) {
                mapArrayList.add(hit.source);
                mapArrayList.get(index).setMapId(hit.id);
                index++;
            }
        } catch (Exception ignored) {

        }

        return mapArrayList;
    }


    public List<MapResource> getMaps() {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("map")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<MapResource> mapArrayList = new ArrayList<>();

        try {
            List<SearchResult.Hit<MapResource, Void>> hits = result.getHits(MapResource.class);

            for (SearchResult.Hit<MapResource, Void> hit : hits) {
                mapArrayList.add(hit.source);
            }
        } catch (Exception e) {

        }

        return mapArrayList;
    }


    public List<MapResource> mapsOfSomeoneByStatut(String username, String me, int statut) {
        boolean acces = false;

        if (statut == 2) {
            acces = DAOFactory.getAccountDAO().jeSuisAutoriser(username, me);
        } else {
            acces = true;
        }

        if (acces) {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchQuery("owner", username));
            searchSourceBuilder.size(1000);

            Search search = new Search.Builder(searchSourceBuilder.toString())
                    .addIndex("map")
                    .addType("GL")
                    .build();

            SearchResult result = null;
            try {
                result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int index = 0;
            ArrayList<MapResource> mapArrayList = new ArrayList<>();

            try {
                List<SearchResult.Hit<MapResource, Void>> hits = result.getHits(MapResource.class);

                for (SearchResult.Hit<MapResource, Void> hit : hits) {
                    if (statut == 2){
                        if (hit.source.getStatus() != 1) {
                            mapArrayList.add(hit.source);
                            mapArrayList.get(index).setMapId(hit.id);
                            index++;
                        }
                    }else{
                        if (hit.source.getStatus() == statut) {
                            mapArrayList.add(hit.source);
                            mapArrayList.get(index).setMapId(hit.id);
                            index++;
                        }
                    }

                }
            } catch (Exception e) {

            }
            return mapArrayList;

        } else {
            return null;
        }
    }


    public List<MapResource> sharedAndPublicMapsOfMyFriends(String username) {

        String idUser = DAOFactory.getAccountDAO().getId(username);

        List<String> listFirendUser = DAOFactory.getAccountDAO().getMySubscriptionsUsernameAsList(idUser);

        if (listFirendUser != null) {
            int index = 0;
            ArrayList<MapResource> mapArrayList = new ArrayList<>();

            for (String friend : listFirendUser) {
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
                searchSourceBuilder.query(QueryBuilders.matchQuery("owner", friend));
                searchSourceBuilder.size(1000);

                Search search = new Search.Builder(searchSourceBuilder.toString())
                        .addIndex("map")
                        .addType("GL")
                        .build();

                SearchResult result = null;
                try {
                    result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    List<SearchResult.Hit<MapResource, Void>> hits = result.getHits(MapResource.class);

                    for (SearchResult.Hit<MapResource, Void> hit : hits) {
                        if (hit.source.getStatus() != 1) {
                            mapArrayList.add(hit.source);
                            mapArrayList.get(index).setMapId(hit.id);
                            index++;
                        }
                    }
                } catch (Exception e) {

                }
            }

            return mapArrayList;

        } else {
            return null;
        }

    }

}
