package yan.biyan;

/*
 * Copyright (c) This is zhaoxubin's Java program.
 * Copyright belongs to the crabapple organization.
 * The crabapple organization has all rights to this program.
 * No individual or organization can refer to or reproduce this program without permission.
 * If you need to reprint or quote, please post it to zhaoxubin2016@live.com.
 * You will get a reply within a week,
 *
 */


import yan.biyan.anno.Load;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer {

    static ThreadPool<HttpRequestHandler> threadPool = new DefaultThreadPool<>(5);

    static String basePath;

    static ServerSocket serverSocket;

    @Load("server_port")
    public static int port = 8080;

    public static void setBasePath(String basePath) {
        if (basePath != null && new File(basePath).exists() && new File(basePath).isDirectory())
            HttpServer.basePath = basePath;
    }


    public static void setPort(int port) {
        if (port > 0)
            HttpServer.port = port;
    }

    public static void start() throws Exception {
        serverSocket = new ServerSocket(port);
        Socket socket = null;

        while ((socket = serverSocket.accept()) != null) {
            threadPool.execute(new HttpRequestHandler(socket));
        }
        serverSocket.close();
    }


}


