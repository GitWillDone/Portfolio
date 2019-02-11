window.onload = function () {
    let join = document.getElementById("submit");
    let mySocket, username, chatroom, send, chat;

    ///test works in conjunction with the onopen(callback) to load the prior messages.  The username will show up as "You"
    //instead of the actual name entered at the login screen.
    function test(event) {
        //console.log(event);
        if (event != undefined) {
            let temp = JSON.parse(event.data);
            chat = document.getElementById('chat');
            //alters the alignment and changes name to "You" for identification
            if (temp.user == username) {
                temp.user = "You";
                chat.value += "\n" + temp.user + ": " + temp.message + "\n";
            } else {
                chat.value += "\n" + temp.user + ": " + temp.message + "\n";
            }
            chat.scrollTop = chat.scrollHeight; //allows for scrolling
            document.getElementById('message').value = "";
        }
    }

    ///by utilizing the callback(), the past messages propagate
    function onopen(callback){
        mySocket.onopen = function () {
            mySocket.send("join " + chatroom); //enter the desired chatroom
            mySocket.onmessage = test;
        }
        callback();
    }

    //where the xhr and websocket is created
    join.addEventListener("click", function () {
        username = document.getElementById('username').value;
        chatroom = document.getElementById('chatroomName').value.toLowerCase();

        let xhr = new XMLHttpRequest();
        mySocket = new WebSocket("ws://" + location.host);
        xhr.overrideMimeType("text/plain");
        xhr.addEventListener("load", setPage);
        xhr.open("GET", "body.html");
        xhr.send();


//         mySocket.onopen = function () {
//             mySocket.send("join " + chatroom);
//             mySocket.onmessage = test;
//         }

        onopen(test);

    });

    ///establishes the change of the ex.html to the body.html and creates a listener for the chat button
    function setPage() {
        document.body.innerHTML = this.response;
        send = document.getElementById('send');
        let field = document.getElementById('message');
        send.addEventListener("click", function () {
            let message = field.value;
            mySocket.send(username + " " + message);
        });

        field.addEventListener("keyup", function (event) {
            if (event.key === 'Enter') {
                send.click();
            }
        });
    }

    //creates the 'Return' key functionality
    let returnFunc = document.getElementById('chatroomName');
    returnFunc.addEventListener("keyup", function (event) {
        if (event.key === 'Enter') {
            join.click();
        }
    });
}