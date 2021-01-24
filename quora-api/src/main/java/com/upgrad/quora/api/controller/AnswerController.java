package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerService answerService;

    /**
     * RestController method called when the request pattern is of type '/question/{questionId}/answer/create'
     * and the incoming request is of 'POST' type
     * Persists answerEntity details in the database
     *
     * @param answerRequest - answer details
     * @param authorization - String represents authorization token
     * @param questionUuid  - String represents question uuid
     * @return - ResponseEntity (AnswerResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization code is sent
     *                                      or user has not signed in or already signed out
     * @throws InvalidQuestionException     - if incorrect/ invalid question uuid is sent
     */

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@RequestHeader("authorization") final String authorization,
                                                       @PathVariable("questionId") final String questionUuid,
                                                       final AnswerRequest answerRequest)
            throws AuthorizationFailedException, InvalidQuestionException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAns(answerRequest.getAnswer());
        final ZonedDateTime now = ZonedDateTime.now();
        answerEntity.setDate(now);
        AnswerEntity createdAnswerEntity = answerService.createAnswer(answerEntity, authorization, questionUuid);

        AnswerResponse answerResponse = new AnswerResponse()
                .id(createdAnswerEntity.getUuid())
                .status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }


    /**
     * RestController method called when the request pattern is of type '/answer/edit/{answerId}'
     * and the incoming request is of 'POST' type
     * Merge answerEntity details in the database
     *
     * @param answerEditRequest - answer edit details
     * @param answerId          - String represents answer uuid
     * @param authorization     - String represents authorization token
     * @return - ResponseEntity (AnswerEditResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization code is sent
     *                                      or user has not signed in or already signed out
     * @throws AnswerNotFoundException      - if incorrect/ invalid answer uuid is sent
     */

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(@RequestHeader("authorization") final String authorization,
                                                                @PathVariable("answerId") final String answerId,
                                                                final AnswerEditRequest answerEditRequest)
            throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(answerId);
        answerEntity.setAns(answerEditRequest.getContent());
        answerEntity.setDate(ZonedDateTime.now());
        AnswerEntity editedAnswerEntity = answerService.editAnswerContent(answerEntity, authorization);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse()
                .id(editedAnswerEntity.getUuid())
                .status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type '/answer/edit/{answerId}'
     * and the incoming request is of 'DELETE' type
     * Delete answerEntity details in the database
     *
     * @param answerId      - String represents answer uuid
     * @param authorization - String represents authorization token
     * @return - ResponseEntity (AnswerDeleteResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization code is sent
     *                                      or user has not signed in or already signed out
     * @throws AnswerNotFoundException      - if incorrect/ invalid answer uuid is sent
     */

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("answerId") final String answerId)
            throws AuthorizationFailedException, AnswerNotFoundException {
        answerService.deleteAnswer(authorization, answerId);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse()
                .id(answerId)
                .status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type '/answer/all/{questionId}'
     * and the incoming request is of 'GET' type
     * Get answer details of question uuid
     *
     * @param questionId    - String represents question uuid
     * @param authorization - String represents authorization token
     * @return - ResponseEntity (AnswerDetailsResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization code is sent
     * @throws InvalidQuestionException     - if incorrect/ invalid question uuid is sent
     */

    @RequestMapping(method = RequestMethod.GET, path = "/answer/all/{questionId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(
            @RequestHeader("authorization") final String authorization,
            @PathVariable("questionId") final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {

        List<AnswerEntity> allAnswers = answerService.getAllAnswersToQuestion(authorization, questionId);
        List<AnswerDetailsResponse> answerDetailsResponseList = new LinkedList<>();

        for (AnswerEntity answerEntity : allAnswers) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse()
                    .id(answerEntity.getUuid())
                    .answerContent(answerEntity.getAns())
                    .questionContent(answerEntity.getQuestion().getContent());
            answerDetailsResponseList.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList, HttpStatus.OK);

    }

}
