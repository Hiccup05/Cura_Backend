package com.hiccup.cura.service;

import com.hiccup.cura.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IUserService {
    private final UserRepository userRepository;
}
