package core;

import java.util.ArrayList;
import java.util.List;

public class TaItem {
	private String taCode="";
	private String taName="";
	private String sendPath="";
	private List<FileItem> files=null;
	
	public TaItem(String taCode, String taName, String sendPath) {
		super();
		this.taCode = taCode;
		this.taName = taName;
		this.sendPath = sendPath;
	}
	
	public void addFile(FileItem fileItem){
		if(files==null)
			files=new ArrayList<FileItem>();
		files.add(fileItem);
	}

	public void setFileOk(String fileName,String fullName){
		for(FileItem file:files){
			if(file.getName().equalsIgnoreCase(fileName)){
				file.setFileOk(true);
				file.setFullPath(fullName);
			}
		}
	}
	
	public void setFileSend(String fileName){
		for(FileItem file:files){
			if(file.getName().equalsIgnoreCase(fileName)){
				file.setFileSend(true);
			}
		}
	}
	
	public String getTaCode() {
		return taCode;
	}

	public String getTaName() {
		return taName;
	}

	public String getSendPath() {
		return sendPath;
	}

	public List<FileItem> getFiles() {
		return files;
	}
	
	
	

}
