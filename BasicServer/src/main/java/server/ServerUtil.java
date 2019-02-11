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
 */
public class ServerUtil {
    private static HashMap<String, Room> userRoomMap = new HashMap<>();
//    private static Connection connection;

    public static void main(String[] args) throws IOException {
        //open the server socket on the localhost port 8080
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(true); //block the serversocketchannel or else it will jump to start when created

//        try {
//            //TODO you must run this at least once!!!
//            //create database file
//            connection = DriverManager.getConnection("jdbc:sqlite:chatHistory.db");
//            Statement statement = connection.createStatement();
//            String template = "CREATE TABLE chatHistory (\n" + "room text,\n" + "user text,\n" + "msg text\n" + ");";
//            //insert into the file
//            String message = "INSERT INTO chatHistory(room, user, message) VALUES('room', 'will', 'start')";
//            statement.executeUpdate(message);
//            statement.close();
//            Statement statement2 = connection.createStatement(); //you must first create the statement, then do the query
//            ResultSet rs = statement2.executeQuery("SELECT * FROM chatHistory");
//            while(rs.next()){
//                System.out.println(rs.getString("room"));
//                System.out.println(rs.getString("user"));
//                System.out.println(rs.getString("message"));
//            }
//
//        } catch (SQLException e){
//            e.getSQLState();
//        }

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

