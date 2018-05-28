package core;

public class FileItem {
	private String taCode="";
	private String taName="";
	private String name="";
	private String desc="";
	private String sendPath="";
	private String fullPath="";
	private boolean isFileOk=false;//文件实体已经存在，可以生成ok文件了
	private boolean isFileSend=false;
	
	public FileItem(String taCode, String taName, String name, String desc,String sendPath) {
		super();
		this.taCode = taCode;
		this.taName = taName;
		this.name = name;
		this.desc = desc;
		this.sendPath=sendPath;
	}

	public String getTaCode() {
		return taCode;
	}

	public String getTaName() {
		return taName;
	}


	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}
	
	public String getSendPath() {
		return sendPath;
	}

	public boolean isFileOk() {
		return isFileOk;
	}

	public boolean isFileSend() {
		return isFileSend;
	}

	public void setFileOk(boolean isFileOk) {
		this.isFileOk = isFileOk;
	}

	public void setFileSend(boolean isFileSend) {
		this.isFileSend = isFileSend;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
	
	
}
