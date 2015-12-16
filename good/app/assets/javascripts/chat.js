var messageTemplate = $("#message-template");
var messagesContainer = $("#messages");
var messageInput = $("#message-input");
var newMessage = Handlebars.compile(messageTemplate.html());


var ws = new WebSocket(messagesContainer.data("ws-url"));

ws.onmessage = function(event)  {
    var message = JSON.parse(event.data);
    messagesContainer.append(newMessage({"username":message.user, "message":message.text}));
};

$( "#message-form" ).submit(function( event ) {
    var input = messageInput.val();
    messageInput.val("");
    ws.send(input);
    event.preventDefault();
});