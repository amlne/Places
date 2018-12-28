package com.example.Places.dao;


import com.example.Places.res.ImageRessource;
import io.searchbox.core.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageDAO  extends DAO<ImageRessource> {

    private List<ImageDAO> images;

    public ImageDAO() {

    }

    @Override
    public ImageRessource create(ImageRessource obj){
        DocumentResult documentResult = null;
        try {
            documentResult =  DAOFactory.getElasticSearchDAO().getClient().execute(new Index.Builder(obj).index("images")
                    .type("GL").build());

            System.out.println("ajout object");
        } catch (IOException e) {
            e.printStackTrace();
        }

        obj.setIdImage(documentResult.getId());
        update(obj,documentResult.getId());
        return obj;
    }

    @Override
    public String delete(String id) {
        try {
            DAOFactory.getElasticSearchDAO().getClient().execute(new Delete.Builder(id)
                    .index("images")
                    .type("GL")
                    .build());
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("delete object");
        return id;
    }

    @Override
    public ImageRessource update(ImageRessource obj, String idObject) {
        try {
            DAOFactory.getElasticSearchDAO().getClient().execute(new Index.Builder(obj).index("images")
                    .type("GL").id(idObject).build());
            System.out.println("update object");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;

    }

    @Override
    public ImageRessource find(String id) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("idImage", id));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex("images")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<SearchResult.Hit<ImageRessource, Void>> hits = result.getHits(ImageRessource.class);
            //List<Account> accountList = result.getSourceAsObjectList(Account.class);

            for (SearchResult.Hit<ImageRessource, Void> hit : hits) {
                return hit.source;
            }
        } catch (Exception e) {

        }

        return null;
    }



    public List<ImageRessource> getImagesByPlaceId(String idplace) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("idplace", idplace));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                // multiple index or types can be added.
                .addIndex("images")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int index = 0;
        ArrayList<ImageRessource> imgArrayList = new ArrayList<>();
        System.out.println("**********result : " + result.getJsonString());

        try {
            List<SearchResult.Hit<ImageRessource, Void>> hits = result.getHits(ImageRessource.class);
            //List<Account> accountList = result.getSourceAsObjectList(Account.class);

            for (SearchResult.Hit<ImageRessource, Void> hit : hits) {
                imgArrayList.add(hit.source);
                imgArrayList.get(index).setIdImage(hit.id);
                index++;
            }
        } catch (Exception e) {

        }
        // System.out.println("**********maplist : "+mapArrayList.get(0).getName());
        return imgArrayList;
    }

}
