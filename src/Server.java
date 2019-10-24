package main;

import java.io.IOException;
import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.NumberFormatException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.text.SimpleDateFormat;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;


import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsServer;

import handler.*;
import main.Password;


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
            Timestamp ts = new Timestamp(date.getTime());
            
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
        } catch (NumberFormatException ex) {
            // usage();
            System.out.println("Failed to parse int from argument");
            return;
        } catch (ArrayIndexOutOfBoundsException ex) {
            logger.log(Level.INFO, "No port provided, defaulting to 443");
            port = 443;
        }

        Server s = new Server();
        s.run(port);
    }

    private static final int MAX_WAITING_CONNS = 12;
    private HttpsServer server;

    /**
     * runs the server
     * @param port the port number on which to run the server
     */
    public void run(int port) {
        // initialize the server
        System.out.println("Initializing server");
        try {
            // load certificate
            char[] password = Password.password().toCharArray();        // keystore password
            KeyStore ks = KeyStore.getInstance("JKS");                  // create keystore
            FileInputStream fis = new FileInputStream("lig.keystore");  // read and load the key
            ks.load(fis, password);

            // display certificate
            Certificate cert = ks.getCertificate("alias");
            System.out.println(String.format("using certificate %s", cert));

            // set up key manager
            KeyManagerFactory tmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            // set up trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            // create the server, use TLS protocol
            server = HttpsServer.create(
                new InetSocketAddress(port), 
                MAX_WAITING_CONNS);
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // set up HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // initialiaze SSL context
                        SSLContext c = SSLContext.getDefault();
                        SSLEngine engine = c.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // get default parameters
                        SSLParamaters defaultSSLParameters = c.getDefaultSSLParameters();
                        params.setSSLParameters(defaultSSLParameters);
                    } catch (Exception ex) {
                        logger.log(Level.Error, "Failed to create HTTPS port", ex);
                    }
                }
            });

        } catch (IOException ex) {
            System.out.println("Failed to initialize");
            return;
        }
        // don't know what this does, apparently it's important
        server.setExecutor(null);

        // create the contexts
        System.out.println("Creating contexts");

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
