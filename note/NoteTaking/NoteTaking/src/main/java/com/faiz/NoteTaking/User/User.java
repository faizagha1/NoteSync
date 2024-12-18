package com.faiz.NoteTaking.User;

import java.time.LocalDateTime;
import java.util.List;

import com.faiz.NoteTaking.Notes.Note;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    @Column(unique = true)
    private String username;
    @Email
    @Column(unique = true)
    private String email;
    @Builder.Default
    private Boolean emailVerified = false;
    @Builder.Default
    private String emailVerificationToken = null;
    @Builder.Default
    private LocalDateTime emailVerificationTokenExpiryDate = null;
    private String password;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;
}
