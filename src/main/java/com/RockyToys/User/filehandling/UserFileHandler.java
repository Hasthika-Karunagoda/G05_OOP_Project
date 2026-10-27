package com.RockyToys.User.filehandling;

import com.RockyToys.User.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserFileHandler {
 private final TextFileHandler files;
 private final String path;

 public UserFileHandler(TextFileHandler f,@Value("${app.data-dir:.}") String d){
  files=f;path=d+"/users.txt";
 }
 public List<User> loadAll(){
  List<User> out=new ArrayList<>();for(String s:files.readAll(path))
   try{
    out.add(User.fromFileString(s));
   }
  catch(Exception ignored){

  }
   return out;
 }

 public void saveAll(List<User> items){
  files.rewriteAll(path,items.stream().map(User::toFileString).toList());
 }

 public String nextId(List<User> items){
  return files.nextId("U",items.stream().map(User::getUserId).toList());

 }


}
