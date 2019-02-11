package server;

import org.omg.PortableInterceptor.ClientRequestInfo;

import java.io.*;
import java.net.Socket;

/**
 * The response will start by generating a request based on the input clientSocket.  From there, the request will be
 * examined for the requirement of a WebSocket or Regular response.
 */
public class HTTPResponse {
    private File file;

    public HTTPResponse(HTTPRequest request, Socket clientSocket) {
        String header;

        try {
            OutputStream clientOutput = clientSocket.getOutputStream();

            file = new File("./resources" + request.getFileName());

            if(file.exists()){
                FileInputStream fis = new FileInputStream(file); //TODO combine these two if you don't end up using fix for anything else
                BufferedInputStream bis = new BufferedInputStream(fis);

                header = "HTTP/1.1 200 OK\r\n";
                clientOutput.write(header.getBytes());
                String contentLength = "Content-Length: " + file.length();
                clientOutput.write(contentLength.getBytes());
                clientOutput.write("\r\n\r\n".getBytes());

                byte[] content =  new byte[(int) file.length()];
                bis.read(content);
                clientOutput.write(content);
            } else {
                header = "HTTP/1.1 404 Not Found";
                clientOutput.write(header.getBytes());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
