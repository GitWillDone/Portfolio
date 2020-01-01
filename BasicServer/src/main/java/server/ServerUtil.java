package server;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.*;
import java.util.HashMap;

/**
 * A TCP server that runs on port 8080.  When a client connects, it sends the client the current date and time, then closes the
 * connection with that client.
 * To connect you must enter the local host 8080 followed by the resource request.  The chatroom is /ex.html
 * Overall, you should type localhost:8080/ex.html                  <-------- to connect
 * By default, no resource request will bring you to ex.html
 */
public class ServerUtil {
    private static HashMap<String, Room> userRoomMap = new HashMap<>();

    public static void main(String[] args) throws IOException {
        //open the server socket on the localhost port 8080
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(true); //block the serversocketchannel or else it will jump to start when created


        //must wrap in a while(true) because the machine is essentially listening for requests for connections to occur
        while (true) {
            SocketChannel client = serverSocketChannel.accept();

            Thread thread = new Thread(() -> { //create an initial thread.
                HTTPRequest request = new HTTPRequest(client.socket());

                if (!request.getIsWebSocket()) { //if the request doesn't require an upgrade
                    HTTPResponse response = new HTTPResponse(request, client.socket());
                    //close the socket after the response
                    try {
                        client.socket().close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    WebSocketResponse.handshakeBack(request, client.socket()); //check for a successful handshake
                    //first to join
                    WebSocketResponse response = new WebSocketResponse();
                    response.decode(client.socket());

                    if (userRoomMap.containsKey(response.getRoomName())) {
                        userRoomMap.get(response.getRoomName()).addClient(client);
                    } else {
                        Room chatroom = new Room(response.getRoomName());
                        try {
                            userRoomMap.put(response.getRoomName(), chatroom);
                            chatroom.addClient(client);
                            chatroom.listenForClient();
                        } catch (FileNotFoundException eFNF) {
                            System.out.println("Client entered an unavailable resource");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }
}

