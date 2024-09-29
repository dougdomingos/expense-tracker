package com.dougdomingos.expensetracker.services;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.dougdomingos.expensetracker.dto.user.CreateNewUserDTO;
import com.dougdomingos.expensetracker.dto.user.LoginRequestDTO;
import com.dougdomingos.expensetracker.dto.user.LoginResponseDTO;
import com.dougdomingos.expensetracker.dto.user.UserResponseDTO;
import com.dougdomingos.expensetracker.entities.user.Role;
import com.dougdomingos.expensetracker.entities.user.User;
import com.dougdomingos.expensetracker.repositories.RolesRepository;
import com.dougdomingos.expensetracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository users;

    private final RolesRepository roles;

    private final JwtEncoder jwtEncoder;

    private final BCryptPasswordEncoder passwordEncoder;

    private final ModelMapper mapper;

    private final long expiresIn = 600L;

    /**
     * Creates a new user.
     */
    @Override
    public LoginResponseDTO createNewUser(CreateNewUserDTO userDTO) {

        if (users.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new BadCredentialsException("User already exists!");
        }

        Optional<Role> userRole = roles.findByRoleName(Role.TypeRole.ROLE_USER);

        if (!userRole.isPresent()) {
            throw new BadCredentialsException("User role does not exist!");
        }

        User newUser = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .roles(Set.of(userRole.get()))
                .build();

        users.save(newUser);

        return LoginResponseDTO.builder()
                .accessToken(generateToken(newUser, this.expiresIn))
                .expiresIn(this.expiresIn)
                .build();
    }

    /**
     * Given the correct user data, return its equivalent JWT.
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginDTO) {

        Optional<User> user = users.findByUsername(loginDTO.getUsername());
        if (!user.isPresent() || passwordEncoder.matches(loginDTO.getPassword(), user.get().getPassword())) {
            throw new BadCredentialsException("Username or password is incorrect!");
        }

        return LoginResponseDTO.builder()
                .accessToken(generateToken(user.get(), this.expiresIn))
                .expiresIn(this.expiresIn)
                .build();
    }

    /**
     * List all users registered in the application.
     */
    @Override
    public List<UserResponseDTO> listUsers() {
        List<User> userList = users.findAll();

        return userList.stream()
                .map((item) -> mapper.map(item, UserResponseDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Generates a JWT for a given user and expiration time.
     * 
     * @param user      The user for which the token will be created
     * @param expiresIn Expiration time for the token (in seconds)
     * @return The generated token
     */
    private String generateToken(User user, long expiresIn) {
        Instant now = Instant.now();
        String roles = user.getRoles().stream()
                .map((role) -> role.getRoleName().name())
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("expense-tracker")
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(this.expiresIn))
                .claim("roles", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

}
