package echoserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;
import utils.Utils;

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
//  private static String logFile; //************************************************************************************

    public static void stopServer() {
        keepRunning = false;
    }

//    private static void handleClient(Socket socket) throws IOException {
//        Scanner input = new Scanner(socket.getInputStream());
//        PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
//
//        String message = input.nextLine(); //IMPORTANT blocking call
//        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
//        while (!message.equals(ProtocolStrings.STOP)) {
//            writer.println(message.toUpperCase());
//            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Returned the message: %1$S ", message.toUpperCase()));
//            message = input.nextLine(); //IMPORTANT blocking call
//        }
//        writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
//        socket.close();
//        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Closed a Connection");
//    }

    public static void main(String[] args) {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");
        String logFile = properties.getProperty("logFile");
        Utils.setLogFile(logFile, EchoServer.class.getName());  // *******************************************************
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Sever started");
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.start();
//                handleClient(socket);
            }
            while (keepRunning);
        }
        catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            Utils.closeLogger(EchoServer.class.getName());  //***************************************************************
        }

    }
}
