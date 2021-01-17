package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CommonController {

    @Autowired private CommonService commonService;

    /**
     * RestController method called when the request pattern is of type '/userprofile/{userId}'
     * and the incoming request is of 'GET' type
     * Retrieve user details based on the uuid provided
     *
     * @param userUuid                          - String representing user uuid
     * @param authorization                     - String represents authorization token
     * @return                                  - ResponseEntity (UserDetailsResponse along with HTTP status code)
     * @throws AuthorizationFailedException     - if user has not signed in or already signed out
     * @throws UserNotFoundException            - if user profile does not exist in the database
     */
    @GetMapping(
            path = "/userprofile/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> userProfile(
            @PathVariable("userId") final String userUuid,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {

        UserEntity existingUser = commonService.getUserByUuid(userUuid, authorization);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse();
        userDetailsResponse
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .userName(existingUser.getUserName())
                .emailAddress(existingUser.getEmail())
                .country(existingUser.getCountry())
                .aboutMe(existingUser.getAboutMe())
                .dob(existingUser.getDob())
                .contactNumber(existingUser.getContactNumber());
        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK);
    }

}