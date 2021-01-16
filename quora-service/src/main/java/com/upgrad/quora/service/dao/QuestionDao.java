package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to persist QuestionEntity object in the database
     * @param questionEntity    - QuestionEntity object to be persisted
     * @return                  - QuestionEntity object
     */
    public QuestionEntity createQuestion(final QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }
}
