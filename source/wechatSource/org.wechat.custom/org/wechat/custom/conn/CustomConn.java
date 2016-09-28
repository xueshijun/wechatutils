package org.wechat.custom.conn;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.wechat.common.conn.Connection;
import org.wechat.common.entity.results.JsonResult;
import org.wechat.common.entity.results.WechatResult;
import org.wechat.common.utils.ConvertJsonUtils;
import org.wechat.common.utils.EncryptUtils;
import org.wechat.custom.entity.ImageCustom;
import org.wechat.custom.entity.MusicCustom;
import org.wechat.custom.entity.NewsCustom;
import org.wechat.custom.entity.SessionEntity;
import org.wechat.custom.entity.TextCustom;
import org.wechat.custom.entity.VideoCustom;
import org.wechat.custom.entity.VoiceCustom;
import org.wechat.custom.entity.results.CustonInfo;
import org.wechat.custom.entity.results.LinekFlist;
import org.wechat.custom.entity.results.OnLineSession;
import org.wechat.custom.handler.CustomHandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 客服消息连接
 * 
 * @author Andy
 * 
 */
public class CustomConn { 
	private static final int DEFAULT_PAGESIZE  = 10; // 默认为页面大小
	private static final int DEFAULT_PAGEINDEX = 1; // 默认第几页
	// 客服消息path 
	private static final String CUSTOM_MSG_PATH = "https://api.weixin.qq.com/cgi-bin/customservice/getrecord";
	// 获取所有客服基本信息 
	private static final String CUSTOM_INFO_PATH = "https://api.weixin.qq.com/cgi-bin/customservice/getkflist";
	// 获取在线客服接待信息  
	private static final String CUSTOM_ONLINE_PATH = "https://api.weixin.qq.com/cgi-bin/customservice/getonlinekflist";
	// 添加客服接口  
	private static final String ADD_CUSTOM_PATH = "https://api.weixin.qq.com/customservice/kfaccount/add";
	// 修改客服信息 
	private static final String UPDATE_CUSTOM_PATH = "https://api.weixin.qq.com/customservice/kfaccount/update";
	// 删除客服   
	private static final String DELETE_CUSTOM_PATH = "https://api.weixin.qq.com/customservice/kfaccount/del";
	// 上传客服头像 
	private static final String UPDATE_CUSTOM_HEADIMG = "http://api.weixin.qq.com/customservice/kfaccount/uploadheadimg";
	// 客服接口 发消息 
	private static final String CUSTOM_SEND_MSG = "https://api.weixin.qq.com/cgi-bin/message/custom/send";
	// 多客服创建会话  createSession
	private static final String CREATESESSION = "https://api.weixin.qq.com/customservice/kfsession/create";
	//关闭会话
	private static final String CLOSESESSION = "https://api.weixin.qq.com/customservice/kfsession/close";
	//根据用户的openid 获取会话的状态 
	private static final String GETSESSIONSTATE="https://api.weixin.qq.com/customservice/kfsession/getsession";
	//根据客户的账号获取当前客服的会话列表 
	private static final String GETSESSIONLIST="https://api.weixin.qq.com/customservice/kfsession/getsessionlist";
	//获取未接入会话列表  
	private static final String GETWAITCASE="https://api.weixin.qq.com/customservice/kfsession/getwaitcase";
	
	/**
	 * 获取未接入会话列表
	 * @param accessToken   授权token
	 * @return
	 */
	public static String getWaitCase(String accessToken){
		String result = "";
		TreeMap<String,String> params = new TreeMap<String,String>();
		params.put("access_token", accessToken);
		result = new Connection().HttpsDefaultExecute("GET",GETWAITCASE,params,"");
		return result;
	}
	
	/**
	 * 根据客服的账号获取当前的会话列表
	 * @param accessToken	授权token
	 * @param kfAccount	   客服账号
	 * @return
	 */
	public static WechatResult getSessionListByKfaccount(String accessToken,String kfAccount){
		WechatResult resultObj = null;
		TreeMap<String,String> params = new TreeMap<String,String>();
		params.put("access_token",accessToken);
		params.put("kf_account",kfAccount);
		String result = new Connection().HttpsDefaultExecute("GET",GETSESSIONLIST,params, "");
		resultObj = ConvertSessionList(result);
		return resultObj;
	}
	
	/**
	 * 将json格式的数据转换为在线会话列表
	 * @param jsonData
	 * @return 如果result.success==true,则返回OnLineSession 对象的list集合
	 * 否则会显示提示信息
	 */
	private static WechatResult ConvertSessionList(String jsonData){
		WechatResult result = new WechatResult();
		if(jsonData!=null&&!"".equals(jsonData)){
			JSONObject object = JSONObject.parseObject(jsonData);
			JSONArray array = object.getJSONArray("sessionlist");
			if(array.size()>0){						//表示有参数
				List<OnLineSession> list = ConvertJsonUtils.jsonToJavaList(jsonData,OnLineSession.class);
				if(list!=null&&list.size()>0){
					result.setObj(list);
					result.setSuccess(true);
				}else{
					result.setObj(jsonData);
				}
			}else{
				result.setObj(jsonData);
			}
		}else{
			result.setObj("Convert params is null!");
		}
		return result;
	}
	
	
	/**
	 * 获取客户会话的状态
	 * @param accessToken	授权token
	 * @param openId	    openid
	 * @return				通过判断 JsonResult 中的code是否获取成功
	 */
	public static JsonResult getSessionStateByOpenId(String accessToken,String openId){
		JsonResult resultObj = null;
		TreeMap<String,String> params = new TreeMap<String,String>();
		params.put("access_token", accessToken);
		params.put("openid", openId);
		String result = new Connection().HttpsDefaultExecute("GET",GETSESSIONSTATE,params, "");
		resultObj = ConvertJsonUtils.jsonToJavaObject(result,JsonResult.class);
		return resultObj;
	}
	
	/**
	 * 添加客服
	 * 
	 * @param accessToken 授权token
	 * @param kf_account 完整客服账号，格式为：账号前缀@公众号微信号，账号前缀最多10个字符，必须是英文或者数字字符。如果没有公众号微信号，请前往微信公众平台设置
	 * @param nickname 客服昵称，最长6个汉字或12个英文字符
	 * @param password 客服账号登录密码，格式为密码明文的32位加密MD5值 (已加密,传入明文即可)
	 * @return ResultMsg errcode==0 errmsg=ok 表示成功!
	 */
	public static JsonResult addCustom(String accessToken, String kf_account,
			String nickname, String password) throws Exception {
		Connection connection = new Connection();
		String result = "";
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("access_token", accessToken);
		// POST params
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("kf_account", kf_account);
		params.put("nickname", nickname);
		params.put("password", EncryptUtils.EncryptMD5(password));
		String data = ConvertJsonUtils.toJsonString(params);
		result = connection.HttpsDefaultExecute("POST", ADD_CUSTOM_PATH, map, data);
		return connection.ConvertResult(result);
	}

	/**
	 * 修改客服
	 * 
	 * @param accessToken
	 *            授权token
	 * @param kf_account
	 *            完整客服账号，格式为：账号前缀@公众号微信号，账号前缀最多10个字符，必须是英文或者数字字符。如果没有公众号微信号，请前往微信公众平台设置
	 *            。
	 * @param nickname
	 *            客服昵称，最长6个汉字或12个英文字符
	 * @param password
	 *            客服账号登录密码，格式为密码明文的32位加密MD5值 (已加密,传入明文即可)
	 * @return ResultMsg errcode==0 errmsg=ok 表示成功!
	 */
	public static JsonResult updateCustomInfo(String accessToken, String kf_account,
			String nickname, String password) throws Exception {
		Connection connection = new Connection();
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("access_token", accessToken);
		// POST params
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("kf_account", kf_account);
		params.put("nickname", nickname);
		params.put("password", EncryptUtils.EncryptMD5(password));
		String data = ConvertJsonUtils.toJsonString(params);
		String result = connection.HttpsDefaultExecute("POST", UPDATE_CUSTOM_PATH, map,data);
		return connection.ConvertResult(result);
	}

	/**
	 * 上传客服头像
	 * 
	 * @param accessToken 授权token
	 * @param kf_account  kf_account的完整账号
	 * @return  JsonResult.errcode==0&& JsonResult.errmsg==ok 表示上传成功
	 */
	public static JsonResult uploadCustomHeadImg(String accessToken, String kf_account,String mediaPath) {
		JsonResult result = null;
		TreeMap<String,String> params = new TreeMap<String,String>();
		params.put("access_token",accessToken);
		params.put("kf_account", kf_account);
		String returnData =new Connection().uploadMediaMethod(params,"POST",UPDATE_CUSTOM_HEADIMG,mediaPath,"");
		result = ConvertJsonUtils.jsonToJavaObject(returnData,JsonResult.class);
		return result;
	}

	/**
	 * 删除客服
	 * 
	 * @param accessToken
	 *            授权token
	 * @param kf_account
	 *            客服完整账号
	 * @return CustomResult errorCode==0和errorMsg=ok 表示成功
	 */
	public static JsonResult deleteCustom(String accessToken, String kf_account) {
		Connection connection = new Connection();
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("access_token", accessToken);
		map.put("kf_account", kf_account);
		String result = connection.HttpsDefaultExecute("POST", DELETE_CUSTOM_PATH, map, "");
		return connection.ConvertResult(result);
	}

	/**
	 * 查询客服消息
	 * 
	 * @param accessToken
	 *            授权token
	 * @param openid
	 *            用户openId
	 * @param stateTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param pageSize
	 *            每页大小，每页最多拉取1000条 默认为10条
	 * @param pageIndex
	 *            查询第几页，从1开始 默认为1页
	 * @return
	 */
	public static String getCustomRecord(String accessToken, String openid,
			int stateTime, int endTime, int pageSize, int pageIndex) {
		String result = "";
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("access_token", accessToken);
		map.put("openid", openid);
		map.put("starttime", "" + stateTime);
		map.put("endtime", "" + endTime);
		map.put("pagesize", "" + (pageSize == 0 ? DEFAULT_PAGESIZE : pageSize));
		map.put("pageindex", ""+ (pageIndex == 0 ? DEFAULT_PAGEINDEX : pageIndex));
		result = new Connection().HttpsDefaultExecute("POST", CUSTOM_MSG_PATH, map, "");
		return result;
	}

	/**
	 * 获取客服信息
	 * 
	 * @param accessToken
	 *            授权token
	 * @return CustonBasicInfo集合的json格式
	 */
	public static String getCustomInfo(String accessToken) {
		String result = "";
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("access_token", accessToken);
		result = new Connection().HttpsDefaultExecute("GET", CUSTOM_INFO_PATH, map, "");
		return result;
	}

	/**
	 * 通过accessToken 获取用户信息
	 * @param accessToken		授权token
	 * @return	返回的是List<CustonOnfo>
	 */
	public static List<CustonInfo> getCustomListInfo(String accessToken){
		List<CustonInfo> list = new ArrayList<CustonInfo>();
		String result = getCustomInfo(accessToken);
		if(result!=null&&!"".equals(result)){
			list =ConvertJsonUtils.jsonToJavaListByKey(result,CustonInfo.class,"kf_list");
		}
		return list;
	}
	/**
	 * 获取在线客服接待信息
	 * @param accessToken  授权token
	 * @return LinekFlist集合的json格式数据    List LinekFlist 集合,该集合中为在线 客服的信息,
	 * 集合大小等于在线客服个数
	 * 应用场景: 判断当前客服是否存在在线客服
	 */
	public static List<LinekFlist> getOnLinekFlist(String accessToken) {
		String result = "";
		TreeMap<String, String> map = new TreeMap<String, String>();
		map.put("access_token", accessToken);
		result = new Connection().HttpsDefaultExecute("GET", CUSTOM_ONLINE_PATH, map, "");
		List<LinekFlist> sessionList = ConvertJsonUtils.jsonToJavaListByKey(result,LinekFlist.class,"kf_online_list");
		return sessionList;
	}

	/**
	 * 上传客服头像
	 * 
	 * @param accessToken
	 *            授权token
	 * @param kf_account
	 *            客服账号 账号前缀@公众号微信号，账号前缀最多10个字符
	 * @param picUrl
	 *            图片url
	 * @return
	 */
	public JsonResult uploadHeadImg(String accessToken, String kf_account,
			String picUrl) {
		return null;
	}
	
	/**
	 * 多客服创建会话
	 * @param accessToken 授权token
	 * @param entity	 会话实体
	 * @return JsonResult  通过判断其errcode 是否操作成功
	 */
	public static JsonResult createSession(String accessToken,SessionEntity entity){
		JsonResult resultObj = null;
		TreeMap<String,String> params = new TreeMap<String,String>();
		params.put("access_token",accessToken);
		String data = JSONObject.toJSONString(entity);
		String result =new Connection().HttpsDefaultExecute("POST", CREATESESSION,params,data);
		resultObj =ConvertJsonUtils.jsonToJavaObject(result,JsonResult.class);
		return resultObj;
	}
	
	/**
	 * 关闭会话
	 * @param accessToken	授权token
	 * @param entity		会话实体
	 * @return				操作的状态,通过判断errcode判断是否成功
	 */
	public static JsonResult closeSession(String accessToken,SessionEntity entity){
		JsonResult resultObj = null;
		TreeMap<String,String> params = new TreeMap<String,String>();
		params.put("access_token",accessToken);
		String data = JSONObject.toJSONString(entity);
		String result = new Connection().HttpsDefaultExecute("POST", CLOSESESSION,params,data);
		resultObj =ConvertJsonUtils.jsonToJavaObject(result,JsonResult.class);
		return resultObj;
	}
	
	
	/**
	 * 发送客服消息
	 * @param accessToken  授权token
	 * @param msgData  消息json格式的数据,包括文本消息,图片消息
	 * @return
	 */
	private static String sendCustomMsg(String accessToken, String msgData) {
		String result = "";
		TreeMap<String, String> params = new TreeMap<String, String>();
		params.put("access_token", accessToken);
		result = new Connection().HttpsDefaultExecute("POST", CUSTOM_SEND_MSG, params, msgData);
		return result;
	}
	
	/**
	 * 将json格式的数据转换为 JSONResult对象
	 * @param data
	 * @return
	 */
	private JsonResult ConvertCustomMsg(String data){
		JsonResult result = null;
		if(data!=null&&!"".equals(data)){
			result = JSONObject.parseObject(data, JsonResult.class);
		}
		return result;
	}
	
	/**
	 * 发送文本客服消息	
	 * @param accessToken   授权token
	 * @param text			文本客服消息
	 * @return  JsonResult.errcode=0 && JsonResult.errmsg==ok 表示发送成功
	 */
	public JsonResult sendCustomOfTextMsg(String accessToken,TextCustom text){
		JsonResult resultObject = null;
		String data = CustomHandler.toTextJson(text);
		String result = sendCustomMsg(accessToken,data);
		resultObject = ConvertCustomMsg(result);
		return resultObject;
	}
	
	/**
	 * 发送图片客服消息
	 * @param accessToken	授权token
	 * @param image			ImageCustom 对象
	 * @return	JsonResult.errcode=0 && JsonResult.errmsg==ok 表示发送成功
	 */
	public JsonResult sendCustomofImageMsg(String accessToken,ImageCustom image){
		JsonResult resultObj = null;
		String data = CustomHandler.toImageJson(image);
		String result = sendCustomMsg(accessToken,data);
		resultObj = ConvertCustomMsg(result);
		return resultObj;
	}
	
	/**
	 * 发送语音客服消息
	 * @param accessToken	  授权token
	 * @param voice			VoiceCustom对象
	 * @return  JsonResult.errcode=0 && JsonResult.errmsg==ok 表示发送成功
	 */
	public JsonResult sendCustomOfVoice(String accessToken,VoiceCustom voice){
		JsonResult resultObj = null;
		String data = CustomHandler.toVoiceJson(voice);
		String result = sendCustomMsg(accessToken,data);
		resultObj = ConvertCustomMsg(result);
		return resultObj;
	}
	
	/**
	 * 发送视频客服消息
	 * @param accessToken 授权token
	 * @param video	VideoCustom 对象
	 * @return  JsonResult.errcode=0 && JsonResult.errmsg==ok 表示发送成功
	 */
	public JsonResult sendCustomOfVideo(String accessToken,VideoCustom video){
		JsonResult resultObj = null;
		String data =CustomHandler.toVideoJson(video);
		String result = sendCustomMsg(accessToken,data);
		resultObj = ConvertCustomMsg(result);
		return resultObj;
	}
	
	/**
	 * 发送音乐客服消息
	 * @param accessToken 授权token
	 * @param music		MusicCustom对象
	 * @return  JsonResult.errcode=0 && JsonResult.errmsg==ok 表示发送成功
	 */
	public JsonResult sendCustomOfMusic(String accessToken,MusicCustom music){
		JsonResult resultObj = null;
		String data = CustomHandler.toMusicJson(music);
		String result = sendCustomMsg(accessToken,data);
		resultObj = ConvertCustomMsg(result);
		return resultObj;
	}
	
	/**
	 * 发送图文客服消息
	 * @param accessToken 授权token
	 * @param news		  NewsCustom 对象
	 * @return	JsonResult.errcode=0 && JsonResult.errmsg==ok 表示发送成功
	 */
	public JsonResult sendCustomOfNews(String accessToken,NewsCustom news){
		JsonResult resultObj = null;
		String data = CustomHandler.toNewsJson(news);
		String result = sendCustomMsg(accessToken,data);
		resultObj = ConvertCustomMsg(result);
		return resultObj;
	}
}
