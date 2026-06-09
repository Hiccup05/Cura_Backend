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
    public void toggleStatus(Long id){
        User user=getUserByIdInternal(id);
        user.setActive(!user.isActive());
        userRepository.save(user);
    }

    @Override
    public String getProfilePictureUrl(Long id){
        User user = getUserByIdInternal(id);
        return user.getProfilePictureUrl()!=null ? user.getProfilePictureUrl() : " ";
    }

    @Override
    public Map<String, String> updateProfilePictureUrl(Long id, MultipartFile file) throws IOException {
        User user = getUserByIdInternal(id);
        String publicId="user_"+user.getId();
        String profileUrl = cloudinaryService.uploadUserProfile(file, publicId);
        user.setProfilePictureUrl(profileUrl);
        userRepository.save(user);
        return Map.of("profilePictureUrl",profileUrl);
    }

    @Override
    public void deleteProfilePicture(Long id) throws IOException {
        User user = getUserByIdInternal(id);
        if(user.getProfilePictureUrl()!=null){
            cloudinaryService.deleteUserProfilePhoto(extractPublicId(user.getProfilePictureUrl()));
            user.setProfilePictureUrl(null);
        }else{
            throw new ResourceNotFoundException("There is no profile picture of the user with id " + id);
        }
        userRepository.save(user);
    }

    private String extractPublicId(String url) {
        // URL format: https://res.cloudinary.com/cloud/image/upload/v123456/cura/users/user_1.jpg
        // We need: cura/users/user_1
        String withoutExtension = url.substring(0, url.lastIndexOf('.'));
        String afterUpload = withoutExtension.substring(withoutExtension.indexOf("upload/") + 7);
        // strip version segment if present (starts with 'v' followed by digits)
        return afterUpload.replaceFirst("^v\\d+/", "");
    }

    private User getUserByIdInternal(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
    }
}
