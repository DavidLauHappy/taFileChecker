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
				//ѭ���ж�����
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
				//˯��5����
			  try{
				  Thread.sleep(5000);
			  }catch(Exception e){
					AppUtils.logError("���ȷ������쳣��"+e.toString());
				}
			}
			//�����߳���Ҫ�˳�
			FDEPCheckAction.getService().exit();
			XZSXCheckAction.getService().exit();
			AppUtils.logInof("���ȷ���.�յ��ⲿ�˳��źųɹ���ֹ,�����˳���");
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
			   AppUtils.logInof("taFileCheckt������.�յ��ⲿ�˳��ź�׼����ֹ");
		  }catch(Exception e){
				  e.printStackTrace();
			  }
		  }
	  
	  private void unlock(){
		  String lockFile=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"ini"+File.separator+"lock.ini";
		   File file=new File(lockFile);
		   if(file.exists()){
			   file.delete();
			   AppUtils.logInof("taFileCheckt������.���������ⲿ�˳��ź����");
		   }
	  }
		
	
		private String getStartPath(){
			String path="";
			try{
					path= System.getProperty("user.dir");//���ֲ�֧������·����
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
