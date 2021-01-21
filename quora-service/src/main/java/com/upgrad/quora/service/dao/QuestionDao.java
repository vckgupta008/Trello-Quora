package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import java.util.List;


@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to persist QuestionEntity object in the database
     *
     * @param questionEntity - QuestionEntity object to be persisted
     * @return - QuestionEntity object
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

    /**
     * Retrieves  the questions present in the Database question table using uuid and return it
     *
     * @param questionUuid - QuestionEntity object to be fetched using questionUuid
     * @return question retrived using uuid present in the question table
     */
    public QuestionEntity getQuestionByUuid(final String questionUuid) {
        try {
            return entityManager.createNamedQuery("getQuestionByUuid", QuestionEntity.class)
                    .setParameter("uuid", questionUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    /**
     * This method to update existing Question in database
     *
     * @param editedQuestionEntity - Edited QuestionEntity Object
     * @return - QuestionEntity Object
     */
    public QuestionEntity editQuestionContent(final QuestionEntity editedQuestionEntity) {
        return entityManager.merge(editedQuestionEntity);
    }

}
