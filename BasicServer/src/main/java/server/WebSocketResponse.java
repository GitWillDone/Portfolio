package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Generates a WebSocketResponse.  After the response is created, the message sent to the server is decoded.
 */
public class WebSocketResponse {
    private String userName, msg;
    private byte[] headerMessage, decoded, jsonBytes;

    ///Listens for a client's message and then decodes the text into bytes.  Member variables, such as msg, are set here
    ///so they can be pulled for later use.
    public void decode(Socket clientSocket){
        try {
            //wait for a message from the client
            DataInputStream dataIn = new DataInputStream(clientSocket.getInputStream());

            headerMessage = new byte[2];
            dataIn.read(headerMessage, 0, 2);

            byte secondByte = (byte) (headerMessage[1] & (byte) 127);
            int payloadLen = 0;

            byte[] extraLen;
            if (secondByte <= 125) {
                payloadLen = secondByte;
            }else if (secondByte == 126) {
                extraLen = new byte[2];
                dataIn.read(extraLen, 0, 2);
                payloadLen = (extraLen[0] << 8)|extraLen[1];
            } else if (secondByte  == 127) {
                extraLen = new byte[8];
                for (int i = 0; i < extraLen.length; i++) {
                    payloadLen = (payloadLen << 8) + (extraLen[i] & 0xff);
                }
            }

            byte[] key = new byte[4];
            dataIn.read(key, 0, 4);

            byte[] encoded = new byte[payloadLen];
            dataIn.read(encoded);

            decoded = new byte[payloadLen];
            for (int i = 0; i < payloadLen; i++) {
                decoded[i] = (byte) (encoded[i] ^ key[i & 0x3]);
            }

            String s = new String(decoded);
            String[] split = s.split("\\s+", 2);
            if (split.length > 1) {
                userName = split[0];
                msg = split[1];
                System.out.println(msg);
            }
            byte[] temp = getJson().getBytes();
            jsonBytes = new byte[2 + temp.length];
            jsonBytes[0] = headerMessage[0];
            jsonBytes[1] = (byte) temp.length;
            for(int i = 0; i < temp.length; i++) {
                jsonBytes[i + 2] = temp[i];
            }

        } catch (IOException e) {
            System.out.println("Unable to read client websocket msg");
        }
    }

    ///Used, in conjunction with the JSON, to see if the the user wants to join a room and which room to join.
    public String getRoomName(){
        String s = new String(decoded);
        System.out.println(s);
        int i = s.indexOf(" ");
        if (s.substring(0, i).equals("join"));
        String roomName = s.substring(i, s.length());
        return roomName;
    }

    ///refactored handshake with the magic string used to switch the protocols and upgrade the user
    public static void handshakeBack(HTTPRequest request, Socket clientSocket) {
        try {
            OutputStream toClient = clientSocket.getOutputStream(); //used to output the bytes after their conversion from text
            //header for switching protocol; essentially the upgrade header
            toClient.write(("HTTP/1.1 101 Switching Protocols\r\n" + "Upgrade: websocket\r\n"
                    + "Connection: Upgrade\r\n" + "Sec-WebSocket-Accept: ").getBytes());

            String wsValue = request.getHeaderMap().get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
            MessageDigest md = MessageDigest.getInstance("SHA-1"); //convert to SHA-1, which is text
            //convert from text to bytes
            String encode = Base64.getEncoder().encodeToString(md.digest(wsValue.getBytes()));
            toClient.write((encode + "\r\n\r\n").getBytes());

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Unable to write handshake header to client");
        }
    }

    ///write out the JSON message
    public void sendMessage(OutputStream toClient) throws IOException{
        //error handling for someone leaving the room
        try {
            toClient.write(jsonBytes);
            toClient.flush();
        } catch (NullPointerException e){
            System.out.println("A user has left the room.");
        }
    }

    ///returns a JSON string specifically formatted for the user: and message:
    public String getJson() {
        return "{ \"user\" : \"" + this.userName + "\", \"message\" : \"" + this.msg + "\" }";
    }

    public String getUserName(){ return this.userName; }

    public String getMessage() {return this.msg;}

    public byte[] getDecoded() {return decoded;}

    public byte[] getJsonBytes() {
        return jsonBytes;
    }
}

