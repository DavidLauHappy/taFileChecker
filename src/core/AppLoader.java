package core;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class AppLoader {
	  public AppLoader(){ }
	  //self-configuration will be loaded only when it start
	  public void loadCfgs(){
		  this.loadCfg();
	  }
	  
	  private void loadCfg(){
		    SAXBuilder builder = new SAXBuilder();
			Document doc;
			String cfgpath="";
			try{
					cfgpath=AppUtils.formatPath(AppContext.getInstance().appPath)+File.separator+"ini"+File.separator+"start.xml";
					doc = builder.build(cfgpath);
					Element configs=doc.getRootElement();
					List<Element> servicesElements= configs.getChildren();
					if(servicesElements!=null&&servicesElements.size()>0){
						for(Element ele:servicesElements){
							JService Service=new JService();
							Attribute attrID= ele.getAttribute("id");
							if(attrID!=null){
								Service.setId(attrID.getValue());
							}else{
								AppUtils.logInof("文件["+cfgpath+"]服务定义配置信息缺少必要属性id，服务被忽略");//记录信息
								continue;
							}
							Attribute attrName= ele.getAttribute("name");
							if(attrName!=null){
								Service.setName(attrName.getValue());
							}else{
								AppUtils.logInof("文件["+cfgpath+"]服务定义配置信息缺少必要属性name，服务被忽略");//记录信息
								continue;
							}
							Attribute attrCheckTime= ele.getAttribute("checkTime");
							if(attrCheckTime!=null){
								Service.setStartCheckTime(attrCheckTime.getValue());
							}else{
								AppUtils.logInof("文件["+cfgpath+"]服务定义配置信息缺少必要属性checkTime，服务被忽略");//记录信息
								continue;
							}
							Attribute attrWorkDate= ele.getAttribute("workdate");
							if(attrWorkDate!=null&&!AppUtils.isNullOrEmpty(attrWorkDate.getValue())){
								Service.setWorkDate(attrWorkDate.getValue());
							}else{
								String currentDate=AppUtils.getCurrentDate("yyyyMMdd");
								Service.setWorkDate(currentDate);
							}
							Attribute attrClassID= ele.getAttribute("classID");
							if(attrClassID!=null){
								Service.setClassURL(attrClassID.getValue());
							}else{
								AppUtils.logInof("文件["+cfgpath+"]服务定义配置信息缺少必要属性classID，服务被忽略");//记录信息
								continue;
							}
							Attribute attrConfig= ele.getAttribute("config");
							if(attrConfig!=null){
								Service.setConfig(attrConfig.getValue());
							}else{
								AppUtils.logInof("文件["+cfgpath+"]服务定义配置信息缺少必要属性config，服务被忽略");//记录信息
								continue;
							}
							List<Element>  childrens=ele.getChildren();
							Map<String,String> parameters=new HashMap<String, String>();
							if(childrens!=null&&childrens.size()>0){
								for(Element child:childrens){
									parameters.put(child.getName(), child.getValue());
								}
							}
							Service.setParameters(parameters);
							AppContext.getInstance().setService(Service);
						}
					}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	  
	   
	    
	   //生成存量清单文件的主函数
	  /*  public static void main(String[] args){
	    	
	    }*/
	    
	   
	    public static final Map<String,String> TaCodes=new HashMap<String, String>();
	    {
	    	TaCodes.put("GXSUBTA", "93");
	    }
	    //  select 'TaCmps.put('+char(34)+name+char(34)+','+char(34)+code+char(34)+');' from(select distinct taname name,tacodesno code from dbo.ofta)t;
	    public static final Map<String,String> TaCmps=new HashMap<String, String>();
	    {
	    	TaCmps.put("75","安信基金");
	    	TaCmps.put("13","宝盈基金");
	    	TaCmps.put("05","博时基金");
	    	TaCmps.put("72","财通基金");
	    	TaCmps.put("74","长安基金");
	    	TaCmps.put("20","长城基金");
	    	TaCmps.put("08","长盛基金");
	    	TaCmps.put("30","长信基金");
	    	TaCmps.put("3M","创金合信");
	    	TaCmps.put("09","大成基金");
	    	TaCmps.put("40","东方基金");
	    	TaCmps.put("58","东吴基金");
	    	TaCmps.put("73","方正富邦基金");
	    	TaCmps.put("71","富安达基金");
	    	TaCmps.put("10","富国基金");
	    	TaCmps.put("48","工银瑞信");
	    	TaCmps.put("36","光大保德");
	    	TaCmps.put("27","广发基金");
	    	TaCmps.put("45","国海富兰克林");
	    	TaCmps.put("76","国金通用");
	    	TaCmps.put("25","国联安基金");
	    	TaCmps.put("0Z","国寿安保");
	    	TaCmps.put("02","国泰基金");
	    	TaCmps.put("1A","国泰元鑫");
	    	TaCmps.put("12","国投瑞银");
	    	TaCmps.put("GXSUBTA","国信分TA");
	    	TaCmps.put("93","国信集合");
	    	TaCmps.put("S2","国信托管");
	    	TaCmps.put("23","海富通");
	    	TaCmps.put("78","红塔红土");
	    	TaCmps.put("3H","红土创新");
	    	TaCmps.put("04","华安基金");
	    	TaCmps.put("24","华宝兴业");
	    	TaCmps.put("41","华富基金");
	    	TaCmps.put("0A","华润元大");
	    	TaCmps.put("63","华商");
	    	TaCmps.put("46","华泰柏瑞");
	    	TaCmps.put("03","华夏基金");
	    	TaCmps.put("54","汇丰晋信基金");
	    	TaCmps.put("47","汇添富基金");
	    	TaCmps.put("07","嘉实基金");
	    	TaCmps.put("53","建信基金");
	    	TaCmps.put("49","交银施罗德基金");
	    	TaCmps.put("21","金鹰基金");
	    	TaCmps.put("26","景顺长城");
	    	TaCmps.put("3Q","九泰基金");
	    	TaCmps.put("69","民生加银");
	    	TaCmps.put("33","摩根华鑫");
	    	TaCmps.put("01","南方基金");
	    	TaCmps.put("66","农银汇理");
	    	TaCmps.put("32","诺安基金");
	    	TaCmps.put("57","诺德基金");
	    	TaCmps.put("06","鹏华基金");
	    	TaCmps.put("70","平安大华");
	    	TaCmps.put("44","前海开源");
	    	TaCmps.put("16","融通基金");
	    	TaCmps.put("37","上投摩根基金");
	    	TaCmps.put("31","申万菱信");
	    	TaCmps.put("98","深圳登记");
	    	TaCmps.put("22","泰达宏利");
	    	TaCmps.put("29","泰信基金");
	    	TaCmps.put("42","天弘基金");
	    	TaCmps.put("35","天治基金");
	    	TaCmps.put("67","西部利得");
	    	TaCmps.put("43","新华基金");
	    	TaCmps.put("55","信诚基金");
	    	TaCmps.put("61","信达澳银基金");
	    	TaCmps.put("34","兴全基金");
	    	TaCmps.put("11","易方达基金");
	    	TaCmps.put("56","益民基金");
	    	TaCmps.put("15","银河基金");
	    	TaCmps.put("18","银华基金");
	    	TaCmps.put("1L","圆信永丰");
	    	TaCmps.put("17","招商基金");
	    	TaCmps.put("68","浙商基金");
	    	TaCmps.put("99","中登上海");
	    	TaCmps.put("39","中海基金");
	    	TaCmps.put("1N","中金基金");
	    	TaCmps.put("4L","中科沃土");
	    	TaCmps.put("60","中欧基金");
	    	TaCmps.put("0F","中融基金");
	    	TaCmps.put("1F","中铁宝盈");
	    	TaCmps.put("59","中邮基金");
	    	TaCmps.put("4D","泓德基金");
	    }
	    
	    //parse the 基金接口.xml to the fileList.xml
	    private void convert(){
	    	SAXBuilder builder = new SAXBuilder();
			Document doc;
			String cfgPath="";
			String workDate=AppUtils.getCurrentDate("yyyyMMdd");
			List<FileItem> filelist=new ArrayList<FileItem>();
			try{
				cfgPath="C:\\Users\\David\\workspace\\taFileCheck"+File.separator+"ini"+File.separator+"jjjk.xml";
				doc = builder.build(cfgPath);
				Element fileList=doc.getRootElement();
				List<Element> tas=fileList.getChildren("Ta");
				if(tas!=null&&tas.size()>0){
					for(Element ta:tas){
						String taCode=ta.getAttributeValue("TaCode");
						String rpttacode=taCode;
						if(TaCodes.containsKey(taCode)){
							rpttacode=TaCodes.get(taCode);
						}
						String taName=ta.getAttributeValue("TaName");
						if(TaCmps.containsKey(rpttacode)){
							Element send=ta.getChild("Send");
							String sendPath="";
							if(send!=null){
								sendPath=send.getAttributeValue("SendPath");
								List<Element> files=send.getChildren("OfDatFiles");
								if(files!=null&&files.size()>0){
									for(Element file:files){
										List<Element> fileLists=file.getChildren("OfDatFile");
										for(Element curFile:fileLists){
												String name=curFile.getAttributeValue("FileName");
												name=name.replace("dbtid", "#dbtid");
												name=name.replace("rpttacode", rpttacode);
												name=name.replace("yyyymmdd", "#yyyymmdd");
												name=name.replace("+", "");
												name=name.replace("'", "");
												name=name.replace(" ", "");
												String desc=curFile.getAttributeValue("Description");
												FileItem fileitem=new FileItem(taCode,taName,name,desc,sendPath);
												filelist.add(fileitem);
										}
									}
								}
							}
						}else{
							System.err.println("剔除掉基金公司："+taName);
						}
					}
				}
				//写入到配置文件
				if(filelist!=null&&filelist.size()>0){
					String listPath="C:\\Users\\David\\workspace\\taFileCheck"+File.separator+"ini"+File.separator+"fileList1.xml";;
					Element root=new Element("fileList");
					Document newDoc= new Document(root);  
					Element taEle=null;
					String currentTa="";
					for(FileItem fileitem:filelist){
						 if(AppUtils.isNullOrEmpty(currentTa)||!currentTa.equalsIgnoreCase(fileitem.getTaCode())){
							 	currentTa=fileitem.getTaCode();
							 	if(taEle!=null){
							 		root.addContent(taEle);
							 		taEle=null;
							 	}
							 	taEle=new  Element("Ta");
							 	taEle.setAttribute("TaCode", currentTa);
							 	taEle.setAttribute("TaName", fileitem.getTaName());
							 	String sendpath="";
							 	if(AppUtils.isNullOrEmpty(fileitem.getSendPath())){
							 		sendpath="";
							 	}else{
							 		sendpath=fileitem.getSendPath();
							 	}
							 	taEle.setAttribute("SendPath", sendpath);
						 }
						 Element fileEle=new  Element("sendFile");
						 fileEle.setAttribute("FileName", fileitem.getName());
						 fileEle.setAttribute("Description", fileitem.getDesc());
						 taEle.addContent(fileEle);
					}
					XMLOutputter XMLOut = new XMLOutputter();     
			        XMLOut.output(newDoc, new FileOutputStream(listPath));  
				}
			}catch(Exception e){
				e.printStackTrace();
			}
	    }
	    
}
