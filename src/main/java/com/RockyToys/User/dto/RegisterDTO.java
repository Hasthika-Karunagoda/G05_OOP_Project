package com.RockyToys.User.dto;
import jakarta.validation.constraints.*;
public class RegisterDTO {
 @NotBlank private String fullName;
 @NotBlank @Email private String email;
 @NotBlank @Size(min=6) private String password;
 private String phone;
 private String address;

 public String getFullName(){return fullName;}
 public void setFullName(String v){fullName=v;}
 public String getEmail(){return email;}
 public void setEmail(String v){email=v;}
 public String getPassword(){return password;}
 public void setPassword(String v){password=v;}
 public String getPhone(){return phone;}
 public void setPhone(String v){phone=v;}
 public String getAddress(){return address;}
 public void setAddress(String v){address=v;}
}
