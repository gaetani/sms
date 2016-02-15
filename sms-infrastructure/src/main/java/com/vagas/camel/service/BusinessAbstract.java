package com.vagas.camel.service;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class BusinessAbstract<T> {


    @PersistenceContext
    protected EntityManager entityManager;
}
