package com.spring.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

public class BasicJmsChat implements MessageListener {
@Autowired
	private Topic topic;
@Autowired
private JmsTemplate chattemplate;
private static String userid;
void subscribe(TopicConnection tc,Topic chattopic,BasicJmsChat bjc) throws JMSException{
	
	
	
	TopicSession session =tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
	TopicSubscriber Ts=session.createSubscriber(chattopic);
	Ts.setMessageListener(bjc);
}
public void setTopic(Topic topic) {
	this.topic = topic;
}
public void setChattemplate(JmsTemplate chattemplate) {
	this.chattemplate = chattemplate;
}
void publish(TopicConnection tc,Topic chattopic,String userid)throws JMSException ,IOException{
	TopicSession session =tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
	TopicPublisher tp=session.createPublisher(chattopic);
	tc.start();
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	while(true)
	{
		String msgtosend= reader.readLine();
		if(msgtosend.equals("exit"))
		{	tc.close();
		System.exit(0);
		
		}else{
		TextMessage msg=session.createTextMessage();
		msg.setText("\n"+userid+" "+msgtosend);
		tp.publish(msg);
		}
	}
	
}

public static void main(String[] args) throws JMSException,IOException {
	
	userid="pp";
	
		ApplicationContext appcontext=new ClassPathXmlApplicationContext("app-context.xml");
	BasicJmsChat bean = appcontext.getBean(BasicJmsChat.class);
TopicConnectionFactory connectionFactory = (TopicConnectionFactory) bean.chattemplate.getConnectionFactory();

	TopicConnection tc = connectionFactory.createTopicConnection();
	bean.publish(tc, bean.topic, userid);
	bean.subscribe(tc, bean.topic, bean);
	

}
@Override
public void onMessage(Message message) {
	// TODO Auto-generated method stub
	
	if(message instanceof TextMessage)
	{
		
		try{
			
			String mes=((TextMessage) message).getText();
			if(!mes.startsWith("["+userid)){
				
				System.out.println(mes);
			}
		}
		catch(Exception e){}
	}
	
	
}
}
