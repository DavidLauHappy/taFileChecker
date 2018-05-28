package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {
	
	public static   String formatPath(String path){
		  if (path != null && !"".equals(path)) {
				path = path.replace('/', File.separatorChar);
				path = path.replace('\\', File.separatorChar);
				while (path.endsWith(String.valueOf(File.separatorChar))) {
					path = path.substring(0, path.length() - 1);
				}
			} else {
				return "";
			}
			return path;
		}
	
	 public static boolean isNullOrEmpty(String str){
	   	  if( str!=null&&!"".equals(str.trim()))
	   		  return false;
	   	  return true;
     }
	 
	 public static String getCurrentDate(String form){
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat(form);
			String staticsDate= format.format(calendar.getTime());
			return staticsDate;
		}
	 
	   public static int getCurrentWeekDay(){
			Calendar calendar = Calendar.getInstance();
			int w=calendar.get(Calendar.DAY_OF_WEEK)-1;
			if(w<=0){
				w=7;
			}
			return w;
	   }
	   
	   public static int getCurrentWeekDay(Date date){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int w=calendar.get(Calendar.DAY_OF_WEEK)-1;
			if(w<=0){
				w=7;
			}
			return w;
	   }
	 
	 public static List<String> getFileLines(File file){
		 List<String> result=new ArrayList<String>();
		 try{
			 FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = "";
				while ((line = br.readLine()) != null) {
					result.add(line);
				}
				fr.close();
				br.close();
		 }catch(Exception e){
			 AppUtils.logError("taFileCheck读取日志文件发送异常(可能是日志文件太大)："+e.toString());
		 }
		 return result;
	 }
	 

    
/*    public static boolean check(String line,String regex){
		pattern_globe=Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
	   	 matcher_globe=pattern_globe.matcher(line);
		  if(!matcher_globe.find())	{ 
				return false;
		  }
    	return true;
   }*/
    
    /*public static void main(String[] args){
    	String fileName="OFI_603_21_20171207.TXT";
    	 String rule="${FileName}\\.ACC";
    	 System.out.println(rule);
    	 String regex="^.*OFD_603_0A_20171208_01.TXT\\.ACC.+$";//rule.replace("${FileName}", fileName);
    	 System.out.println(regex);
    	 String line="<20171208 173857.355,FxTermPkgDea,2395,Ecd=0|FDEP|Tdf=0,tid=4336>0: Add task [ID=120817010681000000ZDCTP1c8affdc9], File=[OFD_603_0A_20171208_01.TXT.ACC], Source UserID=[#HUB#], Dest UserID=[k0101(k0101)]";
        boolean result=check(line,regex);
        System.out.println(result);
    }*/
    
	 public static void logInof(String info){
			try {
				String logDir=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"log";
			    File dir=new File(logDir);
			    if(!dir.exists())
			    	dir.mkdirs();
				   String logpath=logDir+File.separator+AppUtils.getCurrentDate("yyyyMMdd")+".log";
				   String currentTime=AppUtils.getCurrentDate("yyyyMMdd-HH:mm:ss.SSS");
				   File file=new File(logpath);
				   FileWriter fw = new FileWriter(file,true);
				   BufferedWriter bw = new BufferedWriter(fw);
				   bw.write(currentTime);
				   bw.write(" : ");
				   bw.write(info);
				   bw.write("\r\n");
				   bw.close();
				   fw.close();
		           }
			catch (Exception e) {
				e.printStackTrace();
	        }
	 }
	 
	 public static void logService(String serviceName,String info,String workDate){
			try {
				String logDir=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"log";
			    File dir=new File(logDir);
			    if(!dir.exists())
			    	dir.mkdirs();
				   String logpath=logDir+File.separator+serviceName+"_"+workDate+".log";
				   String currentTime=AppUtils.getCurrentDate("yyyyMMdd-HH:mm:ss.SSS");
				   File file=new File(logpath);
				   FileWriter fw = new FileWriter(file,true);
				   BufferedWriter bw = new BufferedWriter(fw);
				   bw.write(currentTime);
				   bw.write(" : ");
				   bw.write(info);
				   bw.write("\r\n");
				   bw.close();
				   fw.close();
		           }
			catch (Exception e) {
				e.printStackTrace();
	        }
	 }
	 
	 public static synchronized void logError(String info){
		 try {
				String logDir=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"log";
			    File dir=new File(logDir);
			    if(!dir.exists())
			    	dir.mkdirs();
				   String logpath=logDir+File.separator+AppUtils.getCurrentDate("yyyyMMdd")+".err";
				   String currentTime=AppUtils.getCurrentDate("yyyyMMdd-HH:mm:ss.SSS");
				   File file=new File(logpath);
				   FileWriter fw = new FileWriter(file,true);
				   BufferedWriter bw = new BufferedWriter(fw);
				   bw.write(currentTime);
				   bw.write(" : ");
				   bw.write(info);
				   bw.write("\r\n");
				   bw.close();
				   fw.close();
		           }
			catch (Exception e) {
				e.printStackTrace();
	        }
	 }
}
