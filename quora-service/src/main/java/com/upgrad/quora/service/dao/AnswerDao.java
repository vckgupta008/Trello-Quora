package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {
    @PersistenceContext
    private EntityManager entityManager;


    /**
     * Method to persist AnswerEntity object in the database
     *
     * @param answerEntity - AnswerEntity object to be persisted
     * @return - AnswerEntity object
     */
    public AnswerEntity createAnswer(final AnswerEntity answerEntity) {
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    /**
     * Method to merge AnswerEntity object in the database
     *
     * @param answerEntity - AnswerEntity object to be merged
     * @return - AnswerEntity object
     */
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity) {
        return entityManager.merge(answerEntity);
    }

    /**
     * Retrieves  the answer present in the Database question table using uuid and return it
     * @param answerUuid - AnswerEntity object to be fetched using answerUuid
     * @return answer retrived using uuid present in the answer table
     */
    public AnswerEntity getAnswerByUuid(final String answerUuid) {
        try {
            return entityManager.createNamedQuery("getAnswerByUuid", AnswerEntity.class)
                    .setParameter("uuid", answerUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
