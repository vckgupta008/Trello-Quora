package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class    QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * RestController method called when the request pattern is of type '/question/create'
     * and the incoming request is of 'POST' type
     * Persist QuestionRequest in the database
     *
     * @param questionRequest               - QuestionRequest object to be persisted in the databse
     * @param authorization                 - String represents authorization token
     * @return                              - ResponseEntity (QuestionResponse along with HTTP status code)
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
     * @param authorization                 -String represents authorization token
     * @return                              -ResponseEntity (QuestionDetailsResponse along with HTTP status code)
     * @throws AuthorizationFailedException -if incorrect/ invalid authorization code is sent
     */

    @RequestMapping(method = RequestMethod.GET,path = "/question/all",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization")
                                                                             final String authorization)
            throws AuthorizationFailedException {

        List<QuestionEntity> questionEntities = questionService.getAllQuestions(authorization);

        List<QuestionDetailsResponse> questionDetailsResponseList = new LinkedList<>();//list is created to return.

        //This loop iterates through the list and the question uuid and content to the questionDetailResponse.
        //This is later added to the questionDetailsResponseList to return to the client.
        for(QuestionEntity questionEntity:questionEntities){
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse()
                    .id(questionEntity.getUuid()).content(questionEntity.getContent());
            questionDetailsResponseList.add(questionDetailsResponse);
        }

        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponseList, HttpStatus.OK);

    }

}
