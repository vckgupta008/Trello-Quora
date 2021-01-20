package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.AnswerEditRequest;
import com.upgrad.quora.api.model.AnswerEditResponse;
import com.upgrad.quora.api.model.AnswerRequest;
import com.upgrad.quora.api.model.AnswerResponse;
import com.upgrad.quora.service.business.AnswerService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
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
     * @throws InvalidQuestionException     - if incorrect/ invalid question code is sent
     */

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest,
                                                       @PathVariable("questionId") final String questionUuid,
                                                       @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, InvalidQuestionException {
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAns(answerRequest.getAnswer());
        final ZonedDateTime now = ZonedDateTime.now();
        answerEntity.setDate(now);
        AnswerEntity createdAnswerEntity = answerService.createAnswer(answerEntity, authorization, questionUuid);

        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswerEntity.getUuid()).status("ANSWER CREATED");
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
     * @throws AnswerNotFoundException      - if incorrect/ invalid answer uuid is sent
     */

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest,
                                                                @PathVariable("answerId") final String answerId,
                                                                @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AnswerNotFoundException {

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(answerId);
        answerEntity.setAns(answerEditRequest.getContent());
        AnswerEntity editedAnswerEntity = answerService.editAnswerContent(answerEntity, authorization, answerId);

        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(editedAnswerEntity.getUuid())
                .status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }
}
