var stompClient = null;

function setConnected(connected) {
	$("#connect").prop("disabled", connected);
	$("#disconnect").prop("disabled", !connected);
	if (connected) {
		$("#conversation").show();
	} else {
		$("#conversation").hide();
	}
	$("#greetings").html("");
}

function connect() {
	var socket = new SockJS('/gs-guide-websocket');
	stompClient = Stomp.over(socket);
	stompClient.connect({
		// headers
		username : "zhangsan"
	}, function(frame) {
		setConnected(true);
		console.log('Connected: ' + frame);
		
		// 订阅主题目的地(destination) -> /topic/greeting
		stompClient.subscribe('/topic/greeting', function(greeting) {
			showGreeting(JSON.parse(greeting.body).content);
		});
		
		// 订阅只有当前用户能收到的消息
		stompClient.subscribe('/user/queue/greeting', function(greeting) {
			showGreeting(JSON.parse(greeting.body).content);
		});

	});
}

function disconnect() {
	if (stompClient !== null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
}

function sendTopic() {
	stompClient.send("/app/topic/hello", {}, JSON.stringify({
		'name' : $("#name").val()
	}));
}

function sendQueue() {
	stompClient.send("/app/queue/hello", {}, JSON.stringify({
		'name' : $("#name").val()
	}));
}

function showGreeting(message) {
	$("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function() {
	$("form").on('submit', function(e) {
		e.preventDefault();
	});
	$("#connect").click(function() {
		connect();
	});
	$("#disconnect").click(function() {
		disconnect();
	});
	$("#send").click(function() {
		sendTopic();
	});
	$("#sendSomeone").click(function() {
		sendQueue();
	});
});
