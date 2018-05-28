package func;

import core.JService;


/**
 * @author David Lau
 * 服务功能实现接口
 */
public interface Action {
	public String fileProcess(JService service);
	public enum ActionState{Init,//初始化状态
									Running,//获得调度
									Done,//成功完成 跳转日期
									Error,//执行错误
									Idel;//休眠
									}
	public static final Long sleepSecond=5L;
}
