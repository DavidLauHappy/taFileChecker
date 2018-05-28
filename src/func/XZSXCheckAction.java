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
	public String currentStep="";//当前服务的中断步骤
	public String breakStep="";//当前服务的中断步骤
	public String doneFileList="";//当前服务处理完成的OK文件个数
	public String breakWorkDate="";//中断时的工作日期
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
					AppUtils.logInof("服务["+this.Service.getName()+"]外部中断 睡眠异常："+e.toString() );
				}
				sleepCycle=millSecond;
			}else{
				this.loadFileList(service.getWorkDate(),service.getConfig());//加载数据配置文件
				this.fileCheck(service.getWorkDate(),fileBaseDir);
				this.makeOkFiles(service.getWorkDate(),fileBaseDir);
				//休眠一个时间段
				if(!AppUtils.isNullOrEmpty(this.sendTimeout)){
					int sleepCnt=Integer.parseInt(this.sendTimeout);
					try {
						this.sleep(sleepCnt*1000L);
					} catch (InterruptedException e) {
						AppUtils.logInof("服务["+this.Service.getName()+"]外部中断 睡眠异常："+e.toString() );
					}
				}
				this.checkSendLog(service.getWorkDate());
				if(this.bussinessStatus.equals(Status.AllFileSend)){
					//当日任务完成切换日期
					String nextWorkDate=DateUtilis.getNextWorkDate(service.getWorkDate(),offDays);
					service.setWorkDate(nextWorkDate);
					AppContext.getInstance().setService(service);
					//设置休眠状态
					status=ActionState.Idel;
					this.setRunAble(false);
					 currentStep="StepDone";
					AppUtils.logInof("服务["+this.Service.getName()+"]进入休眠等待,工作日期切换到:"+this.Service.getWorkDate());
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
		 		AppUtils.logService(ServiceName, "中登双向定点检测服务.启动"+workDate+"检测工作加载清单文件……",workDate);
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
					AppUtils.logService(ServiceName, "中登双向定点检测服务.加载文件列表["+cfgPath+"]完成……",workDate);
				}
				catch(Exception e){
					AppUtils.logService(ServiceName, "中登双向定点检测服务.加载文件列表["+cfgPath+"]异常:"+e.toString(),workDate);
					bussinessStatus=Status.ListLoadedError;
				}
		 	}
	    }
	 
	 private void fileCheck(String workDate,String fileBaseDir){
		 	if(bussinessStatus.equals(Status.ListLoaded)||bussinessStatus.equals(Status.FileLack)){
		 		  AppUtils.logService(ServiceName, "中登双向定点检测服务.开始实体文件检测……",workDate);
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
					  AppUtils.logError("taFileCheck定点检测.文件到达.缺少文件:"+this.TaMap.getFileInof()) ;
					  AppUtils.logService(ServiceName, "中登双向定点检测服务.文件到达.缺少文件:"+this.TaMap.getFileInof(),workDate) ;
					  for(FileItem file:this.TaMap.getMistFiles()){
						  AppUtils.logError("taFileCheck定点检测.文件到达.基金公司["+file.getTaName()+"]目录["+file.getSendPath()+"]下文件["+file.getName()+"]缺失") ;
						  AppUtils.logService(ServiceName, "中登双向定点检测服务.文件到达.基金公司["+file.getTaName()+"]目录["+file.getSendPath()+"]下文件["+file.getName()+"]缺失",workDate) ;
					  }
				  }else{
					  this.bussinessStatus=Status.AllFileExists;
					  AppUtils.logService(ServiceName, "中登双向定点检测服务.实体文件检测成功通过……",workDate);
				  }
		 	}
	 }
	 
	 private void makeOkFiles(String workDate,String fileBaseDir){
	  	   if(bussinessStatus.equals(Status.AllFileExists)||bussinessStatus.equals(Status.OkFilesError)){
	  		   		AppUtils.logService(ServiceName, "中登双向定点检测服务.实体文件正常到达,文件总数:["+this.TaMap.fileCount+"]开始生成.ok文件",workDate);
		  		   	currentStep="makeOkFiles";
			   		//检查进度文件是否
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
				      					   	   AppUtils.logService(ServiceName, "FDEP定点检测服务.文件到达.创建基金公司["+taObj.getTaName()+"]ok文件:"+sendPath+File.separator+okFileName+"之前已经生成，本次续作不重复生成(跳过)",workDate); 
				      				   }else{
											try{
												String content="OkFlag";
												String path=sendPath+File.separator+okFileName;
												this.writeFile(path, content);
												AppUtils.logService(ServiceName, "中登双向定点检测服务.文件到达.创建基金公司["+taObj.getTaName()+"]ok文件:"+sendPath+File.separator+okFileName+"成功完成",workDate);
												this.doneFileList+=okFileName+",";
												 this.progressToFile();
											}catch(Exception e){
												AppUtils.logError("taFileCheck定点检测.文件到达.创建基金公司["+taObj.getTaName()+"]ok文件:"+sendPath+File.separator+okFileName+"异常："+e.toString());
												AppUtils.logService(ServiceName, "中登双向定点检测服务.文件到达.创建基金公司["+taObj.getTaName()+"]ok文件:"+sendPath+File.separator+okFileName+"异常："+e.toString(),workDate);
												error=true;
											}
				      				   }
				      			   }
				      		    }
		    			  }
		    		 }
		    		 if(!error){
		    			 this.bussinessStatus=Status.OkFilesSuccess;
		    			 AppUtils.logService(ServiceName, "中登双向定点检测服务.创建ok文件成功完成……",workDate);
		    			 this.progressToFile();
		    		 }else{
		    			 this.bussinessStatus=Status.OkFilesError;
		    			 AppUtils.logService(ServiceName, "中登双向定点检测服务.创建ok文件发生错误……",workDate);
		    		 }
	  	   		}
	     }
	 
	 
	  private void checkSendLog(String workDate){
	  	   if(bussinessStatus.equals(Status.OkFilesSuccess)||bussinessStatus.equals(Status.FileSendError)){
						AppUtils.logService(ServiceName, "中登双向定点检测服务.文件发送.日志检测开始……",workDate);
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
											    	   		AppUtils.logService(ServiceName, "中登双向定点检测服务.文件发送.日志["+logFile.getAbsolutePath()+"]中检测到文件["+file.getFullPath()+"]发送成功。关联日志："+line,workDate);
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
									    		AppUtils.logError("taFileCheck定点检测.文件发送.超时等待后未在日志["+logFile.getAbsolutePath()+"]中识到基金公司["+taObj.getTaName()+"]"+"文件[："+file.getFullPath()+"]");
									    		AppUtils.logService(ServiceName, "中登双向定点检测服务.文件发送.超时等待后未在日志["+logFile.getAbsolutePath()+"]中识到基金公司["+taObj.getTaName()+"]"+"文件[："+file.getFullPath()+"]",workDate);
									    		error=true;
									    	}
									    }
									  }
								  }
							  }
						  if(!error){
							  this.bussinessStatus=Status.AllFileSend;
							  AppUtils.logService(ServiceName, "中登双向定点检测服务.文件发送.日志检测成功通过"+this.TaMap.getFileCheckInfo(),workDate);
						  }else{
							  this.bussinessStatus=Status.FileSendError;
							  AppUtils.logService(ServiceName, "中登双向定点检测服务.文件发送.日志检测有异常详见错误日志……",workDate);
						  }
					}
	     }
	     
	  
	  	//取日志目录下当天的日志
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
		//public static String regexFile="^.*${FileName}.*接收完毕.*$";
		//根据日志文本行监控文件是否成功上送到小站
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
					AppUtils.logError("生成文件"+path+"异常:"+e.toString());
				}
		    }
		  
	 	
	    public ActionState status;
		public JService Service;//服务线程
		private boolean runAble;//
	    public void wakeUp(JService service){
			String currentDate=AppUtils.getCurrentDate("yyyyMMdd");
			if(status.equals(ActionState.Init)){
				status=ActionState.Running;
				this.bussinessStatus=Status.Ready;
				this.Service=service;
				this.runAble=true;
				AppUtils.logInof("服务["+this.Service.getName()+"]开始调度运行。工作日期："+this.Service.getWorkDate());
				//启动线程
				this.start();
			}else if(status.equals(ActionState.Idel)){
				//判断是否为工作日
				if(DateUtilis.isWorkDay(currentDate,offDays)){
					//判断是否大于服务的工作日期
					if(currentDate.compareTo(service.getWorkDate())>=0){
						this.Service=service;
						status=ActionState.Running;
						this.bussinessStatus=Status.Ready;
						AppUtils.logInof("服务["+this.Service.getName()+"]开始调度运行。工作日期："+this.Service.getWorkDate());
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
							AppUtils.logInof("服务["+this.Service.getName()+"]外部中断 睡眠异常："+e.toString() );
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
				AppUtils.logInof("服务["+this.Service.getName()+"]外部中断。当前工作日期："+this.Service.getWorkDate());
			}catch(Exception e){
				AppUtils.logInof("服务["+this.Service.getName()+"]外部中断 发生异常："+e.toString() );
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
