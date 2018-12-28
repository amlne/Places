package com.example.Places.dao;

import com.example.Places.res.AccountResource;
import io.searchbox.core.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccountDAO extends DAO<AccountResource> {

    public AccountDAO() {

    }

    @Override
    public AccountResource create(AccountResource object) {
        if (sameUsername(object.getUsername()))
            return null;

        DocumentResult documentResult = null;
        try {
            documentResult = DAOFactory.getElasticSearchDAO().getClient().execute(new Index.Builder(object)
                    .index("user")
                    .type("GL").build());
        } catch (IOException e) {
            return null;
        }

        object.setEsId(documentResult.getId());

        update(object, documentResult.getId());

        return object;
    }

    @Override
    public String delete(String username) {
        try {
            DAOFactory.getElasticSearchDAO().getClient().execute(new Delete.Builder(getId(username))
                    .index("user")
                    .type("GL")
                    .build());
        } catch (IOException e) {
            return null;
        }
        return username;
    }

    @Override
    public AccountResource update(AccountResource obj, String idObject) {
        try {
            DAOFactory.getElasticSearchDAO().getClient().execute(new Index.Builder(obj)
                    .index("user")
                    .type("GL").id(idObject).build());
        } catch (IOException e) {
            return null;
        }
        return obj;
    }

    @Override
    public AccountResource find(String user) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("username", user));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            if (hits.size() != 0)
                return hits.get(0).source;
        } catch (Exception ignored) {

        }

        return null;
    }

    public boolean authentificate(String u, String p) {

        String query = "{\n" +
                "    \"query\": {\n" +
                "        \"query_string\" : {\n" +
                "            \"query\" : \"username:" + u + " AND password:" + p + "\"\n" +
                "        }\n" +
                "    }\n" +
                "}";

        Search search = new Search.Builder(query)
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AccountResource acc = null;
        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            for (SearchResult.Hit<AccountResource, Void> hit : hits) {
                acc = hit.source;
            }
            if (acc != null)
                return true;
        } catch (Exception ignored) {

        }
        return false;
    }


    public List<AccountResource> getMyFollowers(String userId) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", userId));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<AccountResource> accountList = new ArrayList<>();
        String myFriendreq = "";
        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            if (hits.size() != 0 && hits.get(0).source.getFollowers() != null)
                myFriendreq = hits.get(0).source.getFollowers();

        } catch (Exception e) {

        }
        if (myFriendreq.isEmpty()) return null;
        List<String> myList = new ArrayList<String>(Arrays.asList(myFriendreq.split(",")));
        for (String ss : myList) {
            accountList.add(findById(ss));
        }

        return accountList;

    }


    public List<String> getFollowersAsList(String userId) {//les gens qui mont autoriser

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", userId));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String myFriendreq = "";
        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            if (hits.size() != 0 && hits.get(0).source.getFollowers() != null){
                    myFriendreq = hits.get(0).source.getFollowers();
            }

        } catch (Exception e) {

        }
        if (myFriendreq.isEmpty()) return null;
        List<String> myList = new ArrayList<String>(Arrays.asList(myFriendreq.split(",")));

        return myList;
    }

    public List<String> getMySubscriptionsAsList(String userId) {// les gens a qui j'ai autoriser

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", userId));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String myFriendreq = "";
        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);
            if (hits.size() != 0 && hits.get(0).source.getSubscriptions() != null){
                    myFriendreq = hits.get(0).source.getSubscriptions();
            }

        } catch (Exception e) {

        }
        if (myFriendreq.isEmpty()) {
            return null;
        }
        List<String> myList = new ArrayList<String>(Arrays.asList(myFriendreq.split(",")));
        return myList;
    }


    public List<AccountResource> getMySubscriptions(String userId) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", userId));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<AccountResource> accountList = new ArrayList<>();
        String myFriend = "";
        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            if (hits.size() != 0 && hits.get(0).source.getSubscriptions() != null)
                myFriend = hits.get(0).source.getSubscriptions();

        } catch (Exception e) {

        }
        if (myFriend.isEmpty()) return null;
        List<String> myList = new ArrayList<String>(Arrays.asList(myFriend.split(",")));
        for (String ss : myList) {
            accountList.add(findById(ss));
        }

        return accountList;
    }


    public AccountResource findById(String userID) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", userID));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            if (hits.size() != 0)
                return hits.get(0).source;
        } catch (Exception e) {

        }

        return null;
    }


    public String getId(String usernameField) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("username", usernameField));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            if (hits.size() != 0)
                return hits.get(0).id;
        } catch (Exception ignored) {

        }
        return null;
    }


    public boolean sameUsername(String use) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("username", use));

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            return hits.size() != 0;
        } catch (Exception ignored) {

        }

        return false;
    }


    public ArrayList<String> findByRegex(String username) {

        String query = "{\n" +
                "  \"query\" : {\n" +
                "    \"query_string\" : {\n" +
                "      \"query\" : \"" + username + "~\",\n" + //ici cest le tag recherché
                "      \"fields\" : [\"username\"]\n" + // icicest le nom de la collone
                "      \n" +
                "    }\n" +
                "  }\n" +
                "}";

        Search.Builder searchBuilder = new Search.Builder(query).addIndex("user").addType("GL");
        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(searchBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> stringList = new ArrayList<>();

        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            for (SearchResult.Hit<AccountResource, Void> hit : hits) {
                stringList.add(hit.source.getUsername());
            }

        } catch (Exception e) {

        }


        return stringList;
    }


    public boolean jeSuisAutoriser(String userA, String userB) {
        //verifier si B est autoriser par A

        String idUserA = getId(userA);
        String idUserB = getId(userB);

        List<String> listFirendUserB = getMySubscriptionsAsList(idUserB);

        if (listFirendUserB != null) {
            if (listFirendUserB.contains(idUserA)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public boolean autoriserSomeone(String userA, String userB) {

        String idUserA = getId(userA);
        String idUserB = getId(userB);

        AccountResource accountA = findById(idUserA);
        AccountResource accountB = findById(idUserB);


        List<String> liste1 = getFollowersAsList(idUserA);
        List<String> liste2 = getMySubscriptionsAsList(idUserB);

        if (liste1 == null) liste1 = new ArrayList<>();
        if (liste2 == null) liste2 = new ArrayList<>();

        liste1.add(idUserB);
        liste2.add(idUserA);

        String tmp1 = String.join(",", liste1);
        String tmp2 = String.join(",", liste2);

        accountA.setFollowers(tmp1);
        accountB.setSubscriptions(tmp2);

        update(accountA, idUserA);
        update(accountB, idUserB);

        return true;
    }


    public boolean deleteAutorisation(String userA, String userB) {

        String idUserA = getId(userA);
        String idUserB = getId(userB);

        AccountResource accountA = findById(idUserA);
        AccountResource accountB = findById(idUserB);

        List<String> liste1 = getFollowersAsList(idUserA);
        List<String> liste2 = getMySubscriptionsAsList(idUserB);

        if (liste1 != null) {
            if (liste1.contains(idUserB)) {
                liste1.remove(idUserB);
                String tmp1 = String.join(",", liste1);
                accountA.setFollowers(tmp1);
                update(accountA, idUserA);
            }
        }

        if (liste2 != null) {
            if (liste2.contains(idUserA)) {
                liste2.remove(idUserA);
                String tmp2 = String.join(",", liste2);
                accountB.setSubscriptions(tmp2);
                update(accountB, idUserB);
            }
        }

        return true;
    }



    public List<String> getMySubscriptionsUsernameAsList(String userId) {// les gens a qui j'ai autoriser

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", userId));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String myFriendreq = "";
        List<String> usernameList = new ArrayList<>();
        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);
            if (hits.size() != 0 && hits.get(0).source.getSubscriptions() != null){
                myFriendreq = hits.get(0).source.getSubscriptions();
            }

        } catch (Exception e) {

        }
        if (myFriendreq.isEmpty()) {
            return null;
        }
        List<String> myList = new ArrayList<String>(Arrays.asList(myFriendreq.split(",")));
        for (String ss : myList){
            usernameList.add(findById(ss).getUsername());
        }

        return usernameList;
    }



    public List<String> getFollowersusernameAsList(String userId) {

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("_id", userId));
        searchSourceBuilder.size(1000);

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("user")
                .addType("GL")
                .build();

        SearchResult result = null;
        try {
            result = DAOFactory.getElasticSearchDAO().getClient().execute(search);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String myFriendreq = "";
        List<String> usernameList = new ArrayList<>();
        try {
            List<SearchResult.Hit<AccountResource, Void>> hits = result.getHits(AccountResource.class);

            if (hits.size() != 0 && hits.get(0).source.getFollowers() != null){
                myFriendreq = hits.get(0).source.getFollowers();
            }

        } catch (Exception e) {

        }
        if (myFriendreq.isEmpty()) return null;
        List<String> myList = new ArrayList<String>(Arrays.asList(myFriendreq.split(",")));

        for (String ss : myList){
            usernameList.add(findById(ss).getUsername());
        }

        return usernameList;
    }


}