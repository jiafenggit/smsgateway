/**
 * Project Name smsgateway
 * File Name PutMsgService.java
 * Package Name com.lljqiu.cmpp.smsgateway.service
 * Create Time 2017年5月13日
 * Create by name：liujie -- email: liujie@lljqiu.com
 * Copyright © 2015, 2017, www.lljqiu.com. All rights reserved.
 */
package com.lljqiu.cmpp.smsgateway.service;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lljqiu.cmpp.smsgateway.exception.GateWayException;
import com.lljqiu.cmpp.smsgateway.stack.MsgCommand;
import com.lljqiu.cmpp.smsgateway.stack.MsgConnect;
import com.lljqiu.cmpp.smsgateway.stack.MsgConnectResp;

/** 
 * ClassName: PutMsgService.java <br>
 * Description: 拼接返回信息<br>
 * Create by: name：liujie <br>email: liujie@lljqiu.com <br>
 * Create Time: 2017年5月13日<br>
 */
public class PutMsgService {
    private static Logger    logger   = LoggerFactory.getLogger(PutMsgService.class);
    /** 
     * Description：拼接连接请求
     * @param connectReq
     * @return
     * @return byte[]
     * @author name：liujie <br>email: liujie@lljqiu.com
     **/
    public static byte[] setConnectResp(MsgConnect connectReq){
        MsgConnectResp connectResp = new MsgConnectResp();
        connectResp.setTotalLength(12 + 4 + 16 + 1);//消息总长度，级总字节数:4+4+4(消息头)+6+16+1+4(消息主体)
        connectResp.setCommandId(MsgCommand.CMPP_CONNECT_RESP);//标识创建连接
        connectResp.setSequenceId(connectReq.getSequenceId());//序列，由我们指定
        int status = 0x0000;
        try {
            CheckService.checkConnectRequest(connectReq);
        } catch (GateWayException e) {
            status = e.getErrorCode();
        }
        connectResp.setStatus(status);
        connectResp.setAuthenticatorISMG(connectReq.getAuthenticatorSource());
        connectReq.setVersion(connectReq.getVersion());
        logger.debug("{}响应消息{}",MsgCommand.CMPP_CONNECT_RESP,ToStringBuilder.reflectionToString(connectResp));
        return connectResp.toByteArry();
    }
}
