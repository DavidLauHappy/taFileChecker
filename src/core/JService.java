package core;

import java.util.Map;

/**
 * @author David Lau
 * 服务定义类
 */
public class JService {
		public String id="";
		public String name="";
		public String startCheckTime="";//开始监控的时间
		public String workDate="";// 服务工作日期
		public String  classURL="";
		public String  config="";
		public Map<String,String> parameters;//服务的个性化配置参数
		
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
