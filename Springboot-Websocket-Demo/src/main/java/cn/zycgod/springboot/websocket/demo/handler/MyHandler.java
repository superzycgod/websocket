package cn.zycgod.springboot.websocket.demo.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyHandler extends TextWebSocketHandler {

	private final Logger logger = LoggerFactory.getLogger(MyHandler.class);

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		logger.info("receive message : session = {}, message = {}", session, message.getPayload());
	}

}
