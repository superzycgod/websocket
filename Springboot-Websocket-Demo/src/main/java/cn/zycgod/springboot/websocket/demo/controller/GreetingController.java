package cn.zycgod.springboot.websocket.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@MessageMapping("/topic/hello")
	@SendTo("/topic/greeting")
	public Greeting topicGreeting(HelloMessage message) throws Exception {
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
	public void queueGreeting(HelloMessage message) throws Exception {
		Thread.sleep(1000); // simulated delay
		// 给指定用户发送消息
		Map<String, Object> header = new HashMap<>();
		header.put("persistent ", "true");
		messagingTemplate.convertAndSendToUser(message.getName(), "/queue/greeting",
				new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!"), header);
	}

	@MessageMapping("/queue/hello/user")
	public void queueGreetingUser(HelloMessage message) throws Exception {
		Thread.sleep(1000); // simulated delay
		// 给指定用户发送消息
		Map<String, Object> header = new HashMap<>();
		header.put("persistent ", "true");
		messagingTemplate.convertAndSend("/queue/greeting-" + message.getName(),
				new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!"), header);
	}

	/**
	 * 给指定用户发送消息
	 * 
	 * @param message
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/sendMessage")
	public ResponseEntity<String> sendMessageToSomeOne(HelloMessage message) throws Exception {
		Thread.sleep(1000); // simulated delay
		// 给指定用户发送消息
		messagingTemplate.convertAndSendToUser(message.getName(), "/queue/greeting",
				new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!"));
		return ResponseEntity.ok("Send Message Success!");
	}

}
