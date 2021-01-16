package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

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

    /**
     * Method to generate JWT auth token if the credentials entered are correct
     * and persist the user auth details in the database through repository
     *
     * @param username                          - String representing username
     * @param password                          - String representing password
     * @return                                  - UserAuthEntity object
     * @throws AuthenticationFailedException    - if incorrect credentials are provided during signin
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(final String username, final String password) throws AuthenticationFailedException {

        // If user does not exists with the provided username, throw exception
        UserEntity userEntity = userDao.getUserByUsername(username);
        if (userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        // If the password provided is incorrect, throw exception
        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if (!encryptedPassword.equals(userEntity.getPassword())) {
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

        // Generate JWT auth token
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);

        UserAuthEntity userAuth = new UserAuthEntity();
        userAuth.setUuid(UUID.randomUUID().toString());
        userAuth.setUser(userEntity);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        userAuth.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
        userAuth.setExpiresAt(expiresAt);
        userAuth.setLoginAt(now);

        userDao.createUserAuth(userAuth);

        return userAuth;
    }

    /**
     * Method to update UserAuthEntity logout time if valid authorization code is provided
     *
     * @param authorizationToken            - String represents authorization token
     * @return                              - UserAuthEntity object
     * @throws SignOutRestrictedException   - if valid authorization token is not provided
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity updateUserAuth(final String authorizationToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
        final ZonedDateTime now = ZonedDateTime.now();
        userAuthEntity.setLogoutAt(now);
        userDao.updateUserAuth(userAuthEntity);
        return userAuthEntity;
    }
}
