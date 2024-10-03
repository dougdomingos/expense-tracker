package com.dougdomingos.expensetracker.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.auth.TokenGenerator;
import com.dougdomingos.expensetracker.dto.user.CreateNewUserDTO;
import com.dougdomingos.expensetracker.dto.user.LoginRequestDTO;
import com.dougdomingos.expensetracker.dto.user.LoginResponseDTO;
import com.dougdomingos.expensetracker.dto.user.UserResponseDTO;
import com.dougdomingos.expensetracker.entities.user.Role;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.exceptions.user.PasswordInvalidException;
import com.dougdomingos.expensetracker.exceptions.user.RoleNotFoundException;
import com.dougdomingos.expensetracker.exceptions.user.UserNotFoundException;
import com.dougdomingos.expensetracker.exceptions.user.UsernameAlreadyExistsException;
import com.dougdomingos.expensetracker.repositories.RolesRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository users;

    private final RolesRepository roles;

    private final TokenGenerator tokenGenerator;

    private final BCryptPasswordEncoder passwordEncoder;

    private final ModelMapper mapper;

    private final long expiresIn = 600L; // 10 minutes

    /**
     * Creates a new user.
     */
    @Override
    public LoginResponseDTO createNewUser(CreateNewUserDTO userDTO) {

        if (users.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException();
        }

        Role userRole = roles
                .findByRoleName(Role.TypeRole.USER)
                .orElseThrow(RoleNotFoundException::new);

        User newUser = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles(Set.of(userRole))
                .build();

        users.save(newUser);

        return LoginResponseDTO.builder()
                .accessToken(tokenGenerator.generateToken(newUser, this.expiresIn))
                .expiresIn(this.expiresIn)
                .build();
    }

    /**
     * Given the correct user data, return its equivalent JWT.
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginDTO) {

        User user = users
                .findByUsername(loginDTO.getUsername())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new PasswordInvalidException();
        }

        return LoginResponseDTO.builder()
                .accessToken(tokenGenerator.generateToken(user, this.expiresIn))
                .expiresIn(this.expiresIn)
                .build();
    }

    /**
     * List all users registered in the application.
     */
    @Override
    public List<UserResponseDTO> listUsers() {
        return users.findAll()
                .stream()
                .map((item) -> mapper.map(item, UserResponseDTO.class))
                .collect(Collectors.toList());
    }
}
