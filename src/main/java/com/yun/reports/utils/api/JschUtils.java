package com.yun.reports.utils.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JschUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(JschUtils.class);
	
    private String hostIp;						//远程主机的hostIp地址
    
    private String userName;					//远程主机登录用户名
    
    private String passWord;					//远程主机的登录密码
    
    public static final int DEFAULT_SSH_PORT = 2222;  	//设置ssh连接的远程端口
    
    private Integer timeOut = 60*1000;
    
    private String fileDir;
    private String fileName;
    private String whiteList;
    private String blackList;
    
    private ArrayList<String> stdout;			//保存输出内容的容器
    
    private Session session;
    Channel channel = null;
    private String charset = "UTF-8";
    
    /**
     * 初始化登录信息
     * @param hosthosthosthosthostIp
     * @param userName
     * @param passWord
     */
    public JschUtils(final String hostIp, final String userName, final String passWord) {
         this.hostIp = hostIp;
         this.userName = userName;
         this.passWord = passWord;
         stdout = new ArrayList<String>();
    }
    
    /**
     * 连接测试
     * @return
     */
    public int connect() {
        int returnCode = 0;
        JSch jsch = new JSch();
        JschUserInfo userInfo = new JschUserInfo();

        try {
            //创建session并且打开连接，因为创建session之后要主动打开连接
            Session session = jsch.getSession(userName, hostIp, DEFAULT_SSH_PORT);
            session.setPassword(passWord);
            session.setUserInfo(userInfo);
            session.connect();

            //关闭session
            session.disconnect();
            return returnCode;
        } catch (JSchException e) {
            e.printStackTrace();
            return returnCode = 1;
        } catch (Exception e) {
            e.printStackTrace();
            return returnCode = 111;
        }
    }
    
    
    /** 
     * 连接到指定的服务器 
     * @return 
     * @throws JSchException 
     */  
    public boolean connectTest() throws JSchException {  
  
    	JSch jsch = new JSch();// 创建JSch对象  
        boolean result = false;  
  
        try{  
  
            long begin = System.currentTimeMillis();//连接前时间  
            logger.debug("Try to connect to hostIp = " + hostIp + ",as userName = " + userName + ",as jschPort =  " + DEFAULT_SSH_PORT);  
  
            session = jsch.getSession(userName, hostIp, DEFAULT_SSH_PORT);// // 根据用户名，主机ip，端口获取一个Session对象  
            session.setPassword(passWord); // 设置密码  
            Properties config = new Properties();  
            config.put("StrictHostKeyChecking", "no");  
            session.setConfig(config);// 为Session对象设置properties  
            session.setTimeout(timeOut);//设置连接超时时间  
            session.connect();  
  
            logger.debug("Connected successfully to hostIp = " + hostIp + ",as userName = " + userName + ",as jschPort = " + DEFAULT_SSH_PORT);  
  
            long end = System.currentTimeMillis();//连接后时间  
  
            logger.debug("Connected To SA Successful in {} ms", (end-begin));  
  
            result = session.isConnected();  
  
        }catch(Exception e){  
            logger.error(e.getMessage(), e);  
        }finally{  
            if(result){  
                logger.debug("connect success");  
            }else{  
                logger.debug("connect failure");  
            }  
        }  
  
        if(!session.isConnected()) {  
            logger.error("获取连接失败");  
        }  
  
        return  session.isConnected();  
  
    }  
    
    /** 
     * 关闭连接 
     */  
    public void close() {  
  
        if(channel != null && channel.isConnected()){  
            channel.disconnect();  
            channel=null;  
        }  
  
        if(session!=null && session.isConnected()){  
            session.disconnect();  
            session=null;  
        }  
  
    } 
    
    /**
     * 执行shell命令
     * @param command
     * @return
     */
    public Map<String,Object> executeExec(final String command) {
    	
    	Map<String,Object> mapResult = new HashMap<String,Object>();
    	
        int returnCode = 0;
        JSch jsch = new JSch();
        JschUserInfo userInfo = new JschUserInfo();

        try {
            //创建session并且打开连接，因为创建session之后要主动打开连接
            Session session = jsch.getSession(userName, hostIp, DEFAULT_SSH_PORT);
            session.setPassword(passWord);
            session.setUserInfo(userInfo);
            session.connect();

            //打开通道，设置通道类型，和执行的命令
            Channel channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec)channel;
            channelExec.setCommand(command);

            channelExec.setInputStream(null);
            channelExec.setErrStream(null);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(channelExec.getInputStream(),charset));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(channelExec.getErrStream(),charset));

            channelExec.connect();
            logger.debug("The remote command is :" + command);

            //接收远程服务器执行命令的结果
            String line;
            while ((line = stdInput.readLine()) != null) {  
                stdout.add(line);  
                logger.debug(line);
            }  
            stdInput.close();  
            
            while ((line = stdError.readLine()) != null) {  
                stdout.add(line);  
                logger.debug(line);
            }  
            stdError.close();  

            //得到returnCode
            if (channelExec.isClosed()) {  
                returnCode = channelExec.getExitStatus();  
            }  
            mapResult.put("stdout", stdout);
            mapResult.put("returnCode", returnCode);
            //关闭通道
            channelExec.disconnect();
            //关闭session
            session.disconnect();

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapResult;
    }
    
    /**
     * 执行shell命令
     * @param command
     * @return
     */
    public Map<String,Object> executeShell(final String command) {
    	
    	Map<String,Object> mapResult = new HashMap<String,Object>();
    	
        int returnCode = 0;
        JSch jsch = new JSch();
        JschUserInfo userInfo = new JschUserInfo();

        try {
            //创建session并且打开连接，因为创建session之后要主动打开连接
            Session session = jsch.getSession(userName, hostIp, DEFAULT_SSH_PORT);
            session.setPassword(passWord);
            session.setUserInfo(userInfo);
            session.connect();

            //打开通道，设置通道类型，和执行的命令
            Channel channel = session.openChannel("shell");
            ChannelExec channelExec = (ChannelExec)channel;
            channelExec.setCommand(command);

            channelExec.setInputStream(null);
            channelExec.setErrStream(null);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(channelExec.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(channelExec.getErrStream()));

            channelExec.connect();
            logger.debug("The remote command is :" + command);

            //接收远程服务器执行命令的结果
            String line;
            while ((line = stdInput.readLine()) != null) {  
                stdout.add(line);  
            }  
            stdInput.close();  
            
            while ((line = stdError.readLine()) != null) {  
                stdout.add(line);  
            }  
            stdError.close();  

            //得到returnCode
            if (channelExec.isClosed()) {  
                returnCode = channelExec.getExitStatus();  
            }  
            mapResult.put("stdout", stdout);
            mapResult.put("returnCode", returnCode);
            //关闭通道
            channelExec.disconnect();
            //关闭session
            session.disconnect();

        } catch (JSchException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapResult;
    }

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ArrayList<String> getStdout() {
		return stdout;
	}

	public void setStdout(ArrayList<String> stdout) {
		this.stdout = stdout;
	}

	public String getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(String whiteList) {
		this.whiteList = whiteList;
	}

	public String getBlackList() {
		return blackList;
	}

	public void setBlackList(String blackList) {
		this.blackList = blackList;
	}
}

