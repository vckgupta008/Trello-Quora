package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;
import java.time.ZonedDateTime;
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
     * @param questionRequest - QuestionRequest object to be persisted in the databse
     * @param authorization   - String represents authorization token
     * @return - ResponseEntity (QuestionResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization code is sent
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest,
                                                           @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        // Set QuestionEntity fields using QuestionRequest object
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setUuid(UUID.randomUUID().toString());
        questionEntity.setContent(questionRequest.getContent());
        final ZonedDateTime now = ZonedDateTime.now();
        questionEntity.setDate(now);

        final QuestionEntity createdQuestion = questionService.createQuestion(questionEntity, authorization);

        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid())
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
     * @throws AuthorizationFailedException -if incorrect/ invalid authorization code is sent
     */

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        List<QuestionEntity> questionEntities = questionService.getAllQuestions(authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = new LinkedList<>();//list is created to return.

        //This loop iterates through the list and the question uuid and content to the questionDetailResponse.
        //This is later added to the questionDetailsResponseList to return to the client.
        for (QuestionEntity questionEntity : questionEntities) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .id(questionEntity.getUuid()).content(questionEntity.getContent());
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
    public ResponseEntity<QuestionEditResponse> editQuestion(@PathVariable("questionId") final String questionId,
                                                             @RequestHeader("authorization") final String authorization,
                                                             final QuestionRequest questionEditRequest)
            throws AuthorizationFailedException, InvalidQuestionException {

        final QuestionEntity editQuestionEntity = new QuestionEntity();
        editQuestionEntity.setContent(questionEditRequest.getContent());
        editQuestionEntity.setDate(ZonedDateTime.now());
        final QuestionEntity editedQuestion = questionService.editQuestionContent(editQuestionEntity,
                questionId, authorization);

        QuestionEditResponse questionEditResponse = new QuestionEditResponse()
                .id(editedQuestion.getUuid())
                .status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }


}
