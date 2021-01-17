package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    /**
     * Method to persist QuestionEntity object in the database through repository
     *
     * @param questionEntity                - QuestionEntity object to be persisted in the database
     * @param authorizationToken            - String represents authorization token
     * @return                              - QuestionEntity object
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization code is sent
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(final QuestionEntity questionEntity, final String authorizationToken)
            throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);

        // if UserAuthEntity object does not exist for the given authorization code, throw exception
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // if the user has already logged out, throw exception
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        questionEntity.setUser(userAuthEntity.getUser());
        questionDao.createQuestion(questionEntity);
        return questionEntity;
    }

    /**
     * Method to retrieve all question posted by any user from the database
     * @param authorizationToken        -String represents authorization token
     * @return                          -List of QuestionEntity
     * @throws AuthorizationFailedException --if incorrect/ invalid authorization code is sent
     */
    public List<QuestionEntity> getAllQuestions(final String authorizationToken)throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);

        if(userAuthEntity == null){//Checking if user is not signed in.
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(userAuthEntity.getLogoutAt() != null){//Checking if user is logged out.
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }

        //Returning the list of questionEntities.
        List<QuestionEntity> questionEntities = questionDao.getAllQuestions();
        return questionEntities;
    }
}