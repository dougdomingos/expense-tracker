package com.dougdomingos.expensetracker.controllers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import com.dougdomingos.expensetracker.dto.user.CreateNewUserDTO;
import com.dougdomingos.expensetracker.dto.user.LoginRequestDTO;
import com.dougdomingos.expensetracker.dto.user.LoginResponseDTO;
import com.dougdomingos.expensetracker.entities.user.Role;
import com.dougdomingos.expensetracker.entities.user.Role.TypeRole;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.exceptions.ApplicationErrorType;
import com.dougdomingos.expensetracker.repositories.RolesRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for user features")
public class UserControllerTest {

    final String URI_USERS = "/users";

    final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc driver;

    @Autowired
    ModelMapper mapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    CommandLineRunner dataInitializer;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    JwtDecoder jwtDecoder;

    @BeforeEach
    void setup() throws Exception {
        dataInitializer.run();

        userRepository.save(User.builder()
                .username("User 001")
                .password(passwordEncoder.encode("test123"))
                .build());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        rolesRepository.deleteAll();
    }

    @Nested
    @DisplayName("Validation tests for admin user and roles")
    class AdminAndRolesTests {

        @Test
        @DisplayName("Check if roles exist on application startup")
        void doRolesExist_onAppStartup() {
            Optional<Role> userRole = rolesRepository.findByRoleName(TypeRole.USER);
            Optional<Role> adminRole = rolesRepository.findByRoleName(TypeRole.ADMIN);

            assertAll(
                    () -> assertTrue(userRole.isPresent()),
                    () -> assertTrue(adminRole.isPresent()));
        }

        @Test
        @DisplayName("Check if admin user exist on application startup")
        void doAdminUserExist_onAppStartup() {
            Optional<User> adminUser = userRepository.findByUsername("admin");

            assertAll(
                    () -> assertTrue(adminUser.isPresent()),
                    () -> assertTrue(userContainsRole(TypeRole.ADMIN, adminUser.get())));
        }
    }

    @Nested
    @DisplayName("Validations for user creation")
    class CreateUserValidationTests {

        @Test
        @DisplayName("Rejects creating users with null name")
        void whenCreateUser_withNullName_expectToFail() throws Exception {
            CreateNewUserDTO newUser = CreateNewUserDTO.builder()
                    .username(null)
                    .password("test_passwd")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Username is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects creating users with blank name")
        void whenCreateUser_withBlankName_expectToFail() throws Exception {
            CreateNewUserDTO newUser = CreateNewUserDTO.builder()
                    .username("")
                    .password("test_passwd")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Username is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects creating users with null password")
        void whenCreateUser_withNullPassword_expectToFail() throws Exception {
            CreateNewUserDTO newUser = CreateNewUserDTO.builder()
                    .username("Test User")
                    .password(null)
                    .build();

            String responseJSON = driver.perform(post(URI_USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Password is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects creating users with blank password")
        void whenCreateUser_withBlankPassword_expectToFail() throws Exception {
            CreateNewUserDTO newUser = CreateNewUserDTO.builder()
                    .username("Test User")
                    .password("")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Password is required", result.getErrors().get(0)));
        }
    }

    @Nested
    @DisplayName("Validations for user login")
    class LoginValidationTests {

        @Test
        @DisplayName("Rejects login with null name")
        void whenLogin_withNullName_expectToFail() throws Exception {
            LoginRequestDTO newUser = LoginRequestDTO.builder()
                    .username(null)
                    .password("test_passwd")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Username is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects login with blank name")
        void whenLogin_withBlankName_expectToFail() throws Exception {
            LoginRequestDTO newUser = LoginRequestDTO.builder()
                    .username("")
                    .password("test_passwd")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Username is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects login with null password")
        void whenLogin_withNullPassword_expectToFail() throws Exception {
            LoginRequestDTO newUser = LoginRequestDTO.builder()
                    .username("Test User")
                    .password(null)
                    .build();

            String responseJSON = driver.perform(post(URI_USERS + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Password is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects login with blank password")
        void whenLogin_withBlankPassword_expectToFail() throws Exception {
            LoginRequestDTO newUser = LoginRequestDTO.builder()
                    .username("Test User")
                    .password("")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Validation errors have occurred", result.getMessage()),
                    () -> assertEquals("Password is required", result.getErrors().get(0)));
        }

        @Test
        @DisplayName("Rejects login for a inexistent user")
        void whenLogin_withInexistentUser_expectToFail() throws Exception {
            LoginRequestDTO newUser = LoginRequestDTO.builder()
                    .username("I do not exist =O")
                    .password("test_password")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Specified user not found", result.getMessage()));
        }

        @Test
        @DisplayName("Rejects login with wrong password")
        void whenLogin_withWrongPassword_expectToFail() throws Exception {
            LoginRequestDTO newUser = LoginRequestDTO.builder()
                    .username("User 001")
                    .password("shh, this is a secret ;)")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isBadRequest())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            ApplicationErrorType result = objectMapper
                    .readValue(responseJSON, ApplicationErrorType.class);

            assertAll(
                    () -> assertEquals("Provided password is invalid", result.getMessage()));
        }
    }

    @Nested
    @DisplayName("Tests for API features")
    class APIFeaturesTests {

        @Test
        @DisplayName("Accept creating user with valid data")
        void whenCreateUser_withValidData_expectToPass() throws Exception {
            CreateNewUserDTO newUser = CreateNewUserDTO.builder()
                    .username("Test User")
                    .password("test_passwd")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isCreated())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            LoginResponseDTO result = objectMapper
                    .readValue(responseJSON, LoginResponseDTO.LoginResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertNotNull(result.getAccessToken()),
                    () -> assertNotNull(result.getExpiresIn()));
        }
        
        @Test
        @DisplayName("Accept login as user with valid data")
        void whenLoginAsUser_withValidData_expectToPass() throws Exception {
            LoginRequestDTO newUser = LoginRequestDTO.builder()
                    .username("User 001")
                    .password("test123")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            LoginResponseDTO result = objectMapper
                    .readValue(responseJSON, LoginResponseDTO.LoginResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertNotNull(result.getAccessToken()),
                    () -> assertNotNull(result.getExpiresIn()));
        }

        @Test
        @DisplayName("Accept login as admin with valid data")
        void whenLoginAsAdmin_withValidData_expectToPass() throws Exception {
            LoginRequestDTO newUser = LoginRequestDTO.builder()
                    .username("admin")
                    .password("admin")
                    .build();

            String responseJSON = driver.perform(post(URI_USERS + "/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn().getResponse().getContentAsString();

            LoginResponseDTO result = objectMapper
                    .readValue(responseJSON, LoginResponseDTO.LoginResponseDTOBuilder.class)
                    .build();

            assertAll(
                    () -> assertNotNull(result.getAccessToken()),
                    () -> assertNotNull(result.getExpiresIn()));
        }
    }

    /**
     * Checks if a user has a particular role.
     * 
     * @param role The expected role
     * @param user The user to be verified
     * @return A boolean value; true if the user contains the given role, false
     *         otherwise
     */
    private boolean userContainsRole(TypeRole role, User user) {
        boolean hasRole = false;

        for (Role userRole : user.getRoles()) {
            hasRole = userRole.getRoleName().equals(role) ? true : false;
        }

        return hasRole;
    }

}
