package com.hiccup.cura.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public interface UserService {

    void toggleStatus(Long id);

    String getProfilePictureUrl(Long id);

    Map<String, String> updateProfilePictureUrl(Long id, MultipartFile file) throws IOException;

    void deleteProfilePicture(Long id) throws IOException;
}
