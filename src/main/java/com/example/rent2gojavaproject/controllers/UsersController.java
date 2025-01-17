package com.example.rent2gojavaproject.controllers;

import com.example.rent2gojavaproject.core.utilities.results.DataResult;
import com.example.rent2gojavaproject.core.utilities.results.Result;
import com.example.rent2gojavaproject.services.abstracts.UserService;
import com.example.rent2gojavaproject.services.dtos.requests.userRequest.UpdateUserRequest;
import com.example.rent2gojavaproject.services.dtos.responses.userResponse.GetUserListResponse;
import com.example.rent2gojavaproject.services.dtos.responses.userResponse.GetUserResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@CrossOrigin
public class UsersController {
    private final UserService userService;

    @GetMapping("/getall")
    public DataResult<List<GetUserListResponse>> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/getAllActiveOrNot")
    public DataResult<Iterable<GetUserListResponse>> findAll(@RequestParam boolean isActive) {
        return this.userService.findAll(isActive);
    }

    @GetMapping("/{id}")
    public DataResult<GetUserResponse> getUserById(@PathVariable int id) {
        return userService.getById(id);
    }

   /* @PostMapping("/add")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Result createUser(@RequestBody @Valid AddUserRequest addUserRequest) {
        return userService.addUser(addUserRequest);
    }*/

    @PutMapping("/update")
    @ResponseStatus(code = HttpStatus.OK)
    public Result updateUser(@RequestBody @Valid UpdateUserRequest updateUserRequest) {
        return userService.updateUser(updateUserRequest);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public Result deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }
}
