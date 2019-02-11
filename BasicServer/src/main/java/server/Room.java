package server;

import javax.management.MXBean;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.channels.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * The thread that receives the message will be the same that sends the messages to all the clients.
 * <p>
 * Messages are currently stored in an ArrayList and will, therefor, be lost as the connection is terminated.
 */
public class Room {
    private Selector selector;
    private ArrayList<SocketChannel> clientQueue = new ArrayList<SocketChannel>();
    private ArrayList<SocketChannel> clients = new ArrayList<SocketChannel>();
    private ArrayList<byte[]> messageHistory = new ArrayList<byte[]>();
    private String name;

    //    public Room(Connection connection, String name) {
    public Room(String name) {
        this.name = name;
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForClient() throws IOException {

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                if (key.isReadable()) {
                    keyIterator.remove();
                    SocketChannel clientSocketChannel = (SocketChannel) key.channel();
                    key.cancel();
                    clientSocketChannel.configureBlocking(true);

                    //read the message this client send and add the json bytes array (with header) to history
                    WebSocketResponse response = new WebSocketResponse();
                    response.decode(clientSocketChannel.socket());
                    if (response.getMessage() == null && response.getUserName() == null) { //handling for client leaving the room
                        clients.remove(clientSocketChannel);
                        break;
                    }
                    messageHistory.add(response.getJsonBytes());

                    clientSocketChannel.configureBlocking(false);
                    selector.selectNow();
                    clientSocketChannel.register(selector, SelectionKey.OP_READ);

                    //post this message to all clients in the room
                    post(response);
                }
            }


            for (SocketChannel sc : clientQueue) {
                sc.configureBlocking(true);

                //send the msg history of this room to newly joined clients
                broadcast(sc);
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);

                //add this newly joined client to existing client
                clients.add(sc);
            }
            clientQueue.clear();
        }
    }

    /**
     * sends a user all the messages thus far
     *
     * @param sc
     */
    private synchronized void broadcast(SocketChannel sc) throws IOException {
        for (byte[] msg : messageHistory) {
            sc.socket().getOutputStream().write(msg);
            sc.socket().getOutputStream().flush();
        }
    }

    /**
     * adds a socket channel to the current room.  Synchronized because issues may occur when two clients try to be added at the same time.
     *
     * @param sc
     */
    public synchronized void addClient(SocketChannel sc) {
        clientQueue.add(sc);
        selector.wakeup();
    }

    /**
     * Posts the message to the entire room
     *
     * @param response
     * @throws IOException
     */
    public synchronized void post(WebSocketResponse response) throws IOException {

        for (SocketChannel sc : clients) {
            SelectionKey key = sc.keyFor(selector);
            key.cancel();
            sc.configureBlocking(true);

            // send the coming client's msg to existing clients
            response.sendMessage(sc.socket().getOutputStream());

            sc.configureBlocking(false);
            selector.selectNow();
            sc.register(selector, SelectionKey.OP_READ);
        }
    }
}


