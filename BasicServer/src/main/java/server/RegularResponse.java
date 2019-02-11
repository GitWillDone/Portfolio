package server;

import java.io.*;

/**
 * Generates a non-WebSocket response. It will allow the user to request and receive riles, and will give a header 200
 * OK if the file exists.
 */
public class RegularResponse {
    private OutputStream toClient;
    private File file;
    private HTTPRequest request;

    public RegularResponse(OutputStream toClient, HTTPRequest request) {
        this.toClient = toClient;
        this.request = request;
    }

    public void invoke() throws IOException, FileNotFoundException {
        String header;
        file = new File("./resources" + request.getFileName());

        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bin = new BufferedInputStream(fis);

            header = "HTTP/1.1 200 OK\r\n";
            toClient.write(header.getBytes());

            String contentLength = "Content-Length: " + file.length();
            toClient.write(contentLength.getBytes());
            toClient.write("\r\n\r\n".getBytes());

            byte[] content = new byte[(int) file.length()];
            bin.read(content);
            toClient.write(content);
        } else {
            header = "HTTP/1.1 404 Not Found";
            toClient.write(header.getBytes());
        }
    }
}

