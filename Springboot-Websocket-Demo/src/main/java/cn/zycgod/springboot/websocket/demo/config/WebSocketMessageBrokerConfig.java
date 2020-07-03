package cn.zycgod.springboot.websocket.demo.config;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

	private final Logger logger = LoggerFactory.getLogger(WebSocketMessageBrokerConfig.class);

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic", "/queue");
		/**
		 * 设置应用目的地前缀，过滤并处理stompClient.send命令
		 * <p>
		 * 例如：stompClient.send("/app/*")时，会转发到@MessageMapping注解的方法
		 */
		config.setApplicationDestinationPrefixes("/app");
		/**
		 * 设置用户目的地前缀
		 * <p>
		 * 例如：stompClient.subscribe('/user/queue/greeting')时，
		 */
		config.setUserDestinationPrefix("/user");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/gs-guide-websocket").withSockJS();
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				logger.info("Stomp Command is {}", accessor.getCommand());
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					// TODO 这里可以进行鉴权
					final String username = accessor.getNativeHeader("username").get(0);
					logger.info("User '{}' connected!", username);
					// 为websocket连接绑定登录用户信息
					accessor.setUser(new Principal() {
						@Override
						public String getName() {
							return (String) username;
						}
					});
				}
				return message;
			}
		});
	}

}
