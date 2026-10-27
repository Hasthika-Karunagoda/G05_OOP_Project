package com.RockyToys.User.filehandling;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Component
public class TextFileHandler {
 private final Path dataDir;
 public TextFileHandler(@Value("${app.data-dir:./data}") String dir){
  dataDir=Paths.get(dir);
  try{
   Files.createDirectories(dataDir);
  }
  catch(IOException e){
   throw new IllegalStateException("Cannot create data directory",e);}
 }
 public synchronized List<String> readAll(String file){
  Path p=Paths.get(file);
  try{if(!Files.exists(p)){
   Files.createDirectories(p.getParent());Files.createFile(p);
  }
  return Files.readAllLines(p);}
  catch(IOException e){
   throw new IllegalStateException("Cannot read data file",e);
  }
 }
 public synchronized void rewriteAll(String file,List<String> lines){
  Path p=Paths.get(file);
  try{Files.createDirectories(p.getParent());Files.write(p,lines,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
  }
  catch(IOException e){
   throw new IllegalStateException("Cannot save data file",e);
  }
 }
 public synchronized String nextId(String prefix,List<String> ids){
  int max=0;for(String id:ids)
   try{max=Math.max(max,Integer.parseInt(id.replaceAll("\\D","")));}
   catch(Exception ignored){}
   return prefix+String.format("%04d",max+1);}
}
