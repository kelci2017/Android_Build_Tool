var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected********: ' + frame);
        console.log("aaaaaaaaaaaaaaaaaaa***************");
        stompClient.subscribe('/topic/greetings', function (greeting) {
            console.log('the topic is subscribed');
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'versionCode': $("#versionCode").val(), 'versionName': $("#versionName").val(), 'commitNo': $("#commit_number").val()}));
    $( "#build_button" ).attr("disabled", true);
}

function showGreeting(message) {
    console.log("________________________");
    // $("#greetings").append("<tr><td>" + message +  "</td></tr>");
    $("#outputElement").append("<p>" + message +  "</p>");
    if (message.indexOf("Nihao") != -1) {
        $( "#build_button" ).attr("disabled", false);
    }
}

$(function () {
    $( "#build_button" ).click(function() { sendName(); });
    $( "#local" ).click(function() {loadLocal();});
    $( "#git" ).click(function() { loadGit(); });

});

function loadLocal() {
    console.log("local local html");
    $("#local_build").style.visibility = "visible";
    $("#git_build").style.visibility = "hidden";
}

function loadGit() {
    $("#local_build").style.visibility = "hidden";
    $("#git_build").style.visibility = "visible";
}

