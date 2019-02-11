package server;


import java.io.OutputStream;

public class BadRequestException extends Exception {
    private OutputStream serverOut;

    //default constructor
    public BadRequestException(){
        super("BadRequestException");
    }
    //constructor which also takes a reason
    public BadRequestException(String reason){
        super("BadRequestException");

    }
}
