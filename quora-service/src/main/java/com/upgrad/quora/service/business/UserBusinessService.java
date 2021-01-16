package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /**
     * Method to check if the user already exists in the database with given username
     *
     * @param username                      - String that represents username to be verified in the database
     * @return                              - UserEntity object if the user does not exists
     * @throws SignUpRestrictedException    - if the user details with given username already exists in the database
     */
    public UserEntity usernameExists(final String username) throws SignUpRestrictedException {
        UserEntity userEntity = userDao.getUserByUsername(username);
        if (userEntity != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        return userEntity;
    }

    /**
     * Method to check if the user already exists in the database with given email id
     *
     * @param email                         - String that represents email id to be verified in the database
     * @return                              - UserEntity object if the user does not exists
     * @throws SignUpRestrictedException    - if the user details with given email id already exists in the database
     */
    public UserEntity userExists(final String email) throws SignUpRestrictedException {
        UserEntity userEntity = userDao.getUserByEmail(email);
        if (userEntity != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        return userEntity;
    }

    /**
     * Method to persist user details in the database through repository
     *
     * @param userEntity    - UserEntity object containing all details the user to be persisted
     * @return              - UserEntity object
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(final UserEntity userEntity) {
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);
        return userDao.createUser(userEntity);
    }

}
