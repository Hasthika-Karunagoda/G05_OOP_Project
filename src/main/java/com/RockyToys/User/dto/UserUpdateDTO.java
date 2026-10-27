package com.RockyToys.User.dto;
import jakarta.validation.constraints.NotBlank;
public class UserUpdateDTO {
    @NotBlank private String fullName;
    private String phone,address,newPassword;

    public String getFullName(){return fullName;}
    public void setFullName(String v){fullName=v;}
    public String getPhone(){return phone;}
    public void setPhone(String v){phone=v;}
    public String getAddress(){return address;}
    public void setAddress(String v){address=v;}
    public String getNewPassword(){return newPassword;}
    public void setNewPassword(String v){newPassword=v;}}
