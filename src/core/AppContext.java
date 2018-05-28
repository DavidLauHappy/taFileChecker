package core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AppContext {
	private static AppContext unique_instance;
	public static AppContext getInstance(){
		if(unique_instance==null)
			unique_instance=new AppContext();
		return unique_instance;
	}
	
	
	public List<JService> getServices(){
		return Services;
	}
	
	public void setService(JService service){
		boolean meet=false;
		for(JService jService:Services){
			if(jService.getId().equals(service.getId())){
				meet=true;
				jService.setClassURL(service.getClassURL());
				jService.setConfig(service.getConfig());
				jService.setName(service.getName());
				jService.setParameters(service.getParameters());
				jService.setWorkDate(service.getWorkDate());
			}
		}
		if(!meet){
			Services.add(service);
		}
	}
	
	private List<JService> Services;
	
	
	private AppContext(){
		Services=new ArrayList();
	}
	
	public void init(String appPath){
		this.appPath=appPath;
	}
	public String appPath="";
	public String dbid="603";
	
	
	private boolean runFlag=false;
	private boolean workFlag=false;
	
	

	public boolean runable() {
		  String lockFile=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"ini"+File.separator+"lock.ini";
		   File file=new File(lockFile);
		   if(file.exists()){
			   return false;
		   }
		   else{
			   return runFlag;
		   }
	}

	public void setRunFlag(boolean runFlag) {
		this.runFlag = runFlag;
	}

	public boolean canWork() {
		 String lockFile=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"ini"+File.separator+"lock.ini";
		   File file=new File(lockFile);
		   if(file.exists()){
			   return false;
		   }else{
			   return workFlag;
		   }
	}

	public void setWorkFlag(boolean workFlag) {
		 if(workFlag){
		  String lockFile=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"ini"+File.separator+"lock.ini";
		   File file=new File(lockFile);
		   if(!file.exists()){
			   this.workFlag = workFlag;
		   }
		 }else{
			 this.workFlag = workFlag;
		 }
	}
	
	
	
}
