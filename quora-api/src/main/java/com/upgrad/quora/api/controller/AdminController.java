package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminService;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * RestController method called when the request pattern is of type '/admin/user/{userId}'
     * and the incoming request is of 'DELETE' type
     * Delete user from the database
     *
     * @param uuid                          - String representing user uuid that needs to be deleted from the database
     * @param authorization                 - String represents authorization token
     * @return                              - ResponseEntity (UserDeleteResponse along with HTTP status code)
     * @throws AuthorizationFailedException - if incorrect/ invalid authorization code is sent, or if the user is not 'admin'
     * @throws UserNotFoundException        - if user to be deleted does not exist in the database
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/admin/user/{userId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDeleteResponse> userDelete(@PathVariable("userId") final String uuid,
                                                         @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, UserNotFoundException {

        adminService.deleteUser(uuid, authorization);

        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(uuid)
                .status("USER SUCCESSFULLY DELETED");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse, HttpStatus.OK);
    }

}
