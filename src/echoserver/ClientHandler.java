/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author Seb
 */
public class ClientHandler extends Thread {

    private Scanner input;
    private PrintWriter writer;
    private Socket socket;
    private String userName;

    private void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        input = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
    }

    public void send(String message) {
        writer.println(message);
    }

    public void sendUserList(ArrayList<String> userList) {
        String userListString = "";
        for (String user : userList) {
            userListString = userListString + user + ",";
        }
        if (userListString.endsWith(",")) {
            userListString = userListString.substring(0, userListString.length() - 1);
        }
        send(ProtocolStrings.ONLINE + userListString);
    }

    @Override
    public void run() {
        String message = input.nextLine(); //IMPORTANT blocking call
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
        while (!message.equals(ProtocolStrings.CLOSE)) {

            String[] partsArray = message.split("#");  // entire command including COMMAND, NAMES and MESSAGE. Should this be refactor into seperate method?
            String command = partsArray[0] + "#";

            if (command.equals(ProtocolStrings.CONNECT)) {
                String name = partsArray[1];
                setUserName(name);
            }
            if (command.equals(ProtocolStrings.SEND)) {
                message = partsArray[2];
                if (partsArray[1].equals("*")) {                    // sends to all users
                    EchoServer.send(message);
                }
                if (partsArray[1].contains(",")) {                  // sends to specified users
                    String[] receivers = partsArray[1].split(",");
                    EchoServer.send(message, receivers);
                }
                else {                                              // sends to 1 user
                    EchoServer.send(message, partsArray[1]);
                }
            }
            if (command.equals(ProtocolStrings.CLOSE)) {

            }
//            writer.println(message.toUpperCase());
//            EchoServer.send(message);
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Returned the message: %1$S ", message.toUpperCase()));
            message = input.nextLine(); //IMPORTANT blocking call
        }
        writer.println(ProtocolStrings.CLOSE); //Echo the stop message back to the client for a nice closedown
        EchoServer.removeHandler(this);
        try {
            socket.close();
        }
        catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Closed a Connection");
    }
}
