package com.makemytrip.makemytrip.models;

import lombok.Data;

@Data
public class Reply {
    private String userId;
    private String email;
    private String message;
    private String createdAt;
}
