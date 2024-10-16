package com.dougdomingos.expensetracker.services.user;

import java.util.List;

import com.dougdomingos.expensetracker.dto.user.CreateNewUserDTO;
import com.dougdomingos.expensetracker.dto.user.LoginRequestDTO;
import com.dougdomingos.expensetracker.dto.user.LoginResponseDTO;
import com.dougdomingos.expensetracker.dto.user.UserResponseDTO;

public interface UserService {

    LoginResponseDTO createNewUser(CreateNewUserDTO userDTO);

    LoginResponseDTO login(LoginRequestDTO loginDTO);

    List<UserResponseDTO> listUsers();
}
