package com.RockyToys.User.service;

import com.RockyToys.User.dto.*;
import com.RockyToys.User.filehandling.UserFileHandler;
import com.RockyToys.User.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {
 private final UserFileHandler files;
 private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();

 public UserService(UserFileHandler f){files=f;}
 public User registerUser(RegisterDTO d){List<User> l=files.loadAll();
  if(l.stream().anyMatch(u->u.getEmail().equalsIgnoreCase(d.getEmail().trim())))throw new IllegalArgumentException("Email is already registered.");User u=new User(files.nextId(l),d.getFullName().trim(),d.getEmail().trim().toLowerCase(Locale.ROOT),encoder.encode(d.getPassword()),safe(d.getPhone()),safe(d.getAddress()),"CUSTOMER");l.add(u);files.saveAll(l);return u;
 }
 public User loginUser(LoginDTO d){
  return files.loadAll().stream().filter(u->u.getEmail().equalsIgnoreCase(d.getEmail().trim())&&encoder.matches(d.getPassword(),u.getPasswordHash())).findFirst().orElseThrow(()->new IllegalArgumentException("Invalid email or password."));
 }

 public User getUserById(String id){
  return files.loadAll().stream().filter(u->u.getUserId().equals(id)).findFirst().orElseThrow(()->new IllegalArgumentException("User not found."));
 }

 public void updateUser(String id,UserUpdateDTO d){
  List<User> l=files.loadAll();User u=l.stream().filter(x->x.getUserId().equals(id)).findFirst().orElseThrow(()->new IllegalArgumentException("User not found."));u.setFullName(d.getFullName().trim());
  u.setPhone(safe(d.getPhone()));
  u.setAddress(safe(d.getAddress()));
  if(d.getNewPassword()!=null&&!d.getNewPassword().isBlank()){if(d.getNewPassword().length()<6)throw new IllegalArgumentException("New password must have at least 6 characters.");
   u.setPasswordHash(encoder.encode(d.getNewPassword()));}
  files.saveAll(l);}

 public void deleteUser(String id){
  List<User> l=files.loadAll();l.removeIf(u->u.getUserId().equals(id));files.saveAll(l);}public List<User> getAllUsers(){return files.loadAll();}private String safe(String s){return s==null?"":s.trim();}}
