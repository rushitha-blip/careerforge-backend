package com.careerforge.service;

import com.careerforge.model.StudentProfile;
import com.careerforge.model.User;
import com.careerforge.repository.RecruiterProfileRepository;
import com.careerforge.repository.StudentProfileRepository;
import com.careerforge.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;

    @Mock
    private PlacementReadinessService placementReadinessService;

    @Mock
    private RoadmapService roadmapService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpSession session;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should successfully register student with BCrypt hashed password and initialized profile")
    void testRegisterStudentSuccess() {
        when(userRepository.existsByEmail("student@test.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(studentProfileRepository.save(any(StudentProfile.class))).thenAnswer(i -> {
            StudentProfile sp = i.getArgument(0);
            sp.setId(10L);
            return sp;
        });

        User registered = authService.registerStudent(
                "Aarav", "student@test.com", "secret123", "9876543210",
                "Computer Science", "4th Year", 8.5
        );

        assertNotNull(registered);
        assertEquals("Aarav", registered.getName());
        assertEquals("$2a$10$hashedPassword", registered.getPassword());
        assertEquals("STUDENT", registered.getRole());

        verify(roadmapService, times(1)).getOrCreateRoadmapForStudent(any(StudentProfile.class));
        verify(placementReadinessService, times(1)).calculateAndUpdateReadinessScore(any(StudentProfile.class));
    }

    @Test
    @DisplayName("Should reject registration with IllegalArgumentException when email already exists")
    void testRegisterStudentDuplicateEmail() {
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.registerStudent(
                    "Duplicate", "existing@test.com", "pass", "123",
                    "CSE", "4th Year", 8.0
            );
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login successfully with valid password and populate session")
    void testLoginSuccess() {
        User user = new User("Rahul", "rahul@test.com", "$2a$10$hashed", "STUDENT", "123");
        when(userRepository.findByEmail("rahul@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("myPassword", "$2a$10$hashed")).thenReturn(true);

        User loggedIn = authService.login("rahul@test.com", "myPassword", session);

        assertNotNull(loggedIn);
        assertEquals("rahul@test.com", loggedIn.getEmail());
        verify(session, times(1)).setAttribute("currentUser", user);
    }

    @Test
    @DisplayName("Should fail login when password does not match")
    void testLoginInvalidPassword() {
        User user = new User("Rahul", "rahul@test.com", "$2a$10$hashed", "STUDENT", "123");
        when(userRepository.findByEmail("rahul@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPass", "$2a$10$hashed")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> {
            authService.login("rahul@test.com", "wrongPass", session);
        });

        verify(session, never()).setAttribute(eq("currentUser"), any());
    }
}
