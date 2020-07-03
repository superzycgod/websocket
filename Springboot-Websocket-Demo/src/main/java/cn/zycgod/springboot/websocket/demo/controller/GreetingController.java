package cn.zycgod.springboot.websocket.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/topic/hello")
	@SendTo("/topic/greeting")
	public Greeting greeting1(HelloMessage message) throws Exception {
		Thread.sleep(1000); // simulated delay
		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
	}

//	@MessageMapping("/queue/hello")
//	@SendToUser("/queue/greeting")
//	public Greeting greeting2(HelloMessage message) throws Exception {
//		Thread.sleep(1000); // simulated delay
//		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
//	}

	@MessageMapping("/queue/hello")
	@SendToUser("/queue/greeting")
	public void greeting2(HelloMessage message) throws Exception {
		Thread.sleep(1000); // simulated delay
		// 给指定用户发送消息
		messagingTemplate.convertAndSendToUser(message.getName(), "/queue/greeting",
				new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!"));
	}

}
