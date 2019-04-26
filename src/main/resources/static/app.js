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
    var socket = new SockJS('/build-android-app');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected********: ' + frame);
        stompClient.subscribe('/topic/buildlog', function (greeting) {
            console.log('the topic is subscribed');
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
}

function sendName() {
    stompClient.send("/app/build", {}, JSON.stringify({'localDirectory': $("#localDirectory").val(), 'gitUrl': $("#gitUrl").val(), 'commitNo': $("#commit_number").val()}));
    $( "#build_button" ).attr("disabled", true);
}

function showGreeting(message) {
    $("#outputElement").append("<p>" + message +  "</p>");
    if (message.indexOf("actionable") != -1) {
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
    $("#local_build").css("visibility", "visible")
    $("#git_build").css("visibility", "hidden")
    $('#build_button').css('margin-top',-160);
    $("#local").css("background-color", "#D3D3D3");
    $("#git").css("background-color", "#F5F5F5");
}

function loadGit() {
    $("#local_build").css("visibility", "visible")
    $("#git_build").css("visibility", "visible")
    $('#build_button').css('margin-top',20);
    $("#local").css("background-color", "#F5F5F5");
    $("#git").css("background-color", "#D3D3D3");
}

