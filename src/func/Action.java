package func;

import core.JService;


/**
 * @author David Lau
 * ������ʵ�ֽӿ�
 */
public interface Action {
	public String fileProcess(JService service);
	public enum ActionState{Init,//��ʼ��״̬
									Running,//��õ���
									Done,//�ɹ���� ��ת����
									Error,//ִ�д���
									Idel;//����
									}
	public static final Long sleepSecond=5L;
}
