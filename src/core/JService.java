package core;

import java.util.Map;

/**
 * @author David Lau
 * ��������
 */
public class JService {
		public String id="";
		public String name="";
		public String startCheckTime="";//��ʼ��ص�ʱ��
		public String workDate="";// ����������
		public String  classURL="";
		public String  config="";
		public Map<String,String> parameters;//����ĸ��Ի����ò���
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getStartCheckTime() {
			return startCheckTime;
		}
		public void setStartCheckTime(String startCheckTime) {
			this.startCheckTime = startCheckTime;
		}
		public String getWorkDate() {
			return workDate;
		}
		public void setWorkDate(String workDate) {
			this.workDate = workDate;
		}
		public String getClassURL() {
			return classURL;
		}
		public void setClassURL(String classURL) {
			this.classURL = classURL;
		}
		public String getConfig() {
			return config;
		}
		public void setConfig(String config) {
			this.config = config;
		}
		public Map<String, String> getParameters() {
			return parameters;
		}
		public void setParameters(Map<String, String> parameters) {
			this.parameters = parameters;
		}
		
			
}
