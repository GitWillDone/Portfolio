package server;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Generates an HTTPRequest. During this process, commands are placed into a HashMap and there is an examination of
 * whether or not the request is a WebSocket request or regular.
 */
public class HTTPRequest {
    private boolean isWebSocket;
    private HashMap<String, String> headerMap;
    private String fileName;
    private String protocol;
    private String method;
    private Scanner readClient;


    public HTTPRequest(Socket clientSocket) {
        isWebSocket = false;
        headerMap = new HashMap<String, String>();

        try {
            readClient = new Scanner(clientSocket.getInputStream());
            //get all the information needed for the GET "resource" HTTP/1.1
            method = readClient.next(); //gets header information
            fileName = readClient.next();
            protocol = readClient.next();
            readClient.nextLine();

            //grabs the value of the request to evaluate for an upgrade.
            while (true) {
                String line = readClient.nextLine();  //takes in lines that will split by : to find Sec-WebSocket-Key
                if (line.isEmpty()) {
                    break;
                }

                String[] keyValue = line.split(": ");

                if (keyValue.length > 1) {
                    headerMap.put(keyValue[0], keyValue[1]);
                }
            }
            isWebSocket = headerMap.containsKey("Sec-WebSocket-Key");
//            if(headerMap.containsKey("Sec-WebSocket-Key")) {
//                System.out.println("This is a WebSocket:  "+ isWebSocket);
//                isWebSocket = true;
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ///used to determine the requirement of the response
    public boolean getIsWebSocket() {
        return isWebSocket;
    }

    ///returns the headermap used in the WebSocketResponse to generate the mogic string
    public HashMap<String, String> getHeaderMap() {
        return headerMap;
    }

    ///used in the regular response to build the file path
    public String getFileName() {
        return fileName;
    }
}
