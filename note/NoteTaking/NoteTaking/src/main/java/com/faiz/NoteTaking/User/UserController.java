package com.faiz.NoteTaking.User;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.faiz.NoteTaking.Authetication.LoginRequest;
import com.faiz.NoteTaking.Authetication.LoginResponse;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<UserResponse> getUser(@RequestAttribute("authenticatedUser") User user) {
        User userCurr = userService.findUser(user.getUsername());
        UserResponse userResponse = new UserResponse(userCurr.getEmail(), "User found");
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestBody UserRequest request) {
        try {
            return ResponseEntity.ok(userService.registerUser(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(
            @RequestBody LoginRequest request) {
        try {
            LoginResponse loginResponse = userService.loginUser(request);
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/validate-email-verification-token")
    public Response verifyEmail(@RequestParam String token,
            @RequestAttribute("authenticatedUser") User user) {
        userService.verifyEmail(token, user.getEmail());
        return new Response("Email verified successfully.");
    }

    @GetMapping("/send-email-verification-token")
    public Response sendEmailVerificationToken(@RequestAttribute("authenticatedUser") User user)
            throws MessagingException {
        userService.sendVerificationEmail(user.getEmail());
        return new Response("Email verification token sent successfully.");
    }
}
