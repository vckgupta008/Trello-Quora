package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;


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

    /**
     * Retrieves all the questions present in the Database question table and returns as a list
     *
     * @return The list of questions present in the question table
     */
    public List<QuestionEntity> getAllQuestions() {
        final List<QuestionEntity> allQuestions = entityManager
                .createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        return allQuestions;
    }
}