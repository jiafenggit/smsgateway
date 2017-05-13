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
import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lljqiu.cmpp.smsgateway.stack.MsgCommand;
import com.lljqiu.cmpp.smsgateway.stack.MsgConnect;
import com.lljqiu.cmpp.smsgateway.stack.MsgHead;
import com.lljqiu.cmpp.smsgateway.stack.MsgSubmit;
import com.lljqiu.cmpp.smsgateway.utils.GateWayUtils;

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
                logger.info("<链接短信网关,version:" + connectReq.getVersion() + " 序列号：" + connectReq.getSequenceId()+">");
                result = PutMsgService.setConnectResp(connectReq);
                break;
            case MsgCommand.CMPP_SUBMIT:
                MsgSubmit submitReq = readSubmit(requestData);
                logger.debug(ToStringBuilder.reflectionToString(submitReq));
                logger.info("<下发短信,手机号:" + submitReq.getDestTerminalId() + " 序列号：" + submitReq.getSequenceId()+">");
                logger.info("<下发短信,短信内容:" + submitReq.getStrMsgContent() + " 序列号：" + submitReq.getSequenceId()+">");
                break;
        }
        return result;
    }
    
    /** 
     * Description：读取提交信息消息
     * @param requestData
     * @return
     * @return MsgSubmit
     * @author name：liujie <br>email: liujie@lljqiu.com
     **/
    private static MsgSubmit readSubmit(byte[] requestData) {
        logger.info("<接受Submit Message>");
        MsgSubmit submitReq = new MsgSubmit();
        ByteArrayInputStream bins = new ByteArrayInputStream(requestData);
        DataInputStream dins = new DataInputStream(bins);
        try {
            submitReq.setTotalLength(requestData.length + 4);
            submitReq.setCommandId(dins.readInt());
            submitReq.setSequenceId(dins.readInt());
            
            byte[] Msg_Id = new byte[8];
            dins.read(Msg_Id);
            submitReq.setMsgId(GateWayUtils.Bytes8ToLong(Msg_Id));
            
            byte[] Pk_total = new byte[1];
            dins.read(Pk_total);
            submitReq.setPkTotal(GateWayUtils.byteToInt(Pk_total[0]));
            
            byte[] Pk_number = new byte[1];
            dins.read(Pk_number);
            submitReq.setPkNumber(GateWayUtils.byteToInt(Pk_number[0]));
            
            byte[] Registered_Delivery = new byte[1];
            dins.read(Registered_Delivery);
            submitReq.setRegisteredDelivery(GateWayUtils.byteToInt(Registered_Delivery[0]));
            
            byte[] Msg_level = new byte[1];
            dins.read(Msg_level);
            submitReq.setMsgLevel(GateWayUtils.byteToInt(Msg_level[0]));
            
            byte[] Service_Id = new byte[10];
            dins.read(Service_Id);
            submitReq.setServiceId(new String(Service_Id,0,10));
            
            byte[] Fee_UserType = new byte[1];
            dins.read(Fee_UserType);
            submitReq.setFeeUserType(GateWayUtils.byteToInt(Fee_UserType[0]));
            
            byte[] Fee_terminal_Id = new byte[32];
            dins.read(Fee_terminal_Id);
            submitReq.setFeeTerminalId(new String(Fee_terminal_Id,0,32));
            
            byte[] Fee_terminal_type = new byte[1];
            dins.read(Fee_terminal_type);
            submitReq.setFeeTerminalType(GateWayUtils.byteToInt(Fee_terminal_type[0]));
            
            byte[] TP_pId = new byte[1];
            dins.read(TP_pId);
            submitReq.setTpPId(GateWayUtils.byteToInt(TP_pId[0]));
            
            byte[] TP_udhi = new byte[1];
            dins.read(TP_udhi);
            submitReq.setTpUdhi(GateWayUtils.byteToInt(TP_udhi[0]));
            
            byte[] Msg_Fmt = new byte[1];
            dins.read(Msg_Fmt);
            submitReq.setMsgFmt(GateWayUtils.byteToInt(Msg_Fmt[0]));
            
            byte[] Msg_src = new byte[6];
            dins.read(Msg_src);
            submitReq.setMsgSrc(new String(Msg_src,0,6));
            
            byte[] FeeType = new byte[2];
            dins.read(FeeType);
            submitReq.setFeeType(new String(FeeType,0,2));
            
            byte[] FeeCode = new byte[6];
            dins.read(FeeCode);
            submitReq.setFeeCode(new String(FeeCode,0,6));
            
            byte[] ValId_Time = new byte[17];
            dins.read(ValId_Time);
            submitReq.setValIdTime(new String(ValId_Time,0,17));
            
            byte[] At_Time = new byte[17];
            dins.read(At_Time);
            submitReq.setAtTime(new String(At_Time,0,17));
            
            byte[] Src_Id = new byte[21];
            dins.read(Src_Id);
            submitReq.setSrcId(new String(Src_Id,0,21));
            
            byte[] DestUsr_tl = new byte[1];
            dins.read(DestUsr_tl);
            int destUsrTl = GateWayUtils.byteToInt(DestUsr_tl[0]);
            submitReq.setDestUsrTl(destUsrTl);
            
            int Dest_terminal_Id_length = 32*destUsrTl;
            byte[] Dest_terminal_Id = new byte[Dest_terminal_Id_length];
            dins.read(Dest_terminal_Id);
            submitReq.addDestTerminalId(new String(Dest_terminal_Id,0,Dest_terminal_Id_length));
            
            byte[] Dest_terminal_type = new byte[1];
            dins.read(Dest_terminal_type);
            submitReq.setDestTerminalType(GateWayUtils.byteToInt(Dest_terminal_type[0]));
            
            byte[] Msg_Length = new byte[1];
            dins.read(Msg_Length);
            int msgLength = GateWayUtils.byteToInt(Msg_Length[0]);
            submitReq.setMsgLength(msgLength);
           
            byte[] Msg_Content = new byte[msgLength];
            dins.read(Msg_Content);
            submitReq.setMsgContent(Msg_Content);
            
            byte[] LinkID = new byte[20];
            dins.read(LinkID);
            submitReq.setLinkID(new String(LinkID,0,20));
            
            dins.close();
            bins.close();
        } catch (IOException e) {
            logger.info("read Submit Message error{}" + e.getMessage());
        }
        return submitReq;
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
        logger.info("<接受链路请求>");
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
