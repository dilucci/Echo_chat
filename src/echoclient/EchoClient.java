package echoclient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

public class EchoClient extends Thread implements EchoListener  {

    Socket socket;
    private int port;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    private static List<EchoListener> listeners = new ArrayList();  // *********************************
    private String message;

    //*****************************
    public void registerEchoListener(EchoListener l) {
        listeners.add(l);
    }

    //*****************************
    public void unregisterEchoListener(EchoListener l) {
        listeners.remove(l);
    }

    private void notifyListeners(String msg) {
        for (EchoListener echoListener : listeners) {
            echoListener.messageArrived(msg);
        }
    }

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
        start();
    }

    public void send(String msg) {
        output.println(msg);
    }

    public void stopClient() throws IOException {
        output.println(ProtocolStrings.STOP);
    }

//    public String receive() {
//        String msg = input.nextLine();
//        if (msg.equals(ProtocolStrings.STOP)) {
//            try {
//                socket.close();
//            }
//            catch (IOException ex) {
//                Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return msg;
//    }
    public static void main(String[] args) {
        int port = 9090;
        String ip = "localhost";
        if (args.length == 2) {
            port = Integer.parseInt(args[0]);
            ip = args[1];
        }
        try {
            EchoClient tester = new EchoClient();
            tester.registerEchoListener(tester);
            tester.connect(ip, port);
            tester.send("Hello World");
//            System.out.println("Sending Hello World");
//            System.out.println("Waiting for a reply...");
            tester.stopClient();
//            EchoClient tester = new EchoClient();
//            tester.connect(ip, port);
//            System.out.println("Sending 'Hello world'");
//            tester.send("Hello World");
//            System.out.println("Waiting for a reply");
////            System.out.println("Received: " + tester.receive()); //Important Blocking call         
//            tester.stop();
//            System.in.read();      
        }
        catch (UnknownHostException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        String msg = input.nextLine();
        while (!msg.equals(ProtocolStrings.STOP)) {
            notifyListeners(msg);
            msg = input.nextLine();
        }
        try {
            socket.close();
        }
        catch (IOException ex) {
            Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void messageArrived(String data) {
        message = data;
        System.out.println(data);
    }
    
    public String getMessage() {
        return message;
    }
}
