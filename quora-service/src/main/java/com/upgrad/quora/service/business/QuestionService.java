package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
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
     * @param questionEntity     - QuestionEntity object to be persisted in the database
     * @param authorizationToken - String represents authorization token
     * @return - Persisted QuestionEntity object
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
        if (userAuthEntity.getLogoutAt() != null
                || userAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        questionEntity.setUser(userAuthEntity.getUser());
        questionDao.createQuestion(questionEntity);
        return questionEntity;
    }

    /**
     * Method to retrieve all question posted by any user from the database
     *
     * @param authorizationToken -String represents authorization token
     * @return -List of QuestionEntity
     * @throws AuthorizationFailedException --if incorrect/ invalid authorization code is sent
     */
    public List<QuestionEntity> getAllQuestions(final String authorizationToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);

        //Checking if user is not signed in.
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        //Checking if user is logged out.
        if (userAuthEntity.getLogoutAt() != null
                || userAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions");
        }

        //Returning the list of questionEntities.
        return questionDao.getAllQuestions();
    }

    /**
     * Method takes question and user entities as parameters and updates the
     * question in the database if the user is the question owner
     *
     * @param editQuestionEntity     - Edited QuestionEntity object
     * @param authorizationToken - String represents authorization token
     * @return - Updated QuestionEntity object
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out, or The user is not the owner of the question
     * @throws InvalidQuestionException     - if the question uuid does not exist in the database
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionContent(final QuestionEntity editQuestionEntity, final String authorizationToken)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null
                || userAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        }
        QuestionEntity currentQuestionEntity = questionDao.getQuestionByUuid(editQuestionEntity.getUuid());

        if (currentQuestionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if (currentQuestionEntity.getUser().getId() != userAuthEntity.getUser().getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        currentQuestionEntity.setContent(editQuestionEntity.getContent());
        currentQuestionEntity.setDate(editQuestionEntity.getDate());
        return questionDao.updateQuestionContent(currentQuestionEntity);
    }

    /**
     * Method to delete question from the database based on the question uuid
     *
     * @param questUuid - String represents question uuid
     * @param token     - String Represents token of user for valid authentication
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out, or The user is not the owner of the question or
     * @throws InvalidQuestionException     - if the question id does not exist in the database
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(final String questUuid, final String token)
            throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity userAuthEntity = userDao.getUserAuth(token);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuthEntity.getLogoutAt() != null
                || userAuthEntity.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questUuid);

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }

        if (questionEntity.getUser().getId() != userAuthEntity.getUser().getId()
                && userAuthEntity.getUser().getRole().equals("nonadmin")) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

        questionDao.deleteQuestion(questionEntity);
    }

    /**
     * Method to getAllQuestionsByUser from the database based on the User uuid
     *
     * @param userUuid           - This represents userUuid
     * @param authorizationToken - access token to authenticate user
     * @return - List of QuestionEntity
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out
     * @throws UserNotFoundException        - - if user does not exist for the given user uuid in the database
     */
    public List<QuestionEntity> getAllQuestionsByUser(final String userUuid, final String authorizationToken)
            throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuth = userDao.getUserAuth(authorizationToken);
        if (userAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        if (userAuth.getLogoutAt() != null
                || userAuth.getExpiresAt().isBefore(ZonedDateTime.now())) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get all questions posted by a specific user");
        }

        UserEntity userEntity = userDao.getUserByUuid(userUuid);
        if (userEntity == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        return questionDao.getAllQuestionsByUser(userUuid);

    }

}