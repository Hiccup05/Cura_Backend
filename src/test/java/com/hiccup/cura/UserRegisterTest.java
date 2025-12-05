package com.hiccup.cura;

import com.hiccup.cura.repository.UserRepository;
import com.hiccup.cura.request.UserRegisterDto;
import com.hiccup.cura.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserRegisterTest {
    @Autowired
    private UserService userService;

    @Test
    public void testUserRegister(){
        UserRegisterDto userRegisterDto=new UserRegisterDto("hiccup","prasad","viking","hiccup@email.com","hiccup123","9827173294");
        userService.userRegister(userRegisterDto);
    }
}
