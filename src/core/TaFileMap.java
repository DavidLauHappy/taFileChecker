package core;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaFileMap{
	
	  public  Map<String,TaItem> taMap=null;
      private List<FileItem> AFiles=null;// not meet file
      private List<FileItem> BFiles=null;//all files
      public int fileCount=0;
      public  boolean allFileChecked=false;
      
	   public TaFileMap(){
		   taMap=new HashMap<String, TaItem>();
		   AFiles=new ArrayList<FileItem>();
		   BFiles=new ArrayList<FileItem>();
	   }
	   
	   public void clear(){
		   taMap.clear();
	   }
	   
	   public boolean containsKey(String key){
		   return taMap.containsKey(key);
	   }
	   
	   public TaItem get(String taCode){
		   return taMap.get(taCode);
	   }
	   
	   public void put(String taCode,TaItem taObj){
		   taMap.put(taCode, taObj);
	   }
	   
	   public void lock(){
		   BFiles.clear();
		   for(String ta:taMap.keySet()){
    		   if(taMap.get(ta).getFiles()!=null&&taMap.get(ta).getFiles().size()>0){
    			   for(FileItem file:taMap.get(ta).getFiles()){
    				   BFiles.add(file);
    			   }
    		   }
    	   }
		   this.fileCount=BFiles.size();
	   }
     
       
       
       public void isAllFilesOk(){
    	   	AFiles.clear();
    	   for(String ta:taMap.keySet()){
    		   if(taMap.get(ta).getFiles()!=null&&taMap.get(ta).getFiles().size()>0){
    			   for(FileItem file:taMap.get(ta).getFiles()){
    				   if(!file.isFileOk()){
    					   AFiles.add(file);
    				   }
    			   }
    		   }
    	   }
    	   if(AFiles.size()>0){
    		   allFileChecked=false;
    	   }else{
    		   allFileChecked=true;
    	   }
       }
       
       

    public String getFileInof(){
 	   String result="应到文件个数[#a]缺少文件个数[#b]";
 	   result=result.replace("#a", this.fileCount+"");
 	   result=result.replace("#b", this.AFiles.size()+"");
 	   return result;
    }
  
    public String getFileCheckInfo(){
    	  int sendedCount=0;
    	  for(String ta:taMap.keySet()){
   		   if(taMap.get(ta).getFiles()!=null&&taMap.get(ta).getFiles().size()>0){
   			   for(FileItem file:taMap.get(ta).getFiles()){
   				   if(file.isFileSend()){
   					   sendedCount++;
   				   }
   			   }
   		   }
   	     }
    	  String result="应发文件个数[#a]实际从日志判断成功发送件个数[#b]";
    	   result=result.replace("#a", this.fileCount+"");
    	   result=result.replace("#b",sendedCount+"");
    	   String tail="文件数一致";
    	   if(sendedCount<this.fileCount)
    		   tail="文件数不一致";
    	   result=result+tail;
    	   return result;
    }
    
    public List<FileItem> getMistFiles(){
 	   return this.AFiles;
    }
}
