/**
 * Project Name smsgateway
 * File Name ReadMsgService.java
 * Package Name com.lljqiu.cmpp.smsgateway.service
 * Create Time 2017年5月13日
 * Create by name：liujie -- email: liujie@lljqiu.com
 * Copyright © 2015, 2017, www.lljqiu.com. All rights reserved.
 */
package com.lljqiu.cmpp.smsgateway.service;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lljqiu.cmpp.smsgateway.stack.MsgCommand;
import com.lljqiu.cmpp.smsgateway.stack.MsgConnect;
import com.lljqiu.cmpp.smsgateway.stack.MsgHead;

/** 
 * ClassName: ReadMsgService.java <br>
 * Description: <br>
 * Create by: name：liujie <br>email: liujie@lljqiu.com <br>
 * Create Time: 2017年5月13日<br>
 */
public class ReadMsgService {

    private static Logger    logger   = LoggerFactory.getLogger(ReadMsgService.class);
    
    @SuppressWarnings("finally")
    public static MsgHead readHead(byte[] data){
        MsgHead head = new MsgHead();
        ByteArrayInputStream bins = new ByteArrayInputStream(data);
        DataInputStream dins = new DataInputStream(bins);
        try {
            head.setTotalLength(data.length + 4);
            head.setCommandId(dins.readInt());
            head.setSequenceId(dins.readInt());
            dins.close();
            bins.close();
        } catch (IOException e) {
            logger.error("read message head error{}",e.getMessage());
        } finally {
            return head;
        }
    }
    /** 
     * Description：读取请求消息,并且根据connand返回响应
     * @throws IOException
     * @return void
     * @author name：liujie <br>email: liujie@lljqiu.com
     **/
    public static byte[] readRequestMessage(DataInputStream input, String spIp) throws IOException{
        byte[] requestData = null;
        int len = input.readInt();
        if (null != input && 0 != len) {
            byte[] data = new byte[len - 4]; //减4的目的是去除消息长度  头4个字节为消息的长度标识
            input.read(data);
            logger.debug("客户端发送内容为："+Arrays.toString(data));
            requestData   = data;
        }
        
        byte[] result = null;
        logger.debug("请求消息长度："+requestData.length);
        MsgHead head = readHead(requestData);
        logger.debug("请求头消息："+ToStringBuilder.reflectionToString(head));
        switch (head.getCommandId()) {
            case MsgCommand.CMPP_CONNECT:
                MsgConnect connectReq = readConnect(requestData,spIp);
                logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "链接短信网关,version:"
                        + connectReq.getVersion() + " 序列号：" + connectReq.getSequenceId());
                result = PutMsgService.setConnectResp(connectReq);
                break;
        }
        return result;
    }
    
    /** 
     * Description：读取线路连接消息
     * @param requestData
     * @return
     * @throws IOException
     * @return MsgConnect
     * @author name：liujie <br>email: liujie@lljqiu.com
     **/
    public static MsgConnect readConnect(byte[] requestData,String spIp) throws IOException{
        MsgConnect msgConnect = new MsgConnect();
        msgConnect.setSpIp(spIp);
        if (requestData.length == 8 + 6 + 16 + 1 +4) {
            ByteArrayInputStream bins = new ByteArrayInputStream(requestData);
            DataInputStream dins = new DataInputStream(bins);
            try {
                msgConnect.setTotalLength(requestData.length + 4);
                msgConnect.setCommandId(dins.readInt());
                msgConnect.setSequenceId(dins.readInt());
                byte[] sourceAddr = new byte[6];
                dins.read(sourceAddr);
                logger.debug("sourceAddr:"+new String(sourceAddr));
                msgConnect.setSourceAddr(new String(sourceAddr));
                byte[] aiByte = new byte[16];
                dins.read(aiByte);
                msgConnect.setAuthenticatorSource(aiByte);
                
                msgConnect.setVersion(dins.readByte());
                msgConnect.setTimestamp(dins.readInt());
                dins.close();
                bins.close();
            } catch (IOException e) {
                logger.info("read msg connect error{}" + e.getMessage());
            }
        } else {
            logger.info("Analysis packet error, packet length inconsistent. Length:" + requestData.length);
        }
        return msgConnect;
    }
}
