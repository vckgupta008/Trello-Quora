package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnswerService {
    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AnswerDao answerDao;

    /**
     * Method to persist AnswerEntity object in the database through repository
     *
     * @param answerEntity       - AnswerEntity object to be persisted in the database
     * @param authorizationToken - String represents authorization token
     * @param questionUuid       - String represents question uuid
     * @return - AnswerEntity object
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization token is sent or
     *                                      user has not signed in or already signed out
     * @throws InvalidQuestionException     - if incorrect/ invalid question uuid is sent
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(final AnswerEntity answerEntity, final String authorizationToken, final String questionUuid)
            throws AuthorizationFailedException, InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionByUuid(questionUuid);

        //if the question object does not exist, throw exception
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);

        // if UserAuthEntity object does not exist for the given authorization code, throw exception
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // if the user has already logged out, throw exception
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post a question");
        }

        answerEntity.setQuestion(questionEntity);
        answerEntity.setUser(userAuthEntity.getUser());
        AnswerEntity createdAnswerEntity = answerDao.createAnswer(answerEntity);
        return createdAnswerEntity;

    }

    /**
     * Method to persist AnswerEntity object in the database through repository
     *
     * @param answerEntity       - AnswerEntity object to be persisted in the database
     * @param authorizationToken - String represents authorization token
     * @param answerUuid         - String represents answer uuid
     * @return - AnswerEntity object
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization token is sent or
     *                                      Owner does not edit answer or the user has already logged out
     * @throws AnswerNotFoundException      - if incorrect/ invalid answer uuid is sent
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(final AnswerEntity answerEntity, final String authorizationToken, final String answerUuid)
            throws AuthorizationFailedException, AnswerNotFoundException {

        UserAuthEntity userAuthEntity = userDao.getUserAuth(authorizationToken);

        // if UserAuthEntity object does not exist for the given authorization code, throw exception
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }

        // if the user has already logged out, throw exception
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        }

        AnswerEntity existingAnswerEntity = answerDao.getAnswerByUuid(answerUuid);

        //if answer does not exist
        if (existingAnswerEntity == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }

        //if owner of the answer doesn not match with user
        if (existingAnswerEntity.getUser().getId() != userAuthEntity.getUser().getId()) {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        answerEntity.setUser(existingAnswerEntity.getUser());
        answerEntity.setQuestion(existingAnswerEntity.getQuestion());
        answerEntity.setDate(existingAnswerEntity.getDate());
        answerEntity.setId(existingAnswerEntity.getId());

        AnswerEntity editedAnswerEntity = answerDao.editAnswerContent(answerEntity);
        return editedAnswerEntity;
    }
}
