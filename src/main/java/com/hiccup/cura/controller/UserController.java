package com.hiccup.cura.controller;

import com.hiccup.cura.model.User;
import com.hiccup.cura.request.UserRegisterDto;
import com.hiccup.cura.response.ApiResponse;
import com.hiccup.cura.response.UpdateUserResponseDto;
import com.hiccup.cura.response.UserLoginResponseDto;
import com.hiccup.cura.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@RequestBody UserRegisterDto userRegisterDto){
        userService.userRegister(userRegisterDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("User Registered Successfully",null));
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable Long id){
        UserLoginResponseDto user = userService.getUserById(id);
        return ResponseEntity.ok().body(new ApiResponse("User fetched successfully",user));
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long id, @RequestBody User user){
        UpdateUserResponseDto updatedUser = userService.updateUser(user,id);
        return ResponseEntity.ok().body(new ApiResponse("User updated successfully",updatedUser));
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id){
        userService.deleteUserByUserId(id);
        return ResponseEntity.ok().body(new ApiResponse("User fetched successfully",null));
    }
}
