package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Method to get user details for the given username from the database
     *
     * @param username  - String that represents username
     * @return          - UserEntity object if user exists, else return null
     */
    public UserEntity getUserByUsername(final String username) {
        try {
            return entityManager.createNamedQuery("userByUsername", UserEntity.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to get user details for the given email id from the database
     *
     * @param email     - String that represents email id
     * @return          - UserEntity object if user exists, else return null
     */
    public UserEntity getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", UserEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Metjod to persist user details in the database
     *
     * @param userEntity    - UserEntity object to be persisted in the database
     * @return              - UserEntity object
     */
    public UserEntity createUser(final UserEntity userEntity) {
        entityManager.persist(userEntity);
        return userEntity;
    }

    /**
     * Method to persist user auth details in the database
     *
     * @param userAuth  - UserAuthEntity object to be persisted in the database
     * @return          - UserAuthEntity object
     */
    public UserAuthEntity createUserAuth(final UserAuthEntity userAuth) {
        entityManager.persist(userAuth);
        return userAuth;
    }

    /**
     * Method to retrieve UserAuthEntity for the given access token
     * @param accessToken   - String represents the access token
     * @return              - UserAuthEntity object if present in the database, else return null
     */
    public UserAuthEntity getUserAuth(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthByAccessToken", UserAuthEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to update UserAuthEntity object in the database
     * @param userAuthEntity    - UserAuthEntity object containing updated values
     */
    public void updateUserAuth(UserAuthEntity userAuthEntity) {
        entityManager.merge(userAuthEntity);
    }

    /**
     * Method to get user details for the given user uuid from the database
     *
     * @param uuid      - String that represents user uuid
     * @return          - UserEntity object if user exists, else return null
     */
    public UserEntity getUserByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * Method to delete user profile from database
     *
     * @param user  - UserEntity object to be deleted
     */
    public void deleteUser(UserEntity user) {
        entityManager.remove(user);
    }
}
