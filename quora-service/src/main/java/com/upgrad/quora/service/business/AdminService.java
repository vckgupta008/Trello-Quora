package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private UserDao userDao;

    /**
     * Method to delete user profile from database for the given user
     *
     * @param uuid                          - String representing user uuid that needs to be deleted from the database
     * @param authorizationToken            - String represents authorization token
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization code is sent, or if the user is not 'admin'
     * @throws UserNotFoundException        - if user to be deleted does not exist in the database
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(final String uuid, final String authorizationToken)
            throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);
        // if UserAuthEntity object does not exist for the given authorization code, throw exception
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        // if the user has already logged out, throw exception
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out");
        }
        // if the user not admin, then he is not allowed to delete user
        if (userAuthEntity.getUser().getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Unauthorized Access, Entered user is not an admin");
        }

        UserEntity user = userDao.getUserByUuid(uuid);
        // if the user to be deleted does not exist in database, throw exception
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid to be deleted does not exist");
        }

        userDao.deleteUser(user);
    }
}
