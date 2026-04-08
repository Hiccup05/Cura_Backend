package com.hiccup.cura.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadUserProfile(MultipartFile file, String publicId) throws IOException {
        Map upload = cloudinary.uploader().upload(
                file.getBytes(), ObjectUtils.asMap(
                        "folder", "cura/users",
                        "public_id", publicId,
                        "overwrite", true
                )
        );
        return upload.get("secure_url").toString();
    }

    public String uploadServicePhoto(MultipartFile file, String publicId) throws IOException {
        Map upload = cloudinary.uploader().upload(
                file.getBytes(), ObjectUtils.asMap(
                        "folder", "cura/services",
                        "public_id", publicId,
                        "overwrite", true
                )
        );
        return upload.get("secure_url").toString();
    }

    public void delete(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
