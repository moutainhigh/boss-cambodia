package CASche.caschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CASche.CASPrivatePara;
import CASche.common.CaConstants;
import CASche.common.SchedultException;

public class MergeImpl implements Merge {

	private CASPrivatePara cas_para=null;
	private int multi_cnt=31;//�ϲ�����
	public MergeImpl(CASPrivatePara cas_para,int multi_cnt){
		this.cas_para=cas_para;
		if(multi_cnt>0)
			this.multi_cnt=multi_cnt;
	}
	/**
	 * ������ֺϲ�
	 * ��ͬ��ʼʱ�䣬����ʱ�䣬ָ������ �ϲ�
	 * @throws Exception 
	 */
	public List<JCaCommand> mergeMultiCotrolId(List<JCmdDay> daylist) throws SchedultException {
		//��ͬ�����ְ�����ָ��ϲ�
		List<JCaCommand> samecontrollist=this.mergeSameCotrolId(daylist);
		//�Ӽ���Ȩ���
		//key=cmd_type
		Map<String,List<JCaCommand>> mergeMap=new  HashMap<String,List<JCaCommand>>();
		//��ȡ��ͬcmd_type��ָ���б���ֳɼӼ���Ȩ�б�
		for(JCaCommand cmd:samecontrollist){
			if(mergeMap.containsKey(cmd.getCmd_type())){
				mergeMap.get(cmd.getCmd_type()).add(cmd);
			}else{
				List<JCaCommand> list=new ArrayList<JCaCommand>();
				list.add(cmd);
				mergeMap.put(cmd.getCmd_type(), list);
			}
		}
		List<JCaCommand> mergelist=new ArrayList<JCaCommand>();
		Map<Long,Long>  mergetransMap=new HashMap<Long,Long>();//��¼��д��transnum
		//����Ȩ�����ϲ�����
		if(mergeMap.containsKey(CaConstants.AddProduct)){
			if(CASPrivatePara.MODE_SAMETIME.equals(this.cas_para.getAddMode())){
				mergelist.addAll(this.multiCotrolId_SAMETIME(mergeMap.get(CaConstants.AddProduct),mergetransMap));
			}else{
				mergelist.addAll(this.multiCotrolId_ALLTIME(mergeMap.get(CaConstants.AddProduct),mergetransMap));
			}
		}
		//����Ȩ�����ϲ�����
		if(mergeMap.containsKey(CaConstants.CancelProduct)){
			if(CASPrivatePara.MODE_SAMETIME.equals(this.cas_para.getCancelMode())){
				mergelist.addAll(this.multiCotrolId_SAMETIME(mergeMap.get(CaConstants.CancelProduct),mergetransMap));
			}else{
				mergelist.addAll(this.multiCotrolId_ALLTIME(mergeMap.get(CaConstants.CancelProduct),mergetransMap));
			}
		}
		//��д����
		for(JCmdDay day:daylist){
			if(!mergetransMap.containsKey(day.getMerge_trunsnum()))
				throw new SchedultException(day.getMerge_trunsnum()+"δ�ҵ��ϲ���Ӧmerge_trunsnum");
			day.setMerge_trunsnum(mergetransMap.get(day.getMerge_trunsnum()));
		}
		
		return mergelist;
	}
	/**
	 * �����ϲ����ɸ�ʱ�����
	 * @param list
	 * @return
	 */
	private List<JCaCommand> multiCotrolId_ALLTIME(List<JCaCommand> list,Map<Long,Long> mergetransMap){
		//ָ��ϲ�
		List<JCaCommand> mergelist=new ArrayList<JCaCommand>();
		StringBuilder merge_buffer=new StringBuilder();
		//��ջ���
		merge_buffer.delete(0, merge_buffer.length());
		if(list.size()==1){
			//��������
			merge_buffer.append("CTRL1:'").append(list.get(0).getControl_id()).append("';");
			list.get(0).setDetail_params(merge_buffer.toString());
			mergelist.addAll(list);//װ���ϲ������Ȩ
			//��¼�ϲ���ˮ
			mergetransMap.put(list.get(0).getTransnum(), list.get(0).getTransnum());
		}else{
			//�����ϲ�
			int merge_cnt=1;
			merge_buffer.delete(0, merge_buffer.length());
			JCaCommand start=null;
			for(int i=0;i<list.size();i++){
				JCaCommand cmd=list.get(i);
				if(start==null){
					start=cmd.esaycopyBean();
				}
				merge_buffer.append("CTRL").append(merge_cnt).append(":")
					.append("'").append(cmd.getControl_id()).append("';");
				if(!start.getAuth_begin_date().equals(cmd.getAuth_begin_date())
						||!start.getAuth_end_date().equals(cmd.getAuth_end_date())){
					//��ʼ����ʱ�䲻һ�£������ʱ�����
					merge_buffer.append("BD").append(merge_cnt).append(":'").append(cmd.getAuth_begin_date())
					.append("';").append("ED").append(merge_cnt).append(":'").append(cmd.getAuth_end_date()).append("';");
				}
				merge_cnt++;
				//��¼�ϲ���ˮ
				mergetransMap.put(cmd.getTransnum(), start.getTransnum());
				//���ϲ�31��ָ��
				if(merge_cnt>=this.multi_cnt){
					merge_buffer.insert(0, "CNT:'"+(this.multi_cnt-1)+"';");
					start.setDetail_params(merge_buffer.toString());
					mergelist.add(start);//װ���ϲ������Ȩ
					merge_cnt=1;
					merge_buffer.delete(0, merge_buffer.length());
					start=null;
				}
			}
			if(merge_cnt>1){
				merge_buffer.insert(0, "CNT:'"+(merge_cnt-1)+"';");
				start.setDetail_params(merge_buffer.toString());
				mergelist.add(start);//װ���ϲ������Ȩ
			}
		}
		return mergelist;
	}
	/**
	 * ��ͬʱ��ģʽ��ָ��ϲ������ɸ�ʱ�����
	 * @param samecontrollist
	 */
	private List<JCaCommand> multiCotrolId_SAMETIME(List<JCaCommand> samecontrollist,Map<Long,Long> mergetransMap){
		
		//key=auth_begin_date+cmd_type+auth_end_date
		Map<String,List<JCaCommand>> mergeMap=new  HashMap<String,List<JCaCommand>>();
		StringBuilder buffer=new StringBuilder();
		//��ȡ��ͬauth_begin_date+cmd_type+auth_end_date��ָ���б�
		for(JCaCommand cmd:samecontrollist){
			buffer.delete(0, buffer.length());
			String key=buffer.append(cmd.getAuth_begin_date())
						.append(cmd.getCmd_type())
						.append(cmd.getAuth_end_date()).toString();
			if(mergeMap.containsKey(key)){
				mergeMap.get(key).add(cmd);
			}else{
				List<JCaCommand> list=new ArrayList<JCaCommand>();
				list.add(cmd);
				mergeMap.put(key, list);
			}
		}
		//ָ��ϲ�
		List<JCaCommand> mergelist=new ArrayList<JCaCommand>();
		StringBuilder merge_buffer=new StringBuilder();
		for(List<JCaCommand> list:mergeMap.values()){
			merge_buffer.delete(0, merge_buffer.length());
			if(list.size()==1){
				merge_buffer.append("CTRL1:'").append(list.get(0).getControl_id()).append("';");
				list.get(0).setDetail_params(merge_buffer.toString());
				mergelist.addAll(list);//װ���ϲ������Ȩ
				mergetransMap.put(list.get(0).getTransnum(), list.get(0).getTransnum());
			}else{
				int merge_cnt=1;
				merge_buffer.delete(0, merge_buffer.length());
				JCaCommand start=null;
				for(int i=0;i<list.size();i++){
					JCaCommand cmd=list.get(i);
					if(start==null){
						start=cmd.esaycopyBean();
					}
					merge_buffer.append("CTRL").append(merge_cnt).append(":")
						.append("'").append(cmd.getControl_id()).append("';");
					merge_cnt++;
					//��¼�ϲ���ˮ
					mergetransMap.put(cmd.getTransnum(), start.getTransnum());
					//ֻ�ϲ�31��ָ��
					if(merge_cnt>=this.multi_cnt){
						
						merge_buffer.insert(0, "CNT:'"+this.multi_cnt+"';");
						start.setDetail_params(merge_buffer.toString());
						mergelist.add(start);//װ���ϲ������Ȩ
						merge_cnt=1;
						merge_buffer.delete(0, merge_buffer.length());
						start=null;
					}
				}
				if(merge_cnt>1){
					merge_buffer.insert(0, "CNT:'"+(merge_cnt-1)+"';");
					start.setDetail_params(merge_buffer.toString());
					mergelist.add(start);//װ���ϲ������Ȩ
				}
			}
		}
		return mergelist;
	}
	
	/**
	 * ��ͬ�����ֺϲ�
	 * ��ȡ���µĿ����ֵļ�¼
	 */
	public List<JCaCommand> mergeSameCotrolId(List<JCmdDay> daylist) {
		Map<String,List<JCmdDay>> mergeMap=new HashMap<String,List<JCmdDay>>();
		for(JCmdDay day:daylist){
			if(mergeMap.containsKey(day.getCacmd().getControl_id())){
				//map���ڿ�����
				mergeMap.get(day.getCacmd().getControl_id()).add(day);
			}else{
				//map�����ڿ�����
				List<JCmdDay> list=new ArrayList<JCmdDay>();
				list.add(day);
				mergeMap.put(day.getCacmd().getControl_id(), list);
			}
		}
		//��ȡ�ϲ���merger_trunsnum
		List<JCaCommand> merge_list=new ArrayList<JCaCommand>();
		for(String controlid:  mergeMap.keySet()){
			List<JCmdDay> list=mergeMap.get(controlid);
			//ȡ��С����ָͬ���trunsnum��Ϊ�ϲ�merge_trunsnum
			Long mergetrunsnum=list.get(0).getCacmd().getTransnum();
			JCaCommand merge=list.get(list.size()-1).getCacmd().copyBean();
			merge.setTransnum(mergetrunsnum.longValue());
			merge_list.add(merge);
			for(JCmdDay day:list){
				day.setMerge_trunsnum(mergetrunsnum);
				if(day.getCacmd().getAuth_end_date().compareTo(merge.getAuth_end_date())>0){
					merge.setAuth_end_date(day.getCacmd().getAuth_end_date());
				}
			}
		}
		return merge_list;
	}

}
