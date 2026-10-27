package com.RockyToys.User.filehandling;

import java.util.ArrayList;
import java.util.List;

public final class FileFormat {
 private FileFormat() {}
 public static String esc(String s){
  return (s==null?"":s).replace("\\","\\\\").replace("|","\\p").replace("\n","\\n").replace("\r","");
 }
 public static String unesc(String s){
  StringBuilder b=new StringBuilder();boolean e=false;for(char c:s.toCharArray()){if(e){b.append(c=='n'?'\n':c=='p'?'|':c);e=false;}else if(c=='\\')e=true;else b.append(c);}if(e)b.append('\\');return b.toString();
 }
 public static String[] split(String s){List<String> a=new ArrayList<>();StringBuilder b=new StringBuilder();boolean e=false;for(char c:s.toCharArray()){if(e){b.append('\\').append(c);e=false;}else if(c=='\\')e=true;else if(c=='|'){a.add(b.toString());b.setLength(0);}else b.append(c);}if(e)b.append('\\');a.add(b.toString());if(a.size()!=7)throw new IllegalArgumentException("Invalid user record");return a.toArray(new String[0]);}
}
