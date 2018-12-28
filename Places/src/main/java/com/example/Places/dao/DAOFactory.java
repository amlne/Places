package com.example.Places.dao;

public class DAOFactory {
    private static ElasticSearchDAO esDAO = new ElasticSearchDAO();

    public static ElasticSearchDAO getElasticSearchDAO() {
        return esDAO;
    }

    public static AccountDAO getAccountDAO() {
        return new AccountDAO();
    }

    public static PlaceDAO getPlaceDAO() {
        return new PlaceDAO();
    }

    public static MapDAO getMapDAO() {
        return new MapDAO();
    }

    public static ImageDAO getImageDAO() {
        return new ImageDAO();
    }
}
