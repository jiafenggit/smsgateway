/**
 * Project Name smsgateway
 * File Name CheckService.java
 * Package Name com.lljqiu.cmpp.smsgateway.service
 * Create Time 2017年5月13日
 * Create by name：liujie -- email: liujie@lljqiu.com
 * Copyright © 2015, 2017, www.lljqiu.com. All rights reserved.
 */
package com.lljqiu.cmpp.smsgateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.lljqiu.cmpp.smsgateway.cache.EhCache;
import com.lljqiu.cmpp.smsgateway.exception.GateWayException;
import com.lljqiu.cmpp.smsgateway.stack.MsgConnect;
import com.lljqiu.cmpp.smsgateway.utils.Constants;
import com.lljqiu.cmpp.smsgateway.utils.GatewayConfig;

/** 
 * ClassName: CheckService.java <br>
 * Description: 校验请求<br>
 * Create by: name：liujie <br>email: liujie@lljqiu.com <br>
 * Create Time: 2017年5月13日<br>
 */
public class CheckService {
    private static Logger logger = LoggerFactory.getLogger(CheckService.class);

    public static void checkConnectRequest(MsgConnect connectReq) {
        logger.debug("spIp={},AuthenticatorSource={}",connectReq.getSpIp(),connectReq.getAuthenticatorSource());
        JSONObject json = (JSONObject) EhCache.get(EhCache.CACHE_NAME, connectReq.getSpIp());
        GateWayException.checkCondition(json.isEmpty(), 0x0002, "非源地址");
        GateWayException.checkCondition(!json.getString(Constants.SPID).equals(connectReq.getSourceAddr()), 0x0002,
                "非源地址");
//        boolean flag = GateWayUtils.checkAuthenticatorSource(connectReq.getSourceAddr(),
//                json.getString(Constants.SHAREDSECRET), connectReq.getTimestamp() + "",connectReq.getAuthenticatorSource());
        boolean flag = json.getString(Constants.SPID).equals(connectReq.getSourceAddr());
        GateWayException.checkCondition(!flag, 0x0003, "认证失败");
        GateWayException.checkCondition(connectReq.getVersion() > GatewayConfig.getGatewayVersion(), 0x0004, "版本太高");
        

    }

}
