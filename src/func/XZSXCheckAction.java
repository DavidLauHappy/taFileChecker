package func;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;


import core.AppContext;
import core.AppUtils;
import core.DateUtilis;
import core.FileItem;
import core.JService;
import core.TaFileMap;
import core.TaItem;
import func.Action.ActionState;

public class XZSXCheckAction  extends Thread implements Action{
	private static XZSXCheckAction unique_instance;
	public static XZSXCheckAction getService(){
		if(unique_instance==null)
			unique_instance=new XZSXCheckAction();
		return unique_instance;
	}
	
	private XZSXCheckAction(){
		status=ActionState.Init;
		bussinessStatus=Status.Ready;
		}
	
	public static String ServiceName="XZSXCheck";
	public enum Status{Ready,
									ListLoaded,
									ListLoadedError,
									AllFileExists,
									FileLack,
									OkFilesSuccess,
									OkFilesError,
									AllFileSend,
									FileSendError;
									};
	public Status bussinessStatus;
	public String checkFreq="";
	public String sendTimeout="";
	public String offDays="";
	public String fileBaseDir="";
	public String logPath="";
	public String checkRules="";
	public long sleepCycle=1000L;
	public String currentStep="";//��ǰ������жϲ���
	public String breakStep="";//��ǰ������жϲ���
	public String doneFileList="";//��ǰ��������ɵ�OK�ļ�����
	public String breakWorkDate="";//�ж�ʱ�Ĺ�������
	public String fileProcess(JService service){
		String result="";
		try{
			Map<String,String> parameters=service.getParameters();
			if(parameters!=null){
				if(parameters.containsKey("checkFreq")){
				 checkFreq=parameters.get("checkFreq");
					}
				if(parameters.containsKey("sendTimeout")){
					sendTimeout=parameters.get("sendTimeout");
				}
				if(parameters.containsKey("offDays")){
					offDays=parameters.get("offDays");
				}
				if(parameters.containsKey("sendBasePath")){
					fileBaseDir=parameters.get("sendBasePath");
				}
				if(parameters.containsKey("logPath")){
					logPath=parameters.get("logPath");
				}
				if(parameters.containsKey("checkRules")){
					checkRules=parameters.get("checkRules");
				}

			}
			if(this.bussinessStatus.equals(Status.AllFileSend)){
				long millSecond=Action.sleepSecond*1000;
				if(!AppUtils.isNullOrEmpty(this.checkFreq)){
					int checkCycle=Integer.parseInt(this.checkFreq);
					millSecond=checkCycle*1000;
				}
				try {
					this.sleep(millSecond);
				} catch (InterruptedException e) {
					AppUtils.logInof("����["+this.Service.getName()+"]�ⲿ�ж� ˯���쳣��"+e.toString() );
				}
				sleepCycle=millSecond;
			}else{
				this.loadFileList(service.getWorkDate(),service.getConfig());//�������������ļ�
				this.fileCheck(service.getWorkDate(),fileBaseDir);
				this.makeOkFiles(service.getWorkDate(),fileBaseDir);
				//����һ��ʱ���
				if(!AppUtils.isNullOrEmpty(this.sendTimeout)){
					int sleepCnt=Integer.parseInt(this.sendTimeout);
					try {
						this.sleep(sleepCnt*1000L);
					} catch (InterruptedException e) {
						AppUtils.logInof("����["+this.Service.getName()+"]�ⲿ�ж� ˯���쳣��"+e.toString() );
					}
				}
				this.checkSendLog(service.getWorkDate());
				if(this.bussinessStatus.equals(Status.AllFileSend)){
					//������������л�����
					String nextWorkDate=DateUtilis.getNextWorkDate(service.getWorkDate(),offDays);
					service.setWorkDate(nextWorkDate);
					AppContext.getInstance().setService(service);
					//��������״̬
					status=ActionState.Idel;
					this.setRunAble(false);
					 currentStep="StepDone";
					AppUtils.logInof("����["+this.Service.getName()+"]�������ߵȴ�,���������л���:"+this.Service.getWorkDate());
				}else{
					status=ActionState.Error;
				}
			}
			
		}catch(Exception e){
			result="";
		}
		return result;
	}
	
	 public TaFileMap TaMap=new TaFileMap();
	 private void loadFileList(String workDate,String config){
		 	if(bussinessStatus.equals(Status.Ready)||bussinessStatus.equals(Status.ListLoadedError)){
		 		AppUtils.logService(ServiceName, "�е�˫�򶨵������.����"+workDate+"��⹤�������嵥�ļ�����",workDate);
		    	SAXBuilder builder = new SAXBuilder();
				Document doc;
				String cfgPath="";
				try{
						cfgPath=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+config;
						doc = builder.build(cfgPath);
						TaMap.clear();
						Element fileList=doc.getRootElement();
						List<Element> tas=fileList.getChildren("Ta");
						if(tas!=null&&tas.size()>0){
							for(Element ta:tas){
								String taCode=ta.getAttributeValue("TaCode");
								String taName=ta.getAttributeValue("TaName");
								String sendPath=ta.getAttributeValue("SendPath");
								//no send path Ta need work by hand and donn't need check
								if(!AppUtils.isNullOrEmpty(sendPath)){
									TaItem taObj=null;
									sendPath=sendPath.replace("#yyyymmdd", workDate);
									sendPath=sendPath.replace("#YYYYMMDD", workDate);
									if(TaMap.containsKey(taCode)){
											taObj=TaMap.get(taCode);
									}else{
										taObj=new TaItem(taCode,taName,sendPath);
									}
									List<Element> files=ta.getChildren("sendFile");
									if(files!=null&&files.size()>0){
										for(Element file:files){
											String name=file.getAttributeValue("FileName");
											name=name.replace("#yyyymmdd", workDate);
											name=name.replace("#YYYYMMDD", workDate);
											String desc=file.getAttributeValue("Description");
											FileItem item=new FileItem(taCode,taName,name,desc,sendPath);
											taObj.addFile(item);
										}
									TaMap.put(taCode, taObj);
									}
								}
							}
						}
					TaMap.lock();
					bussinessStatus=Status.ListLoaded;
					AppUtils.logService(ServiceName, "�е�˫�򶨵������.�����ļ��б�["+cfgPath+"]��ɡ���",workDate);
				}
				catch(Exception e){
					AppUtils.logService(ServiceName, "�е�˫�򶨵������.�����ļ��б�["+cfgPath+"]�쳣:"+e.toString(),workDate);
					bussinessStatus=Status.ListLoadedError;
				}
		 	}
	    }
	 
	 private void fileCheck(String workDate,String fileBaseDir){
		 	if(bussinessStatus.equals(Status.ListLoaded)||bussinessStatus.equals(Status.FileLack)){
		 		  AppUtils.logService(ServiceName, "�е�˫�򶨵������.��ʼʵ���ļ���⡭��",workDate);
		 		 currentStep="fileCheck";
				  if(TaMap.taMap!=null&&TaMap.taMap.size()>0){
					  for(String ta:TaMap.taMap.keySet()){
						 if(AppContext.getInstance().runable()){
							  TaItem taObj=TaMap.taMap.get(ta);
							  String sendPath=fileBaseDir+File.separator+taObj.getSendPath();
							  sendPath=AppUtils.formatPath(sendPath);
							  File sendDir=new File(sendPath);
							  File[] dataFiles=sendDir.listFiles(new FileFilter() {
								  @Override
									public boolean accept(File file){
										  if(file.isFile()&&(!file.getName().toUpperCase().endsWith(".OK"))){
											  return true;
										  }
										  return false;
									  }
								});
							if(dataFiles!=null&&dataFiles.length>0){  
								for(int i=0;i<dataFiles.length;i++){
									String fileName=dataFiles[i].getName();
									taObj.setFileOk(fileName,dataFiles[i].getAbsolutePath());
								}
							 }
							TaMap.taMap.put(ta, taObj) ; 
						  }
					  }
				  }
				  this.TaMap.isAllFilesOk();
				  if(!this.TaMap.allFileChecked){
					  this.bussinessStatus=Status.FileLack;
					  AppUtils.logError("taFileCheck������.�ļ�����.ȱ���ļ�:"+this.TaMap.getFileInof()) ;
					  AppUtils.logService(ServiceName, "�е�˫�򶨵������.�ļ�����.ȱ���ļ�:"+this.TaMap.getFileInof(),workDate) ;
					  for(FileItem file:this.TaMap.getMistFiles()){
						  AppUtils.logError("taFileCheck������.�ļ�����.����˾["+file.getTaName()+"]Ŀ¼["+file.getSendPath()+"]���ļ�["+file.getName()+"]ȱʧ") ;
						  AppUtils.logService(ServiceName, "�е�˫�򶨵������.�ļ�����.����˾["+file.getTaName()+"]Ŀ¼["+file.getSendPath()+"]���ļ�["+file.getName()+"]ȱʧ",workDate) ;
					  }
				  }else{
					  this.bussinessStatus=Status.AllFileExists;
					  AppUtils.logService(ServiceName, "�е�˫�򶨵������.ʵ���ļ����ɹ�ͨ������",workDate);
				  }
		 	}
	 }
	 
	 private void makeOkFiles(String workDate,String fileBaseDir){
	  	   if(bussinessStatus.equals(Status.AllFileExists)||bussinessStatus.equals(Status.OkFilesError)){
	  		   		AppUtils.logService(ServiceName, "�е�˫�򶨵������.ʵ���ļ���������,�ļ�����:["+this.TaMap.fileCount+"]��ʼ����.ok�ļ�",workDate);
		  		   	currentStep="makeOkFiles";
			   		//�������ļ��Ƿ�
			     	this.doneFileList="";
			   		this.loadProgressFile();
	  		   		boolean error=false;
		    		 for(String ta:this.TaMap.taMap.keySet()){
		    			  if(AppContext.getInstance().runable()){
				    			 TaItem taObj=this.TaMap.taMap.get(ta);
				    			 if(this.TaMap.taMap.get(ta).getFiles()!=null&&this.TaMap.taMap.get(ta).getFiles().size()>0){
				      			   String sendPath=fileBaseDir+File.separator+taObj.getSendPath();
								   sendPath=AppUtils.formatPath(sendPath);
				      			   for(FileItem file:this.TaMap.taMap.get(ta).getFiles()){
				      				   String fileName=file.getName();
				      				   String okFileName=fileName+".ok";
				      				 if(this.breakWorkDate.equals(workDate)
				      						   &&this.doneFileList.indexOf(okFileName)!=-1){
				      					   	   AppUtils.logService(ServiceName, "FDEP���������.�ļ�����.��������˾["+taObj.getTaName()+"]ok�ļ�:"+sendPath+File.separator+okFileName+"֮ǰ�Ѿ����ɣ������������ظ�����(����)",workDate); 
				      				   }else{
											try{
												String content="OkFlag";
												String path=sendPath+File.separator+okFileName;
												this.writeFile(path, content);
												AppUtils.logService(ServiceName, "�е�˫�򶨵������.�ļ�����.��������˾["+taObj.getTaName()+"]ok�ļ�:"+sendPath+File.separator+okFileName+"�ɹ����",workDate);
												this.doneFileList+=okFileName+",";
												 this.progressToFile();
											}catch(Exception e){
												AppUtils.logError("taFileCheck������.�ļ�����.��������˾["+taObj.getTaName()+"]ok�ļ�:"+sendPath+File.separator+okFileName+"�쳣��"+e.toString());
												AppUtils.logService(ServiceName, "�е�˫�򶨵������.�ļ�����.��������˾["+taObj.getTaName()+"]ok�ļ�:"+sendPath+File.separator+okFileName+"�쳣��"+e.toString(),workDate);
												error=true;
											}
				      				   }
				      			   }
				      		    }
		    			  }
		    		 }
		    		 if(!error){
		    			 this.bussinessStatus=Status.OkFilesSuccess;
		    			 AppUtils.logService(ServiceName, "�е�˫�򶨵������.����ok�ļ��ɹ���ɡ���",workDate);
		    			 this.progressToFile();
		    		 }else{
		    			 this.bussinessStatus=Status.OkFilesError;
		    			 AppUtils.logService(ServiceName, "�е�˫�򶨵������.����ok�ļ��������󡭡�",workDate);
		    		 }
	  	   		}
	     }
	 
	 
	  private void checkSendLog(String workDate){
	  	   if(bussinessStatus.equals(Status.OkFilesSuccess)||bussinessStatus.equals(Status.FileSendError)){
						AppUtils.logService(ServiceName, "�е�˫�򶨵������.�ļ�����.��־��⿪ʼ����",workDate);
						currentStep="logCheck";  	
						boolean error=false;
							 File logFile=this.getLogFile(workDate);
							  if(logFile!=null){
							  List<String> lines=AppUtils.getFileLines(logFile);
							  for(String line:lines){
									  if(AppContext.getInstance().runable()){
										  for(String ta:this.TaMap.taMap.keySet()){
											  	TaItem taObj=this.TaMap.taMap.get(ta);
											    for(FileItem file:taObj.getFiles()){
											    	if(file.isFileOk()&&!file.isFileSend()){
											    	   	 boolean logExist=this.logCheck(line, file.getName());
											    	   	 if(logExist){
											    	   		AppUtils.logService(ServiceName, "�е�˫�򶨵������.�ļ�����.��־["+logFile.getAbsolutePath()+"]�м�⵽�ļ�["+file.getFullPath()+"]���ͳɹ���������־��"+line,workDate);
											    	   		taObj.setFileSend(file.getName());
											    	   	 }
											    	}
											    }
											    this.TaMap.taMap.put(ta, taObj) ; 
										  }
									  }
								  }
								  for(String ta:this.TaMap.taMap.keySet()){
									  if(AppContext.getInstance().runable()){
									  	TaItem taObj=this.TaMap.taMap.get(ta);
									    for(FileItem file:taObj.getFiles()){
									    	if(!file.isFileSend()){
									    		AppUtils.logError("taFileCheck������.�ļ�����.��ʱ�ȴ���δ����־["+logFile.getAbsolutePath()+"]��ʶ������˾["+taObj.getTaName()+"]"+"�ļ�[��"+file.getFullPath()+"]");
									    		AppUtils.logService(ServiceName, "�е�˫�򶨵������.�ļ�����.��ʱ�ȴ���δ����־["+logFile.getAbsolutePath()+"]��ʶ������˾["+taObj.getTaName()+"]"+"�ļ�[��"+file.getFullPath()+"]",workDate);
									    		error=true;
									    	}
									    }
									  }
								  }
							  }
						  if(!error){
							  this.bussinessStatus=Status.AllFileSend;
							  AppUtils.logService(ServiceName, "�е�˫�򶨵������.�ļ�����.��־���ɹ�ͨ��"+this.TaMap.getFileCheckInfo(),workDate);
						  }else{
							  this.bussinessStatus=Status.FileSendError;
							  AppUtils.logService(ServiceName, "�е�˫�򶨵������.�ļ�����.��־������쳣���������־����",workDate);
						  }
					}
	     }
	     
	  
	  	//ȡ��־Ŀ¼�µ������־
	 	private File getLogFile(String workDate){
	 		String fileDay=workDate.substring(4);
			File logFile=null;
			String logDir=AppUtils.formatPath(logPath);
			File dir=new File(logDir);
			if(dir.exists()&&dir.isDirectory()){
				File[] files=dir.listFiles();
				if(files!=null&&files.length>0){
					for(File file:files){
						if(file.getName().indexOf(fileDay)!=-1){
							logFile=file;
							return logFile;
						}
					}
				}
			}
			return logFile;
		}
	 	
		public static Pattern pattern_globe=null;
		public static Matcher matcher_globe=null;
		//public static String regexFile="^.*${FileName}.*�������.*$";
		//������־�ı��м���ļ��Ƿ�ɹ����͵�Сվ
		  private boolean logCheck(String line,String fileName){	
			  String regex=checkRules.replace("${FileName}", fileName);
			  pattern_globe=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
	 		  matcher_globe=pattern_globe.matcher(line);
			  if(matcher_globe.find())	{ 
					return true;
			  }	
			  return false;
		  }
		  
		  public   void writeFile(String path,String content){
		    	try {
					File file = new File(path);
					FileWriter fw = new FileWriter(file);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(content);
					bw.close();
					fw.close();
				} catch (Exception e) {
					AppUtils.logError("�����ļ�"+path+"�쳣:"+e.toString());
				}
		    }
		  
	 	
	    public ActionState status;
		public JService Service;//�����߳�
		private boolean runAble;//
	    public void wakeUp(JService service){
			String currentDate=AppUtils.getCurrentDate("yyyyMMdd");
			if(status.equals(ActionState.Init)){
				status=ActionState.Running;
				this.bussinessStatus=Status.Ready;
				this.Service=service;
				this.runAble=true;
				AppUtils.logInof("����["+this.Service.getName()+"]��ʼ�������С��������ڣ�"+this.Service.getWorkDate());
				//�����߳�
				this.start();
			}else if(status.equals(ActionState.Idel)){
				//�ж��Ƿ�Ϊ������
				if(DateUtilis.isWorkDay(currentDate,offDays)){
					//�ж��Ƿ���ڷ���Ĺ�������
					if(currentDate.compareTo(service.getWorkDate())>=0){
						this.Service=service;
						status=ActionState.Running;
						this.bussinessStatus=Status.Ready;
						AppUtils.logInof("����["+this.Service.getName()+"]��ʼ�������С��������ڣ�"+this.Service.getWorkDate());
						this.setRunAble(true);
					}
				}
			}else{
				this.doNothing();
			}
		}
	 
	    public void doNothing(){}
	    
		public void run(){
			try{
				  while(true){
					  if(!this.runAble){
						  try {
							this.sleep(sleepCycle);
						} catch (InterruptedException e) {
							AppUtils.logInof("����["+this.Service.getName()+"]�ⲿ�ж� ˯���쳣��"+e.toString() );
						}
					  }else{
						  this.fileProcess(Service);
					  }
				  }
			}catch(Exception e){
				e.printStackTrace();
			}
		}   
	    
		private  void setRunAble(boolean flag) {
			this.runAble = flag;
		}	
		
		public void exit(){
			try{
				status=ActionState.Idel;
				this.setRunAble(false);
				this.progressToFile();
				this.interrupt();
				AppUtils.logInof("����["+this.Service.getName()+"]�ⲿ�жϡ���ǰ�������ڣ�"+this.Service.getWorkDate());
			}catch(Exception e){
				AppUtils.logInof("����["+this.Service.getName()+"]�ⲿ�ж� �����쳣��"+e.toString() );
			}
		}
		
		private void progressToFile(){
			String path=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"ini"+File.separator+this.Service.getId()+".progress";
			try {
				   File file=new File(path);
				   FileWriter fw = new FileWriter(file,false);
				   BufferedWriter bw = new BufferedWriter(fw);
				   String workDateInfo="workDate="+this.Service.getWorkDate();
				   bw.write(workDateInfo);
				   bw.write("\r\n");
				   String stepInfo="breakStep="+this.currentStep;
				   bw.write(stepInfo);
				   bw.write("\r\n");
				   String doneFileListInfo="doneFileList="+this.doneFileList;
				   bw.write(doneFileListInfo);
				   bw.write("\r\n");
				   bw.close();
				   fw.close();
			}catch (Exception e) {
				e.printStackTrace();
	        }
		}
		
		private void loadProgressFile(){
			 try{
				   String path=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"ini"+File.separator+this.Service.getId()+".progress";
				    FileReader fr = new FileReader(new File(path));
					BufferedReader br = new BufferedReader(fr);
					String line = "";
					while ((line = br.readLine()) != null) {
		               String key=line.split("\\=")[0];
		               String value=line.split("\\=")[1];
		               if("workDate".equals(key.trim())){
		            	  this.breakWorkDate=value;
		               }else if("breakStep".equals(key.trim())){
		            	   this.breakStep=value;
		               }else if("doneFileList".equals(key.trim())){
		            	   this.doneFileList=value;
		               }
					}
					fr.close();
					br.close();
			 }catch(Exception e){
				 
			 }
		}
		
}
