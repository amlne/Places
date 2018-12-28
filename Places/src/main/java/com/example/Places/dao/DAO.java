package com.example.Places.dao;

import java.io.IOException;

public abstract class DAO<T> {

    public DAO() {
    }

    protected abstract T create(T obj) throws IOException;

    protected abstract String delete(String id);

    protected abstract T update(T obj, String idObject);

    protected abstract T find(String id);
}