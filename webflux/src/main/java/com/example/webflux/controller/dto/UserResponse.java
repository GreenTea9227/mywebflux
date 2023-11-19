package com.example.webflux.controller.dto;

import com.example.webflux.service.ImageResponse;
import lombok.Data;

import java.util.Optional;

@Data
public class UserResponse {
    private final String id;
    private final String name;
    private final int age;
    private final Long followCount;
    private final Optional<ProfileImageResponse> image;
}
