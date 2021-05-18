package com.csye7125.notifier.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class EmailRequestDto {

    @Email(message = "Invalid Email address")
    private String email;
    @NotEmpty(message = "Email body cannot be Null")
    private String body;
}
