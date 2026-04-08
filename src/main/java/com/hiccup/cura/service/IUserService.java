package com.hiccup.cura.service;

import com.hiccup.cura.exception.custom.ResourceNotFoundException;
import com.hiccup.cura.model.User;
import com.hiccup.cura.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class IUserService implements UserService {
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Map<String, String> updateProfilePictureUrl(Long id, MultipartFile file) throws IOException {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        String publicId="user_"+user.getId();
        String profileUrl = cloudinaryService.uploadUserProfile(file, publicId);
        user.setProfilePictureUrl(profileUrl);
        userRepository.save(user);
        return Map.of("profilePictureUrl",profileUrl);
    }
}
