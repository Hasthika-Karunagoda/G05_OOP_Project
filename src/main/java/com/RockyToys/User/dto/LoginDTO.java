package com.RockyToys.User.dto;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank private String email;
    @NotBlank private String password;
    public String getEmail(){return email;}
    public void setEmail(String v){email=v;}
    public String getPassword(){return password;}
    public void setPassword(String v){password=v;}
}
