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

import com.alibaba.fastjson.JSON;
import yan.biyan.anno.loader.MethodAndClass;
import yan.biyan.anno.loader.Scanner;

import java.io.*;
import java.net.Socket;

import static yan.biyan.HttpServer.basePath;

public class FileRequestHandler {


    public void handler(Socket socket) {

        BufferedReader reader = null;
        BufferedReader br = null;
        PrintWriter out = null;
        InputStream in = null;
        String baseUrl = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String header = reader.readLine();
            baseUrl = header.split(" ")[1];
            String filePath = basePath + baseUrl;


            out = new PrintWriter(socket.getOutputStream());

            if (isBinFile(filePath)) {

                File image = new File(filePath);
                FileInputStream inputStream = new FileInputStream(image);
                int length = inputStream.available();
                byte data[] = new byte[length];
                String fileName = image.getName();
                String fileType = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
                inputStream.read(data);


                out.println("HTTP/1.1 200 OK");
                out.println("Server: YANYAN");
                out.println("Connection: close");
                out.println("Content-Type: " + FileType.getByFormat(fileType));
                out.println("Accept-Ranges: bytes");
                out.println("Content-Length: " + length);
                out.println("");
                out.flush();
                OutputStream toClient = socket.getOutputStream();
                toClient.write(data);
                toClient.flush();
                inputStream.close();
                toClient.close();


            } else if (isCharFile(filePath)) {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));

                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null)
                    stringBuilder.append(line);
                String suffix = filePath.substring(filePath.lastIndexOf("."), filePath.length());
                out.println("HTTP/1.1 200 OK");
                out.println("Server: YANYAN");
                out.println("Connection: close");
                out.println("Content-Type: " + FileType.getByFormat(suffix) + "; charset=UTF-8");
                out.println("Content-Length: " + stringBuilder.length());
                out.println("");
                out.println(stringBuilder.toString());
                out.flush();

            } else {
                baseUrl = baseUrl.charAt(baseUrl.length() - 1) == '/' ? baseUrl : baseUrl + '/';

                MethodAndClass methodAndClass = Scanner.URL_METHOD_AND_CLASS.get(baseUrl);
                if (methodAndClass == null) throw new Exception();
                Object resInfo = methodAndClass.method.invoke(methodAndClass.cla.newInstance());

                class b implements Serializable{

                    int id=1;
                    String info=resInfo.toString();

                    public int getId() {
                        return id;
                    }

                    public void setId(int id) {
                        this.id = id;
                    }

                    public String getInfo() {
                        return info;
                    }

                    public void setInfo(String info) {
                        this.info = info;
                    }
                }
                String res = JSON.toJSONString(new b());
                System.out.println(res);
                out.println("HTTP/1.1 200 OK");
                out.println("Server: YANYAN");
                out.println("Connection: close");
                out.println("Content-Type: application/json;" + " charset=UTF-8");
                out.println("Content-Length: " + res.getBytes().length);
                out.println("");
                out.println(res);
                out.flush();
            }


        } catch (Exception ex) {
            out.println("HTTP/1.1 500");
            out.println("");
            out.flush();
        } finally {
            close(br, in, reader, out, socket);
        }
    }

    private boolean isBinFile(String url) {
        int lastIndexOf = url.lastIndexOf('.');
        if (lastIndexOf == -1)
            return false;
        String suffix = url.substring(lastIndexOf);

        if (FileType.getByFormat(suffix) == null)
            return false;
        else return true;
    }

    private boolean isCharFile(String url) {
        int lastIndexOf = url.lastIndexOf('.');
        if (lastIndexOf == -1)
            return false;
        String suffix = url.substring(lastIndexOf);

        if ("text".equals(FileType.getByFormat(suffix).substring(0, 4)))
            return true;
        else return false;
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
}