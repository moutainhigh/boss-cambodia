package CASche.caschedule;

import java.util.List;

import CASche.common.SchedultException;


public interface Merge {

	/**
	 * ͬ�����ܿ�����ͬ�����ֵļӼ�ָ��ϲ�
	 * @param list
	 * @return
	 */
	public List<JCaCommand> mergeSameCotrolId(List<JCmdDay> daylist);
	
	/**
	 * ͬ�����ܿ��Ķ�������ֺϲ���һ����ָ��
	 * @param list
	 * @return
	 * @throws Exception 
	 */
	public List<JCaCommand> mergeMultiCotrolId(List<JCmdDay> daylist) throws SchedultException;
}
