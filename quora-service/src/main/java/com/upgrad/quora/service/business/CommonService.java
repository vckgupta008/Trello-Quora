package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CommonService {

    @Autowired
    private UserDao userDao;

    /**
     * Method to retrieve user details based on the provided uuid
     *
     * @param userUuid           - String representing user uuid
     * @param authorizationToken - String represents authorization token
     * @return - UserEntity object
     * @throws AuthorizationFailedException - if user has not signed in or already signed out
     * @throws UserNotFoundException        - if user profile does not exist in the database
     */
    public UserEntity getUserByUuid(final String userUuid, final String authorizationToken)
            throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        // if UserAuthEntity object does not exist for the given authorization code, throw exception
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // if the user has already logged out, throw exception
        if (userAuthEntity.getLogoutAt() != null
                || userAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get user details");
        }
        // if the user does not not exist with the given uuid throw exception
        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if (userEntity == null)
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");

        return userEntity;

    }

}
