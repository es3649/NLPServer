package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.text.SimpleDateFormat;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

import handler.*;


/** class Server
 */
public class Server {
    public Server() {}

    public static Logger logger;

    static {
        Level logLevel = Level.FINEST;

        logger = Logger.getLogger("server");
        logger.setLevel(logLevel);
        logger.setUseParentHandlers(false);
        
        try {
            // try to set up the file handler, if it fails, the set up
            // the console handler

            // build a filename with date-time
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss");
            Date date = new Date();
            Timestamp ts = new Timestamp(date.getTime())
            
            FileHandler fileHandler = new FileHandler("log/server"+ sdf.format(ts) +".txt");
            fileHandler.setLevel(logLevel);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);


        } catch (IOException ex) {
            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(logLevel);
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

            // log the error we already got:
            logger.log(Level.WARNING, "Failed to initialize file logger", ex);
        }
    }

    /**
     * main begins hosting the Server
     * @param args command line arguments (TODO docs for these)
     */
    public static void main(String args[]) {
        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ex) {
            usage();
            return;
        }

        Server s = new Server();
        s.run(port);
    }

    private static final int MAX_WAITING_CONNS = 12;
    private HttpServer server;

    /**
     * runs the server
     * @param args
     */
    public void run(int port) {
        // initialize the server
        System.out.println("Initializing server");
        try {
            server = HttpServer.create(
                new InetSocketAddress(port), 
                MAX_WAITING_CONNS);
        } catch (IOException ex) {
            System.out.println("Failed to initialize");
            return;
        }
        // don't know what this does, apparently it's important
        server.setExecutor(null);

        // create the contexts
        System.out.println("Creating contexts");

        server.createContext("/user/register", new RegistrationHandler());
        server.createContext("/user/login", new LoginRequestHandler());
        server.createContext("/clear", new ClearRequestHandler());
        server.createContext("/fill", new FillRequestHandler());
        server.createContext("/load", new LoadRequestHandler());
        server.createContext("/person", new PersonRequestHandler());
        server.createContext("/event", new EventRequestHandler());
        server.createContext("/", new handler.FileHandler());

        // start the server!
        System.out.println("Starting server...");
        server.start();
        System.out.printf("Server started on port %d\n", port);
        logger.log(Level.INFO, "Server started");
    }

    /**
     * prints the usage information
     */
    private static void usage() {
        System.out.println("Usage: java main/Server <port number>");
    }
};
