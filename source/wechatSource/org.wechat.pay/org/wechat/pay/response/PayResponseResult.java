package org.wechat.pay.response;

import org.wechat.common.entity.results.BaseXmlObj;

/**
 * 支付返回的结果
 * 
 * @author Andy
 * 
 */
public class PayResponseResult extends BaseXmlObj implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String return_code; // 返回状态码
	private String return_msg; // 返回信息

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String returnCode) {
		return_code = returnCode;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String returnMsg) {
		return_msg = returnMsg;
	}
}
