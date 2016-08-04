package com.ycsoft.beans.core.prod;

import java.util.Date;

import com.ycsoft.commons.constants.DictKey;
import com.ycsoft.commons.store.MemoryDict;

public class CProdOrderDto extends CProdOrder {
	private String tariff_name;//display(4)
	private String disct_name;
	private String prod_type;
	private String prod_name; //display(1)
	private String prod_type_text;//display(3)
	private String serv_id;
	private String serv_id_text;
	private String is_base;
	private String is_base_text;
	private String public_acctitem_type_text;
	private String package_name;//display(2)
	private String user_name;
	//nvl(折扣周期月数，资费周期月数)
	private Integer billing_cycle;
	//计费类型:包月，包天，按次，流量等
	private String billing_type;
	//用户协议期限
	private Date protocol_date;
	//用户类型
	private String user_type;
	
	private String user_type_text;
	//终端类型（主\副）
	private String terminal_type;
	
	private String terminal_type_text;

	private Integer rent;
	
	//可退金额部分(退订和销户)
	private Integer balance_cfee;
	//可转金额部分(退订和销户)
	private Integer balance_acct;
	
	private String login_name;
	
	
	public String getLogin_name() {
		return login_name;
	}
	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}
	public Integer getBalance_cfee() {
		return balance_cfee;
	}
	public void setBalance_cfee(Integer balance_cfee) {
		this.balance_cfee = balance_cfee;
	}
	public Integer getBalance_acct() {
		return balance_acct;
	}
	public void setBalance_acct(Integer balance_acct) {
		this.balance_acct = balance_acct;
	}
	public String getBilling_type() {
		return billing_type;
	}
	public void setBilling_type(String billing_type) {
		this.billing_type = billing_type;
	}
	public Integer getRent() {
		return rent;
	}
	public void setRent(Integer rent) {
		this.rent = rent;
	}
	public String getUser_type_text() {
		return user_type_text;
	}
	public String getTerminal_type_text() {
		return terminal_type_text;
	}
	public String getUser_type() {
		return user_type;
	}
	public void setUser_type(String user_type) {
		this.user_type = user_type;
		this.user_type_text = MemoryDict.getDictName(DictKey.USER_TYPE, this.user_type);
	}
	public String getTerminal_type() {
		return terminal_type;
	}
	public void setTerminal_type(String terminal_type) {
		this.terminal_type = terminal_type;
		this.terminal_type_text=MemoryDict.getDictName(DictKey.TERMINAL_TYPE, this.terminal_type);
	}
	public Integer getBilling_cycle() {
		return billing_cycle;
	}
	public void setBilling_cycle(Integer billing_cycle) {
		this.billing_cycle = billing_cycle;
	}
	public Date getProtocol_date() {
		return protocol_date;
	}
	public void setProtocol_date(Date protocol_date) {
		this.protocol_date = protocol_date;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
	public String getProd_type() {
		return prod_type;
	}
	public void setProd_type(String prod_type) {
		this.prod_type = prod_type;
		this.prod_type_text = MemoryDict.getDictName(DictKey.PROD_TYPE, this.prod_type);
	}
	public String getTariff_name() {
		return tariff_name;
	}
	public void setTariff_name(String tariff_name) {
		this.tariff_name = tariff_name;
	}
	public String getDisct_name() {
		return disct_name;
	}
	public void setDisct_name(String disct_name) {
		this.disct_name = disct_name;
	}
	
	public String getProd_type_text() {
		return prod_type_text;
	}
	
	public String getServ_id() {
		return serv_id;
	}
	public void setServ_id(String serv_id) {
		this.serv_id = serv_id;
		this.serv_id_text = MemoryDict.getDictName(DictKey.SERV_ID, this.serv_id);
	}
	public String getServ_id_text() {
		return serv_id_text;
	}
	public String getIs_base() {
		return is_base;
	}
	public void setIs_base(String is_base) {
		this.is_base = is_base;
		this.is_base_text = MemoryDict.getDictName(DictKey.BOOLEAN, this.is_base);
	}
	public String getIs_base_text() {
		return is_base_text;
	}
	public String getPublic_acctitem_type_text() {
		return public_acctitem_type_text;
	}
	@Override
	public void setPublic_acctitem_type(String public_acctitem_type) {
		// TODO Auto-generated method stub
		super.setPublic_acctitem_type(public_acctitem_type);
		this.public_acctitem_type_text = MemoryDict.getDictName(DictKey.PUBLIC_ACCTITEM_TYPE, public_acctitem_type);
	}
	public String getPackage_name() {
		return package_name;
	}
	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}
	
}
