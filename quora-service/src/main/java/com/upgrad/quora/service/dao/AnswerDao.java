package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Method to persist AnswerEntity object in the database
     *
     * @param answerEntity - AnswerEntity object to be persisted
     * @return - persisted AnswerEntity object
     */
    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     * Method to merge AnswerEntity object in the database
     *
     * @param answerEntity - AnswerEntity object to be merged
     * @return - updated AnswerEntity object
     */
    public AnswerEntity updateAnswerContent(final AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    /**
     * Retrieves  the answer present in the Database question table using uuid and return it
     *
     * @param answerUuid - AnswerEntity object to be fetched using answerUuid
     * @return answer retrieved using uuid present in the answer table
     */
    public AnswerEntity getAnswerByUuid(final String answerUuid) {
        try {
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class)
                    .setParameter("uuid", answerUuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Remove  the answer present in the Database question table using uuid
     *
     * @param answerEntity - AnswerEntity object to be removed from database
     */
    public void deleteAnswer(final AnswerEntity answerEntity) {
        entityManager.remove(answerEntity);
    }

    /**
     * Retrieves all the answer present in the Database answer table for given question uuid and returns as a list
     *
     * @return The list of AnswerEntity  present for question uuid
     */
    public List<AnswerEntity> getAllAnswersToQuestion(final String questionUuid) {
        final List<AnswerEntity> allAnswers = entityManager
                .createNamedQuery("getAllAnswersOfQuestion", AnswerEntity.class)
                .setParameter("questionUuid", questionUuid)
                .getResultList();
        return allAnswers;
    }

}
