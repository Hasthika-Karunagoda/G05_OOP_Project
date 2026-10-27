package com.RockyToys.User.model;

import com.RockyToys.User.filehandling.FileFormat;

public class User {
 private String userId,fullName,email,passwordHash,phone,address,role;
 public User(){

 }
 public User(String id, String fullName, String email, String hash, String phone, String address, String role){
  this.userId=id;
  this.fullName=fullName;
  this.email=email;
  this.passwordHash=hash;
  this.phone=phone;
  this.address=address;
  this.role=role;
 }
 public String getUserId(){return userId;}
 public void setUserId(String v){userId=v;}
 public String getFullName(){return fullName;}
 public void setFullName(String v){fullName=v;}
 public String getEmail(){return email;}
 public void setEmail(String v){email=v;}
 public String getPasswordHash(){return passwordHash;}
 public void setPasswordHash(String v){passwordHash=v;}
 public String getPhone(){return phone;}
 public void setPhone(String v){phone=v;}
 public String getAddress(){return address;}
 public void setAddress(String v){address=v;}
 public String getRole(){return role;}
 public void setRole(String v){role=v;}

 public String toFileString(){
  return userId+"|"+ FileFormat.esc(fullName)+"|"+FileFormat.esc(email)+"|"+passwordHash+"|"+FileFormat.esc(phone)+"|"+FileFormat.esc(address)+"|"+role;}

 public static User fromFileString(String s){
  String[] p=FileFormat.split(s);return new User(p[0],FileFormat.unesc(p[1]),FileFormat.unesc(p[2]),p[3],FileFormat.unesc(p[4]),FileFormat.unesc(p[5]),p[6]);
 }
}
