window.onload = function () {

    let sock = new WebSocket("ws://" + location.host);

    console.log("In sec");

    sock.onopen = function(){
        console.log("handshake complete")
        sock.send("This is a test");

        setInterval(sock.send("This is the follow-on handshake"), 2000);
    }



}