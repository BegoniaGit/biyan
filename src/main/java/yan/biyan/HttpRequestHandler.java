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

import java.io.*;
import java.net.Socket;

public class HttpRequestHandler implements Runnable {

    private Socket socket;

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
            new FileRequestHandler().handler(socket);
    }

    private static void close(Closeable... closeables) {
        if (closeables != null)
            for (Closeable closeable : closeables) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    System.out.println("ERROR");
                }
            }
    }

    private boolean isFile() {
        String url = null;
        try {
            url = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                    .readLine()
                    .split(" ")[1];

        } catch (IOException e) {
            e.printStackTrace();
        }
        int lastIndexOf = url.lastIndexOf('.');
        if (lastIndexOf == -1)
            return false;
        String suffix = url.substring(lastIndexOf);
        if (FileType.getByFormat(suffix) == null)
            return false;
        return true;
    }
}