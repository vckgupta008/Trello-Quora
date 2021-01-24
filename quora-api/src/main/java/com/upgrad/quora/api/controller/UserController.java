package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    /**
     * RestController method called when the request pattern is of type '/user/signup'
     * and the incoming request is of 'POST' type
     * Persists UserEntity details in the database
     *
     * @param signupUserRequest - signup user details
     * @return - ResponseEntity (SignupUserResponse along with HTTP status code)
     * @throws SignUpRestrictedException - if the username/ user details with the emailid already exists in the database
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest)
            throws SignUpRestrictedException {

        // Set UserEntity fields using SignupUserRequest object
        final UserEntity userEntity = new UserEntity();
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setRole("nonadmin");
        userEntity.setContactNumber(signupUserRequest.getContactNumber());

        final UserEntity createdUserEntity = userBusinessService.signup(userEntity);

        SignupUserResponse userResponse = new SignupUserResponse()
                .id(createdUserEntity.getUuid())
                .status("USER SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SignupUserResponse>(userResponse, HttpStatus.CREATED);
    }

    /**
     * RestController method called when the request pattern is of type '/user/signin'
     * and the incoming request is of 'POST' type
     * Login user if valid credentials are provided and generates JWT auth token
     *
     * @param authorization - String representing the username and password of the user
     * @return - ResponseEntity (SigninResponse along with HTTP status code)
     * @throws AuthenticationFailedException - if the username/ password provided is incorrect
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signin", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthEntity userAuth = userBusinessService.signInUser(decodedArray[0], decodedArray[1]);
        UserEntity user = userAuth.getUser();

        SigninResponse signinResponse = new SigninResponse()
                .id(user.getUuid())
                .message("SIGNED IN SUCCESSFULLY");
        HttpHeaders headers = new HttpHeaders();
        headers.add("access_token", userAuth.getAccessToken());

        return new ResponseEntity<SigninResponse>(signinResponse, headers, HttpStatus.OK);
    }

    /**
     * RestController method called when the request pattern is of type '/user/signin'
     * and the incoming request is of 'POST' type
     * Sign out user if valid authorization token is provided
     *
     * @param authorization - String represents authorization token
     * @return - ResponseEntity (SignoutResponse along with HTTP status code)
     * @throws SignOutRestrictedException - if valid authorization token is not provided
     */
    @RequestMapping(method = RequestMethod.POST, path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") final String authorization)
            throws SignOutRestrictedException {

        UserAuthEntity userAuth = userBusinessService.signOutUser(authorization);
        UserEntity user = userAuth.getUser();

        SignoutResponse signoutResponse = new SignoutResponse()
                .id(user.getUuid())
                .message("SIGNED OUT SUCCESSFULLY");

        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);
    }

}
