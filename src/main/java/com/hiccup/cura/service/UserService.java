package com.hiccup.cura.service;

import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.UserRepository;
import com.hiccup.cura.request.UserRegisterDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public void userRegister(UserRegisterDto userRegisterDto){
        User user = dtoToUser(userRegisterDto);
        userRepository.save(user);
    }
    public User dtoToUser(UserRegisterDto userRegisterDto){
        return modelMapper.map(userRegisterDto, User.class);

    }
}
