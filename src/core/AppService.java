package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import func.FDEPCheckAction;
import func.XZSXCheckAction;

public class AppService {
	public AppLoader appLoader;
	private  static AppService app=null;
	
	public static void main(String[] args){
			if(args!=null){
				if("start".equals(args[0])){
						AppService.getInstance().start();
				}else if("stop".equals(args[0])){
					AppService.getInstance().stop();
					System.exit(0);
				}
			}else{
				System.exit(0);
			}
		}


		public void start(){
			AppContext.getInstance().init(app.getStartPath());
			app.appLoader=new AppLoader();
			app.appLoader.loadCfgs();
			app.unlock();
			AppContext.getInstance().setRunFlag(true);
			AppContext.getInstance().setWorkFlag(true);
			app.run();
		}
		
		public void stop(){
			AppContext.getInstance().init(app.getStartPath());
			AppService.getInstance().lock();
		}
		
		private void run(){
			while(AppContext.getInstance().runable()){
				//循环判断任务
				for(JService Service:AppContext.getInstance().getServices()){
					String startTime=Service.getStartCheckTime();
					String currTime=AppUtils.getCurrentDate("HH:mm:ss");
					if(currTime.replace(":", "").compareTo(startTime.replace(":", ""))>0){
						 String classID=Service.getClassURL();
						 if(classID.equals("func.FDEPCheckAction")){
							 FDEPCheckAction.getService().wakeUp(Service);
						 }
						 if(classID.equals("func.XZSXCheckAction")){
							 XZSXCheckAction.getService().wakeUp(Service);
						 }
					}else{
						 String classID=Service.getClassURL();
						 if(classID.equals("func.FDEPCheckAction")){
							 FDEPCheckAction.getService().Service=Service;
						 }
						 if(classID.equals("func.XZSXCheckAction")){
							 XZSXCheckAction.getService().Service=Service;
						 }
					}
				}
				//睡眠5秒钟
			  try{
				  Thread.sleep(5000);
			  }catch(Exception e){
					AppUtils.logError("调度服务发生异常："+e.toString());
				}
			}
			//服务线程需要退出
			FDEPCheckAction.getService().exit();
			XZSXCheckAction.getService().exit();
			AppUtils.logInof("调度服务.收到外部退出信号成功终止,程序退出！");
			System.exit(0);
		}
		

		
	  private void lock(){
		  try{
			  String lockFile=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"ini"+File.separator+"lock.ini";
			  String currentTime=AppUtils.getCurrentDate("yyyyMMdd-HH:mm:ss.SSS");
			   File file=new File(lockFile);
			   FileWriter fw = new FileWriter(file,true);
			   BufferedWriter bw = new BufferedWriter(fw);
			   bw.write(currentTime);
			   bw.write("\r\n");
			   bw.close();
			   fw.close();
			   AppUtils.logInof("taFileCheckt定点检测.收到外部退出信号准备终止");
		  }catch(Exception e){
				  e.printStackTrace();
			  }
		  }
	  
	  private void unlock(){
		  String lockFile=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"ini"+File.separator+"lock.ini";
		   File file=new File(lockFile);
		   if(file.exists()){
			   file.delete();
			   AppUtils.logInof("taFileCheckt定点检测.启动忽略外部退出信号完成");
		   }
	  }
		
	
		private String getStartPath(){
			String path="";
			try{
					path= System.getProperty("user.dir");//这种不支持中文路径吧
			}catch(Exception e){
				e.printStackTrace();
			}
			return path;
		}
		
		private AppService(){}
		public static AppService getInstance(){
			if(app==null)
				app=new AppService();
			return app;
		}
}
