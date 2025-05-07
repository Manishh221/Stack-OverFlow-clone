package io.mountblue.StackOverflow.controllers;

import io.mountblue.StackOverflow.services.CloudinaryService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ImageUploadController {

    private final CloudinaryService cloudinaryService;

    public ImageUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @PostMapping("/upload-image")
    public Map<String, Object> uploadImage(@RequestParam("image") MultipartFile image) {
        Map<String, Object> result = new HashMap<>();
        try {
            String imageUrl = cloudinaryService.uploadFile(image, "stackoverflow");
            System.out.println("image-url : "+imageUrl);
            result.put("success", 1);
            result.put("url", imageUrl);
        } catch (IOException e) {
            result.put("success", 0);
            result.put("message", "Image upload failed");
        }
        return result;
    }

}
