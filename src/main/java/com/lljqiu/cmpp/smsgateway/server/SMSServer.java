/**
 * Project Name smsgateway
 * File Name SMSServer.java
 * Package Name com.lljqiu.cmpp.smsgateway.server
 * Create Time 2017年5月13日
 * Create by name：liujie -- email: liujie@lljqiu.com
 */
package com.lljqiu.cmpp.smsgateway.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lljqiu.cmpp.smsgateway.service.ReadMsgService;
import com.lljqiu.cmpp.smsgateway.utils.GatewayConfig;

/** 
 * ClassName: SMSServer.java <br>
 * Description: 短信网关 服务<br>
 * Create by: name：liujie <br>email: liujie@lljqiu.com <br>
 * Create Time: 2017年5月13日<br>
 */
public class SMSServer {
    private static Logger    logger   = LoggerFactory.getLogger(SMSServer.class);
    
    /** 
     * Description：初始化server
     * @param port 监听端口
     * @throws Exception
     * @return void
     * @author name：liujie <br>email: liujie@lljqiu.com
     **/
    @SuppressWarnings("resource")
    public void socketServer(int port) throws Exception {
        ServerSocket serverSocket = new ServerSocket(port);
        logger.info("start server success...");
        Socket socket = serverSocket.accept();
        while (true) {
            // 读取客户端数据    
            String spIp = socket.getInetAddress().getHostAddress();
            DataInputStream input = new DataInputStream(socket.getInputStream());
            byte[] readRequestMessage = ReadMsgService.readRequestMessage(input, spIp);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.write(readRequestMessage);
            out.flush();
            //out.close();
            //input.close();
        }
    }

    /** 
     * Description：启动server服务
     * @return void
     * @author name：liujie <br>email: liujie@lljqiu.com
     **/
    public void start() {
        try {
            logger.info("start server port={}",GatewayConfig.getGatewayPort());
            socketServer(GatewayConfig.getGatewayPort());
        } catch (Exception e) {
            logger.error("init server error{}",e.getMessage());
        }
        
    }

}
