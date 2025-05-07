package wiemwebsite.demo.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CloudinaryUploader {

    public static final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "drius9csi",
            "api_key", "782966969387422",
            "api_secret", "brA76qDdi9yNp7C4924SgBNucvA"
    ));

    public static String uploadImage(File file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        return uploadResult.get("secure_url").toString();
    }
}
