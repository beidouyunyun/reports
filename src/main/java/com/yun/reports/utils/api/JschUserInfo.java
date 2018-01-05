package com.yun.reports.utils.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.UserInfo;

public class JschUserInfo implements UserInfo {

	private static final Logger logger = LoggerFactory.getLogger(JschUserInfo.class);
	
	@Override
    public String getPassphrase() {
        // TODO Auto-generated method stub
        logger.debug("JschUserInfo.getPassphrase()");
        return null;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        logger.debug("JschUserInfo.getPassword()");
        return null;
    }

    @Override
    public boolean promptPassphrase(String arg0) {
        // TODO Auto-generated method stub
        logger.debug("JschUserInfo.promptPassphrase():" + arg0);
        return false;
    }

    @Override
    public boolean promptPassword(String arg0) {
        // TODO Auto-generated method stub
        logger.debug("JschUserInfo.promptPassword():" + arg0);
        return false;
    }

    @Override
    public boolean promptYesNo(String arg0) {
        // TODO Auto-generated method stub'
         logger.debug("JschUserInfo.promptYesNo():" + arg0);
         if (arg0.contains("The authenticity of host")) {  
             return true;  
         }  
        return true;
    }

    @Override
    public void showMessage(String arg0) {
        // TODO Auto-generated method stub
        logger.debug("JschUserInfo.showMessage()");
    }

}
