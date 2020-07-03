package cn.zycgod.springboot.websocket.demo.config;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * 
 * <b>什么是Stomp</b>
 * <p>
 * STOMP即Simple (or Streaming) Text Orientated Messaging
 * Protocol，简单(流)文本定向消息协议，它提供了一个可互操作的连接格式，允许STOMP客户端与任意STOMP消息代理（Broker）进行交互。
 * STOMP协议由于设计简单，易于开发客户端，因此在多种语言和多种平台上得到广泛地应用。
 * <p>
 * <b>demo配置</b>
 * <p>
 * Stomp Client：使用的是stomp.js<br>
 * Stomp Broker：Spring内置的基于的内存的broker
 * 
 * 
 * @author zhangyanchao
 */
@Configuration
@EnableWebSocketMessageBroker
@ConditionalOnProperty(name = "stomp.broker", havingValue = "rabbitmq", matchIfMissing = false)
public class WebSocketRabbitmqMessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

	private final Logger logger = LoggerFactory.getLogger(WebSocketRabbitmqMessageBrokerConfig.class);

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		// @formatter:off
		
		// 使用rabbitmq作为StompBroker
		config.enableStompBrokerRelay("/topic", "/queue")
        .setRelayHost("docker01")       // rabbitmq-host服务器地址
        .setRelayPort(61613)            // rabbitmq-stomp 服务器服务端口
        .setClientLogin("guest")        // 登陆账户
        .setClientPasscode("guest")     // 登陆密码
        .setUserDestinationBroadcast("/topic/unresolved-user-destination")
        .setUserRegistryBroadcast("/topic/simp-user-registry");   
		
		/**
		 * 设置应用目的地前缀，过滤并处理stompClient.send命令
		 * <p>
		 * 例如：stompClient.send("/app/*")时，会转发到<code>@MessageMapping</code>注解的方法
		 */
		config.setApplicationDestinationPrefixes("/app");
		
		/**
		 * 设置用户目的地前缀
		 * <p>
		 * 例如：stompClient.subscribe('/user/queue/greeting')时，
		 */
		config.setUserDestinationPrefix("/user");
		
		// @formatter:on
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
				logger.info("preSend message is {}", message);
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					// TODO 这里监听了CONNECT命令，可以从header中获取用户鉴权信息进行鉴权操作
					final String username = accessor.getNativeHeader("username").get(0);
					logger.info("User '{}' connected!", username);
					// 为websocket连接绑定登录用户信息
					// Spring will note and save the authenticated user and associate it with subsequent STOMP messages on the same session:
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
