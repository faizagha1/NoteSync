package com.faiz.NoteTaking.User;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.faiz.NoteTaking.Authetication.EmailService;
import com.faiz.NoteTaking.Authetication.Encoder;
import com.faiz.NoteTaking.Authetication.JsonWebToken;
import com.faiz.NoteTaking.Authetication.LoginRequest;
import com.faiz.NoteTaking.Authetication.LoginResponse;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final Encoder encoder;
    private final JsonWebToken jwt;
    private final EmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    public static String generateEmailVerificationToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }

    public void sendVerificationEmail(String email) throws MessagingException {
        User user = userRepository.findByEmail(email);
        if (user != null && !user.getEmailVerified()) {
            String token = generateEmailVerificationToken();
            String hashedToken = encoder.Enocode(token);
            user.setEmailVerificationToken(hashedToken);
            user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(3));
            userRepository.save(user);
            String subject = "Email Verification";
            String message = "To verify your email, please use this code: " + token;
            try {
                emailService.sendEmail(email, subject, message);
            } catch (Exception e) {
                logger.info("Error while sending email: {}", e.getMessage());
            }
        } else {
            throw new IllegalArgumentException("Email verification token failed, or email is already verified.");
        }
    }

    public void verifyEmail(String email, String token) {
        User user = userRepository.findByEmail(email);
        if (user != null && encoder.match(token, user.getEmailVerificationToken())
                && !user.getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            user.setEmailVerificationTokenExpiryDate(null);
            userRepository.save(user);
        } else if (user != null && encoder.match(token, user.getEmailVerificationToken())
                && user.getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        } else {
            throw new IllegalArgumentException("Invalid token");
        }
    }

    public LoginResponse registerUser(UserRequest request) throws MessagingException {
        if (userRepository.findByEmail(request.email()) != null) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.findByUsername(request.username()) != null) {
            throw new RuntimeException("Username already exists");

        }
        String encodedPassword = encoder.Enocode(request.password());
        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .username(request.username())
                .email(request.email())
                .password(encodedPassword)
                .build();

        String emailToken = generateEmailVerificationToken();
        String hashedToken = encoder.Enocode(emailToken);
        user.setEmailVerificationToken(hashedToken);
        user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(3));

        userRepository.save(user);

        String subject = "Email Verification";
        String message = "To verify your email, please use this code: " + emailToken;
        try {
            emailService.sendEmail(request.email(), subject, message);
        } catch (Exception e) {
            logger.info("Error while sending email: {}", e.getMessage());
        };

        String token = jwt.generateToken(user.getUsername());
        emailService.sendEmail(request.email(), "Registration successful", "You have successfully registered");
        return new LoginResponse(token, request.email(), "Registration successful");
    }

    public LoginResponse loginUser(LoginRequest request) {
        User user = userRepository.findByUsername(request.username());
        if (!encoder.match(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        String token = jwt.generateToken(user.getUsername());
        return new LoginResponse(token, user.getEmail(), "Login successful");
    }

    public User findUser(String username) {
        return userRepository.findByUsername(username);
    }

}
