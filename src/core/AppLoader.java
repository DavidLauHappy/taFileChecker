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
								AppUtils.logInof("�ļ�["+cfgpath+"]������������Ϣȱ�ٱ�Ҫ����id�����񱻺���");//��¼��Ϣ
								continue;
							}
							Attribute attrName= ele.getAttribute("name");
							if(attrName!=null){
								Service.setName(attrName.getValue());
							}else{
								AppUtils.logInof("�ļ�["+cfgpath+"]������������Ϣȱ�ٱ�Ҫ����name�����񱻺���");//��¼��Ϣ
								continue;
							}
							Attribute attrCheckTime= ele.getAttribute("checkTime");
							if(attrCheckTime!=null){
								Service.setStartCheckTime(attrCheckTime.getValue());
							}else{
								AppUtils.logInof("�ļ�["+cfgpath+"]������������Ϣȱ�ٱ�Ҫ����checkTime�����񱻺���");//��¼��Ϣ
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
								AppUtils.logInof("�ļ�["+cfgpath+"]������������Ϣȱ�ٱ�Ҫ����classID�����񱻺���");//��¼��Ϣ
								continue;
							}
							Attribute attrConfig= ele.getAttribute("config");
							if(attrConfig!=null){
								Service.setConfig(attrConfig.getValue());
							}else{
								AppUtils.logInof("�ļ�["+cfgpath+"]������������Ϣȱ�ٱ�Ҫ����config�����񱻺���");//��¼��Ϣ
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
	  
	   
	    
	   //���ɴ����嵥�ļ���������
	  /*  public static void main(String[] args){
	    	
	    }*/
	    
	   
	    public static final Map<String,String> TaCodes=new HashMap<String, String>();
	    {
	    	TaCodes.put("GXSUBTA", "93");
	    }
	    //  select 'TaCmps.put('+char(34)+name+char(34)+','+char(34)+code+char(34)+');' from(select distinct taname name,tacodesno code from dbo.ofta)t;
	    public static final Map<String,String> TaCmps=new HashMap<String, String>();
	    {
	    	TaCmps.put("75","���Ż���");
	    	TaCmps.put("13","��ӯ����");
	    	TaCmps.put("05","��ʱ����");
	    	TaCmps.put("72","��ͨ����");
	    	TaCmps.put("74","��������");
	    	TaCmps.put("20","���ǻ���");
	    	TaCmps.put("08","��ʢ����");
	    	TaCmps.put("30","���Ż���");
	    	TaCmps.put("3M","�������");
	    	TaCmps.put("09","��ɻ���");
	    	TaCmps.put("40","��������");
	    	TaCmps.put("58","�������");
	    	TaCmps.put("73","�����������");
	    	TaCmps.put("71","���������");
	    	TaCmps.put("10","��������");
	    	TaCmps.put("48","��������");
	    	TaCmps.put("36","��󱣵�");
	    	TaCmps.put("27","�㷢����");
	    	TaCmps.put("45","������������");
	    	TaCmps.put("76","����ͨ��");
	    	TaCmps.put("25","����������");
	    	TaCmps.put("0Z","���ٰ���");
	    	TaCmps.put("02","��̩����");
	    	TaCmps.put("1A","��̩Ԫ��");
	    	TaCmps.put("12","��Ͷ����");
	    	TaCmps.put("GXSUBTA","���ŷ�TA");
	    	TaCmps.put("93","���ż���");
	    	TaCmps.put("S2","�����й�");
	    	TaCmps.put("23","����ͨ");
	    	TaCmps.put("78","��������");
	    	TaCmps.put("3H","��������");
	    	TaCmps.put("04","��������");
	    	TaCmps.put("24","������ҵ");
	    	TaCmps.put("41","��������");
	    	TaCmps.put("0A","����Ԫ��");
	    	TaCmps.put("63","����");
	    	TaCmps.put("46","��̩����");
	    	TaCmps.put("03","���Ļ���");
	    	TaCmps.put("54","�����Ż���");
	    	TaCmps.put("47","��������");
	    	TaCmps.put("07","��ʵ����");
	    	TaCmps.put("53","���Ż���");
	    	TaCmps.put("49","����ʩ�޵»���");
	    	TaCmps.put("21","��ӥ����");
	    	TaCmps.put("26","��˳����");
	    	TaCmps.put("3Q","��̩����");
	    	TaCmps.put("69","��������");
	    	TaCmps.put("33","Ħ������");
	    	TaCmps.put("01","�Ϸ�����");
	    	TaCmps.put("66","ũ������");
	    	TaCmps.put("32","ŵ������");
	    	TaCmps.put("57","ŵ�»���");
	    	TaCmps.put("06","��������");
	    	TaCmps.put("70","ƽ����");
	    	TaCmps.put("44","ǰ����Դ");
	    	TaCmps.put("16","��ͨ����");
	    	TaCmps.put("37","��ͶĦ������");
	    	TaCmps.put("31","��������");
	    	TaCmps.put("98","���ڵǼ�");
	    	TaCmps.put("22","̩�����");
	    	TaCmps.put("29","̩�Ż���");
	    	TaCmps.put("42","������");
	    	TaCmps.put("35","���λ���");
	    	TaCmps.put("67","��������");
	    	TaCmps.put("43","�»�����");
	    	TaCmps.put("55","�ųϻ���");
	    	TaCmps.put("61","�Ŵ��������");
	    	TaCmps.put("34","��ȫ����");
	    	TaCmps.put("11","�׷������");
	    	TaCmps.put("56","�������");
	    	TaCmps.put("15","���ӻ���");
	    	TaCmps.put("18","��������");
	    	TaCmps.put("1L","Բ������");
	    	TaCmps.put("17","���̻���");
	    	TaCmps.put("68","���̻���");
	    	TaCmps.put("99","�е��Ϻ�");
	    	TaCmps.put("39","�к�����");
	    	TaCmps.put("1N","�н����");
	    	TaCmps.put("4L","�п�����");
	    	TaCmps.put("60","��ŷ����");
	    	TaCmps.put("0F","���ڻ���");
	    	TaCmps.put("1F","������ӯ");
	    	TaCmps.put("59","���ʻ���");
	    	TaCmps.put("4D","���»���");
	    }
	    
	    //parse the ����ӿ�.xml to the fileList.xml
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
							System.err.println("�޳�������˾��"+taName);
						}
					}
				}
				//д�뵽�����ļ�
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
