package com.hiccup.cura.mapper;

import com.hiccup.cura.model.User;
import com.hiccup.cura.request.UserRegisterDto;
import com.hiccup.cura.response.UserLoginResponseDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;
    public User registerDtoToUser(UserRegisterDto userRegisterDto){
        return modelMapper.map(userRegisterDto, User.class);
    }

    public UserLoginResponseDto userToLoginResponseDto(User user){
        return modelMapper.map(user, UserLoginResponseDto.class);
    }
}
