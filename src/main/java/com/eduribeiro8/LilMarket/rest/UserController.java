package com.eduribeiro8.LilMarket.rest;

import com.eduribeiro8.LilMarket.dto.UserRequestDTO;
import com.eduribeiro8.LilMarket.dto.UserResponseDTO;
import com.eduribeiro8.LilMarket.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public UserResponseDTO save(@RequestBody UserRequestDTO userRequestDTO){
        return userService.save(userRequestDTO);
    }

    @GetMapping("/user/username/{username}")
    public UserResponseDTO getUserByUsername(@PathVariable String username){
        return userService.findUserByUsernameDTO(username);
    }
}
