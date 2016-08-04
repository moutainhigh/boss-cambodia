package CASche.caschedule;

import java.security.Key;

import javax.crypto.Cipher;

import CASche.CADB;
import CASche.CASPrivatePara;
import CASche.CASchePara;
import CASche.CATools;
import CASche.ConfigFile;
import CASche.ParaFromFile;
import CASche.check.OsdCheckRefresh;
import CASche.help.LoggerHelper;

public class CasSchedult {
	public static void main(String[] args) {
		
		String processName="";
		ConfigFile cf = new ConfigFile("./config/casche.properties");
		String jdbcurl = cf.getValue("JDBCURL");
		String user = cf.getValue("USER");
		String password = cf.getValue("PASSWORD");
		if ("".equals(jdbcurl) == true){
			LoggerHelper.error(CasSchedult.class,"SERVICE item don't set value ...!");
			System.exit(-1);
		}
		if ("".equals(user) == true){
			LoggerHelper.error(CasSchedult.class, "USER item don't set value ...!");
			System.exit(-1);
		}
		if ("".equals(password) == true){
			LoggerHelper.error(CasSchedult.class,"PASSWORD item don't set value ...!");
			System.exit(-1);
		}
		try {
			byte[] pnBytes = CATools.hexStr2Bytes(password);
			Cipher cipherEn =  Cipher.getInstance("DES/ECB/Nopadding");
			Key keyK = new javax.crypto.spec.SecretKeySpec("abcdefgh".getBytes(), "DES");
			cipherEn.init(Cipher.DECRYPT_MODE, keyK);
			byte[] pn = cipherEn.doFinal(pnBytes, 0, pnBytes.length);
			int len = pn.length;
			for (int i=pn.length-1; i>=0&&pn[i]==0x00; i-- ){
				len--;
			}
			password = new String(pn);
			password = password.substring(0, len);
						
		} catch (Exception e1) {
			LoggerHelper.error(CasSchedult.class, "�������ʧ��", e1);
			System.exit(-1);
		} 

		CASchePara schePara = new CASchePara();
		ParaFromFile pff = new ParaFromFile("./config/casche.properties");
		pff.setSchePara(schePara);
		if (pff.GetPara(processName) < 0){
			LoggerHelper.error(CasSchedult.class, "parameter define error in ./config/casche.properties");
			System.exit(-1);
		}
		
		if( schePara.getMapCAS()==null||schePara.getMapCAS().size()==0){
			LoggerHelper.error(CasSchedult.class, "*****δ����CA������");
			System.exit(-1);
		}
		
		//����OSD�Ϸ��������ͬ��
		if(schePara.isOsd_content_check()){
			OsdCheckRefresh.setRefresh_time(schePara.getOsd_base_refresh_time());
			OsdCheckRefresh osdrefresh= new OsdCheckRefresh(new CADB(jdbcurl,user,password));
			osdrefresh.refresh();
			Thread t=new Thread(osdrefresh); 
			t.start();
			LoggerHelper.info(OsdCheckRefresh.class, "*****����OSD�Ϸ����ͬ��");
		}
		//����out=out_his,out��дday����
		//(CASchePara casp,CADB db)
		MoveCaOutToBak sendwritebank=new MoveCaOutToBak(schePara,new CADB(jdbcurl,user,password));
		Thread t1=new Thread(sendwritebank); 
		t1.start();
		LoggerHelper.info(CasSchedult.class, "*****���� out���ͽ����дday���ƶ�����ʷ���߳�");
		System.out.println("*****���� out���ͽ����дday���ƶ�����ʷ���߳�");
		for(CASPrivatePara cas_para: schePara.getMapCAS().values()){
			Merge merge=new MergeImpl(cas_para,31);
			MergeMoveToOut cas_schedult=new MergeMoveToOut(new CADB(jdbcurl,user,password),cas_para,schePara,merge);
			Thread t2=new Thread(cas_schedult);
			t2.start();
			LoggerHelper.info(CasSchedult.class, "*****"+cas_para.getName()+"���� ca���ȳ���********");
			System.out.println("*****"+cas_para.getName()+"���� ca���ȳ���********");
		}
		
		
	}
}
