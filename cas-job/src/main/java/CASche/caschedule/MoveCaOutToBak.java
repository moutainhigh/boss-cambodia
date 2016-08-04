package CASche.caschedule;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import CASche.CACommand;
import CASche.CADB;
import CASche.CASchePara;
import CASche.CATools;
import CASche.common.CaConstants;
import CASche.help.LoggerHelper;

/**
 * �ƶ��ѷ���ָ���ʷ��out=>out_bak
 * ����д����״̬��day��
 */
public class MoveCaOutToBak implements Runnable {

	private CADB db = null;
	private String count_sql=null;
	private String update_sql=null;
	private String insert_sql=null;
	private String delete_sql=null;
	private String updateday_sql=null;
	private String query_sql=null;
	public MoveCaOutToBak(CASchePara casp,CADB db){

		this.db=db;
		this.db.connDB();
		
		this.count_sql= " SELECT count(1) FROM " + casp.getDstTable() + " WHERE is_sent='Y' ";
		this.query_sql=" SELECT * FROM " + casp.getDstTable() + " WHERE is_sent='A'  ";
		this.updateday_sql="update "+casp.getSrcTableBak()+" set is_sent='Y' , send_date=? , result_flag=? ,error_info=?,ret_date=? where merge_trunsnum=? ";
		this.update_sql=" UPDATE " + casp.getDstTable() + " SET is_sent='A'  WHERE is_sent='Y' ";
        this.insert_sql="INSERT INTO " + casp.getDstTableBak() +  
		" SELECT TRANSNUM,JOB_ID,CAS_ID,CAS_TYPE,USER_ID,CUST_ID,DONE_CODE,CMD_TYPE "+
		" ,STB_ID,CARD_ID,PRG_NAME,BOSS_RES_ID,CONTROL_ID,AUTH_BEGIN_DATE "+
		",AUTH_END_DATE,result_flag,ERROR_INFO,AREA_ID,'Y' IS_SENT,RECORD_DATE "+
		" ,send_date,DETAIL_PARAMS,PRIORITY,RET_DATE FROM "+casp.getDstTable()+ " WHERE is_sent='A' ";
        this.delete_sql="DELETE FROM " + casp.getDstTable() + " WHERE is_sent='A' ";
	}
	
	/**
	 * ��out�ѷ��͵�ָ����>������ʱ���ƶ�outָ���ʷ��
	 * @param ps_count
	 * @param ps_update
	 * @param ps_insert
	 * @param ps_delete
	 * @return
	 * @throws SQLException
	 */
	private int move_ps(PreparedStatement ps_count,PreparedStatement ps_update,
			PreparedStatement ps_insert,PreparedStatement ps_delete,
			PreparedStatement ps_dayupdate,PreparedStatement ps_query) throws SQLException{
		ResultSet resultSet = null;	
		try {
			try{
				//��ѯout��is_sent=Y������
				resultSet=ps_count.executeQuery();
				if(!resultSet.next())
					return 0;
				else{
					if(resultSet.getInt(1)<=0)
						return 0;
				}
				
			}catch(SQLException e3){
				LoggerHelper.error(this.getClass(), this.count_sql);
				throw e3;
			}
			try{
				//update out set is_sent=A where is_sent=Y
				ps_update.executeUpdate();
			}catch(SQLException e3){
				LoggerHelper.error(this.getClass(), this.update_sql);
				throw e3;
			}
			try{
				resultSet.close();
				//��ȡis_sent=A�ļ�¼
				resultSet=ps_query.executeQuery();
			}catch(SQLException e3){
				LoggerHelper.error(this.getClass(), this.query_sql);
				throw e3;
			}
			
			try{
				//����j_ca_command_day���is_sent,send_date,result_flag,error_info����Ϣ
				int count=0;
				while (resultSet.next()) {
					count ++;
					ps_dayupdate.setTimestamp(1,resultSet.getTimestamp("send_date"));
					ps_dayupdate.setString(2,resultSet.getString("result_flag"));
					ps_dayupdate.setString(3,resultSet.getString("error_info"));
					ps_dayupdate.setTimestamp(4,resultSet.getTimestamp("ret_date"));
					ps_dayupdate.setLong(5,resultSet.getLong("transnum"));
					ps_dayupdate.addBatch();
					if(count>=500){
						count=0;
						ps_dayupdate.executeBatch();
						db.commit();
	
					}
				}
				if(count>0){
					ps_dayupdate.executeBatch();
					db.commit();
		
				}
			}catch(SQLException e3){
				LoggerHelper.error(this.getClass(), this.updateday_sql);
				throw e3;
			}
			try{
				//��out����is_sent=A�ļ�¼ ���뵽out_bak��
				ps_insert.executeUpdate();
			}catch(SQLException e3){
				LoggerHelper.error(this.getClass(), this.insert_sql);
				throw e3;
			}
			try{
				//ɾ��out����is_sent=A�ļ�¼
				return ps_delete.executeUpdate();
			}catch(SQLException e3){
				LoggerHelper.error(this.getClass(), this.delete_sql);
				throw e3;
			}
		} catch (SQLException e) {
			throw e;
		}finally{
			if(resultSet!=null){
				try{resultSet.close();}catch(Exception e1){}
				resultSet=null;
			}
		}
	}
	/**
	 * �����ݿ����ӣ�
	 */
	public void run() {
		LoggerHelper.debug(this.getClass(), "======����ʼ ��out �Ƶ� out_bak �� ����дday��");

		PreparedStatement ps_count= db.prepareStatement(this.count_sql);
		PreparedStatement ps_update=db.prepareStatement(this.update_sql);
		PreparedStatement ps_insert=db.prepareStatement(this.insert_sql);
		PreparedStatement ps_delete=db.prepareStatement(this.delete_sql);
		PreparedStatement ps_updateday=db.prepareStatement(this.updateday_sql);
		PreparedStatement ps_query=db.prepareStatement(this.query_sql);
		while(true){
			CATools.sleeping(CaConstants.movecaouttohis_speed);
			try {
				//���ݿ�����
				if (db.getConnectInterruptFlag() == true){
					while(db.getConnectInterruptFlag() == true){
				 		db.connDB();
				 		CATools.sleeping(CaConstants.free_sleep_time);
				 	}
				}
				int move_cnt=0;
				if(ps_count==null)
					ps_count= db.prepareStatement(this.count_sql);
				if(ps_insert==null)
					ps_update=db.prepareStatement(this.update_sql);
				if(ps_insert==null)
					ps_insert=db.prepareStatement(this.insert_sql);
				if(ps_delete==null)
					ps_delete=db.prepareStatement(this.delete_sql);
				if(ps_updateday==null)
					ps_updateday=db.prepareStatement(this.updateday_sql);
				if(ps_query==null)
					ps_query=db.prepareStatement(this.query_sql);
					
				//����״������out=>his
				move_cnt=this.move_ps(ps_count, ps_update, ps_insert, ps_delete,ps_updateday,ps_query);
				//�ύ���ݿ�
				db.commit();
			
				if(move_cnt>0)
					LoggerHelper.debug(this.getClass(),"out=>bak �ƶ���"+ move_cnt+"��");	
				else{
					LoggerHelper.debug(this.getClass(),"��д��������");
					CATools.sleeping(CaConstants.free_sleep_time);
				}
			} catch (SQLException e) {
				LoggerHelper.error(this.getClass(),"���ݿ����", e);
				if(ps_count!=null){
					try{ps_count.close();}catch(Exception e1){}
					ps_count=null;
				}
				if(ps_update!=null){
					try{ps_update.close();}catch(Exception e1){}
					ps_update=null;
				}
				if(ps_insert!=null){
					try{ps_insert.close();}catch(Exception e1){}
					ps_insert=null;
				}
				if(ps_delete!=null){
					try{ps_delete.close();}catch(Exception e1){}
					ps_delete=null;
				}
				if(ps_updateday!=null){
					try{ps_updateday.close();}catch(Exception e1){}
					ps_updateday=null;
				}
				if(ps_query!=null){
					try{ps_query.close();}catch(Exception e1){}
					ps_query=null;
				}
				db.rollback();
				if (db.checkSpecialErrorCode(e.getErrorCode()) == -1) {
					db.setConnectInterruptFlag(true);
					db.closeConn();
				}
				CATools.sleeping(CaConstants.free_sleep_time);
			} catch (Exception e) {
				LoggerHelper.error(this.getClass(),"ϵͳ����", e);
				if(ps_count!=null){
					try{ps_count.close();}catch(Exception e1){}
					ps_count=null;
				}
				if(ps_update!=null){
					try{ps_update.close();}catch(Exception e1){}
					ps_update=null;
				}
				if(ps_insert!=null){
					try{ps_insert.close();}catch(Exception e1){}
					ps_insert=null;
				}
				if(ps_delete!=null){
					try{ps_delete.close();}catch(Exception e1){}
					ps_delete=null;
				}
				if(ps_updateday!=null){
					try{ps_updateday.close();}catch(Exception e1){}
					ps_updateday=null;
				}
				if(ps_query!=null){
					try{ps_query.close();}catch(Exception e1){}
					ps_query=null;
				}
				db.rollback();
				db.setConnectInterruptFlag(true);
				db.closeConn();
				CATools.sleeping(CaConstants.free_sleep_time);
			}
		}
	}

}
