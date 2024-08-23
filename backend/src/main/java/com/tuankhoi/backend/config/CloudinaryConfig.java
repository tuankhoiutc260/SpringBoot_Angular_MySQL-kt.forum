package com.tuankhoi.backend.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dk7o7rlks",
                "api_key", "532229764494214",
                "api_secret", "Aj0PdIZU366OxBtzx4NjCTUDnsM"));
    }
}