package com.makemytrip.makemytrip.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotifyData {
    private String message;
    private LocalDateTime dateTime;
    private LocalDateTime updatedDate;
}