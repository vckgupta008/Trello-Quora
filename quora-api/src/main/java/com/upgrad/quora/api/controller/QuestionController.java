package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * RestController method called when the request pattern is of type '/question/create'
     * and the incoming request is of 'POST' type
     * Persist QuestionRequest in the database
     *
     * @param authorization   - String represents authorization token
     * @param questionRequest - QuestionRequest object to be persisted in the database
     * @return - ResponseEntity (QuestionResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out@param authorization
     *                                      - String represents authorization token
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization code is sent
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") final String authorization,
                                                           final QuestionRequest questionRequest)
            throws AuthorizationFailedException {

        // Set QuestionEntity fields using QuestionRequest object
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        final ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDate(now);

        final QuestionEntity createdQuestion = questionService.createQuestion(questionEntity, authorization);

        QuestionResponse questionResponse = new QuestionResponse()
                .id(createdQuestion.getUuid())
                .status("QUESTION CREATED");

        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    /**
     * RestController method called when the request pattern is of type '/question/all'
     * and the incoming request is of 'GET' type
     * Fetch all the questions posted by any user from the database
     *
     * @param authorization -String represents authorization token
     * @return -ResponseEntity (QuestionDetailsResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out
     */

    @RequestMapping(method = RequestMethod.GET, path = "/question/all",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        List<QuestionEntity> questionEntities = questionService.getAllQuestions(authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = new ArrayList<>();//list is created to return.

        //This loop iterates through the list and the question uuid and content to the questionDetailResponse.
        //This is later added to the questionDetailsResponseList to return to the client.
        for (QuestionEntity questionEntity : questionEntities) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .id(questionEntity.getUuid())
                    .content(questionEntity.getContent());
            questionDetailsResponseList.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);

    }

    /**
     * RestController method called when the request pattern is of type '/question/edit/{questionId}'
     * and the incoming request is of 'PUT' type
     * Edit content from all the questions posted by any user from the database
     *
     * @param questionId          - Question Id from HTTP header to get update the question
     * @param authorization       - String represents authorization token
     * @param questionEditRequest - Edited question details
     * @return - ResponseEntity (QuestionEditResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out,
     *                                      or The user is not the owner of the question
     * @throws InvalidQuestionException     - if the question uuid does not exist in the database
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(@RequestHeader("authorization") final String authorization,
                                                                    @PathVariable("questionId") final String questionId,
                                                                    final QuestionRequest questionEditRequest)
            throws AuthorizationFailedException, InvalidQuestionException {

        final QuestionEntity editQuestionEntity = new QuestionEntity();
        editQuestionEntity.setUuid(questionId);
        editQuestionEntity.setContent(questionEditRequest.getContent());
        editQuestionEntity.setDate(ZonedDateTime.now());
        final QuestionEntity editedQuestion = questionService.editQuestionContent(editQuestionEntity, authorization);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse()
                .id(editedQuestion.getUuid())
                .status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }

    /**
     * This endpoint is used to delete a question that has been posted by a user. Only the owner
     * or admin of the question can delete the question.
     *
     * @param questionId    - id of the question to be edited.
     * @param authorization - access token to authenticate user
     * @return - ResponseEntity(QuestionDeleteResponse with Http status code)
     * @throws AuthorizationFailedException -   if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out, or The user is not the owner of the question
     * @throws InvalidQuestionException     - if the question id does not exist in the database
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(@RequestHeader("authorization") final String authorization,
                                                                 @PathVariable(value = "questionId") final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {
        questionService.deleteQuestion(questionId, authorization);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse()
                .id(questionId)
                .status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type "question/all/{userId}"
     * and the incoming request is of 'GET' type
     * Retrieve all the questions for the given user
     *
     * @param accessToken - access token to authenticate user
     * @param userId      - This represents userUuid
     * @return - ResponseEntity(QuestionDetailsResponse, HttpStatus.OK)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization Token is sent,
     *                                      or the user has already signed out
     * @throws UserNotFoundException        - if user does not exist for the given user uuid in the database
     */
    @RequestMapping(method = RequestMethod.GET, path = "question/all/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUser(@RequestHeader("authorization") final String accessToken,
                                                                               @PathVariable("userId") final String userId)
            throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionEntity> questions = questionService.getAllQuestionsByUser(userId, accessToken);
        List<QuestionDetailsResponse> questionDetailResponses = new ArrayList<>();
        for (QuestionEntity questionEntity : questions) {

            QuestionDetailsResponse questionDetailResponse = new QuestionDetailsResponse();
            questionDetailResponse.setId(questionEntity.getUuid());
            questionDetailResponse.setContent(questionEntity.getContent());
            questionDetailResponses.add(questionDetailResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailResponses, HttpStatus.OK);
    }

}


