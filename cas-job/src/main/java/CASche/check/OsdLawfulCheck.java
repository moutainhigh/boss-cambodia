package CASche.check;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import CASche.caschedule.JCaCommand;
import CASche.common.CaConstants;

public class OsdLawfulCheck {
	
	
		private final static String osd_begin=";MSG:'";
		private final static String osd_end="';style:";
		/**
		 * ��ָ������ȡOSD����
		 * @param ca
		 * @return
		 * @throws Exception 
		 */
		public static String extractOsdContent(JCaCommand ca) throws Exception{
			if(ca==null) return null;
			//��ָ����osdʱ
			if(CaConstants.SendOsd.equals(ca.getCmd_type())){
				if(ca.getDetail_params()==null||ca.getDetail_params().trim().length()<=0){
					throw new Exception("OSD����Ϊ��");
				}
				int begin=ca.getDetail_params().indexOf(osd_begin);
				int end=ca.getDetail_params().indexOf(osd_end);
				if(begin+6>=end||begin<=0||end<=0){
					throw new Exception("OSD���ݸ�ʽ�޷�ʶ��{*;MSG:'*';style:*}");
				}
				String osd=ca.getDetail_params().substring(begin+6, end);
				return osd;
			}
			return null;
		}
		//OSD���ݺϷ�����֤
		public static String LawfulCheck(String content){
			if(content==null) return null;
			Set<Integer> check_num_set=new HashSet<Integer>(content.length());
			int check_num_size=content.length();
			for(String phrase:OsdCheckRefresh.getPhrases()){
				int fromIndex=0;
				int _index=0;
				do{
					_index=content.indexOf(phrase, fromIndex);
					fromIndex=_index+phrase.length();;
					if(_index>=0){
						for(int i=_index;i<fromIndex;i++){
							if(!check_num_set.contains(i)){
								check_num_set.add(i);
							}
						}
					}
				}while(_index>=0);
				
				if(check_num_set.size()==check_num_size){
					return null;
				}
			}
			
			if(check_num_set.size()>check_num_size){
				content="System_Error:�ַ�������"+check_num_size+",�����㳤��Ϊ"+check_num_set.size();
			}else{
				char[] chars=content.toCharArray();
				char[] error_content=new char[check_num_size-check_num_set.size()];
				for(int i=0,j=0;i<check_num_size;i++){
					if(!check_num_set.contains(i)){
						error_content[j]=chars[i];
						j++;
					}
				}
				content=new String(error_content);
			}
			
			return content;
		}
		
		
		public static void main(String[] args){
			OsdLawfulCheck ch=new OsdLawfulCheck();
			JCaCommand ca=new JCaCommand();
			ca.setCmd_type(CaConstants.SendOsd);
			ca.setDetail_params("TITLE:'-1';MSG:'�𾴵��û�:�������ֵ�����Ƿ��,Ϊ��Ӱ�����������տ�,�������µ�ǰ�����Ӫҵ�����ѻ��ֵ.�ͷ�96296';style:'1';duration:'120'");
			try {
				String content=ch.extractOsdContent(ca);
				Set<String> _base=new HashSet<String>();
				_base.add("�𾴵��û�");
				_base.add("���ֵ�����Ƿ��");
				_base.add(",");
				_base.add("Ϊ��Ӱ�����������տ�,�������µ�ǰ�����Ӫҵ�����ѻ��ֵ");
				_base.add(".�ͷ�96296");
				_base.add("��");
				_base.add("��");
				_base.add(":");
				_base.add("����");
				OsdCheckRefresh.setPhrases(_base);
				
				System.out.println("check:"+ch.LawfulCheck(content));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		public static void main1(String[] args){
			OsdLawfulCheck ch=new OsdLawfulCheck();
			JCaCommand ca=new JCaCommand();
			ca.setCmd_type(CaConstants.SendOsd);
			ca.setDetail_params("TITLE:'-1';MSG:'�𾴵��û�:�������ֵ�����Ƿ��,Ϊ��Ӱ�����������տ�,�������µ�ǰ�����Ӫҵ�����ѻ��ֵ.�ͷ�96296';style:'1';duration:'120'");
			try {
				System.out.println(ch.extractOsdContent(ca));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ca.setCmd_type(CaConstants.AddProduct);
			ca.setDetail_params("TITLE:'-1';MSG:'�𾴵��û�:�������ֵ�����Ƿ��,Ϊ��Ӱ�����������տ�,�������µ�ǰ�����Ӫҵ�����ѻ��ֵ.�ͷ�96296';style:'1';duration:'120'");
			try {
				System.out.println(ch.extractOsdContent(ca));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ca.setCmd_type(CaConstants.SendOsd);
			ca.setDetail_params("TITLE:'-1SG:'�𾴵��û�:�������ֵ�����Ƿ��,Ϊ��Ӱ�����������տ�,�������µ�ǰ�����Ӫҵ�����ѻ��ֵ.�ͷ�96296';style:'1';duration:'120'");
			try {
				System.out.println(ch.extractOsdContent(ca));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ca.setDetail_params("");
			try {
				System.out.println(ch.extractOsdContent(ca));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ca.setDetail_params("TITLE:'-1;MSG:'12';style:'1';duration:'120'");
			try {
				System.out.println(ch.extractOsdContent(ca));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

}
