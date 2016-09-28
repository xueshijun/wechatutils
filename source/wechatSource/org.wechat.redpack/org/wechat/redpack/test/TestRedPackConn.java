package org.wechat.redpack.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;

import org.junit.Test;
import org.wechat.common.entity.results.WechatResult;
import org.wechat.common.utils.ConvertJsonUtils;
import org.wechat.redpack.request.GethbinfoRequest;
import org.wechat.redpack.request.GroupredpackRequest;
import org.wechat.redpack.request.RedpackRequest;
import org.wechat.redpack.utils.SendRedPackUtils;

/**
 * 测试发送红包的URL地址
 * @author Andy
 *
 */
public class TestRedPackConn {
	
	/**
	 * 测试发送红包url地址
	 */
	public void testSendRedBack(){
		FileInputStream  fis = null;
		try {
			//添加密匙
			KeyStore keystore = KeyStore.getInstance("PKCS12");
			//读取证书
			fis = new FileInputStream(new File("d:\\apiclient_cert.p12"));
			//读取证书
			keystore.load(fis,"10012936".toCharArray());
			URL url = new URL("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack");
			HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			/*connection.setSSLSocketFactory(arg0);*/
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 测试发送红包
	 * (测试成功)
	 */
	@Test
	public void testHttpClientSendRedBack(){
		RedpackRequest request = new RedpackRequest();
		request.setAct_name("test Activity"); 
		request.setClient_ip("192.168.0.1");
		request.setMch_billno("10012936201509259000000019");
		request.setMch_id("10012936");
		request.setNonce_str("1111");
		request.setRe_openid("o8ed_jv3vIC6l7Y8WQybls0xl8n0");
		request.setRemark("This is remark");
		request.setSend_name("sanyangxiyi");
		request.setSign("");
		request.setTotal_amount(100);
		request.setTotal_num(1);
		request.setWishing("zhufu");
	    request.setWxappid("wx7c50b0eb849348d3");
	    SendRedPackUtils utils = new SendRedPackUtils();
	    WechatResult result =  utils.sendRed(request,"7cfefba351c3db469ffdff3bf9d80be9","D:\\apiclient_cert.p12","10012936");
	    String jsonData = ConvertJsonUtils.toJsonString(result);
	    System.out.println(jsonData);
	}
	
	/**
	 * 测试获取红包发送信息
	 * (测试成功)
	 */
	@Test
	public void testGetRedInfo(){
		SendRedPackUtils utils = new SendRedPackUtils();
		GethbinfoRequest request = new GethbinfoRequest();
		request.setAppid("wx7c50b0eb849348d3");
		request.setBill_type("MCHT");
		request.setMch_billno("10012936201509259000000000");
		request.setMch_id("10012936");
		request.setNonce_str("MCHT");
		WechatResult result = utils.getHbInfo(request,"D:\\apiclient_cert.p12","10012936","7cfefba351c3db469ffdff3bf9d80be9");
		String jsonData = ConvertJsonUtils.toJsonString(result);
	    System.out.println(jsonData);
	}
	
	/**
	 * 测试发送裂变红包
	 * (测试成功)
	 */
	@Test
	public void testSendGroupRedPack(){
		SendRedPackUtils utils = new SendRedPackUtils();
		GroupredpackRequest request = new GroupredpackRequest();
		request.setWxappid("wx7c50b0eb849348d3");
		request.setAct_name("test Activity");
		request.setAmt_type("ALL_RAND");
		request.setMch_billno("10012936201509260000000000");
		request.setMch_id("10012936");
		request.setNonce_str("11111");
		request.setRe_openid("o8ed_jv3vIC6l7Y8WQybls0xl8n0");
		request.setRemark("This is remark!");
		request.setSend_name("sanyangxiyi");
		request.setTotal_amount(300);
		request.setTotal_num(3);
		request.setWishing("wish1");
		WechatResult result = utils.sendGroupRedPack(request,"7cfefba351c3db469ffdff3bf9d80be9","D:\\apiclient_cert.p12","10012936");
		String jsonData = ConvertJsonUtils.toJsonString(result);
	    System.out.println(jsonData);
	}
}
