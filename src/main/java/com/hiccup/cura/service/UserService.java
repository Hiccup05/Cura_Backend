package com.hiccup.cura.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface UserService {

    Map<String, String> updateProfilePictureUrl(Long id, MultipartFile file) throws IOException;
}
