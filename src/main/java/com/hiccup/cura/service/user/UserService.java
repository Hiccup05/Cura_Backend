package com.hiccup.cura.service.user;

import com.hiccup.cura.exception.UserAlreadyFound;
import com.hiccup.cura.exception.UserNotFound;
import com.hiccup.cura.mapper.UserMapper;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.UserRepository;
import com.hiccup.cura.request.UserRegisterDto;
import com.hiccup.cura.response.UpdateUserResponseDto;
import com.hiccup.cura.response.UserLoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public void userRegister(UserRegisterDto userRegisterDto){
            userRepository.findByEmail(userRegisterDto.getEmail()).ifPresent(user->{
                throw new UserAlreadyFound("User already exists with this email.");
            });
            User user = userMapper.registerDtoToUser(userRegisterDto);
            userRepository.save(user);
    }

    public UserLoginResponseDto getUserById(Long id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFound("Cannot find user"));
        return userMapper.userToLoginResponseDto(user);
    }

    public void deleteUserByUserId(Long id){
        userRepository.findById(id).ifPresentOrElse(
                userRepository::delete,
                ()->{
                    throw new UserNotFound("Cannot find user");
                }
        );
    }

    public UpdateUserResponseDto updateUser(User user, Long id){
        return userRepository.findById(id).map(userInDb -> {
            userInDb.setFirstName(user.getFirstName());
            userInDb.setMiddleName(user.getMiddleName());
            userInDb.setLastName(user.getLastName());
            userInDb.setMobNo(user.getMobNo());
            userInDb.setDateOfBirth(user.getDateOfBirth());
            userInDb.setAddress(user.getAddress());
            User updatedUser = userRepository.save(userInDb);
            return userMapper.userUpdateResponseDto(user);
        }).orElseThrow(() -> new UserNotFound("User Not found"));
    }

}
