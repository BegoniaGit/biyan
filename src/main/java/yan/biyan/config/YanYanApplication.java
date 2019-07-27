package yan.biyan.config;

/*
 * Copyright (c) This is zhaoxubin's Java program.
 * Copyright belongs to the crabapple organization.
 * The crabapple organization has all rights to this program.
 * No individual or organization can refer to or reproduce this program without permission.
 * If you need to reprint or quote, please post it to zhaoxubin2016@live.com.
 * You will get a reply within a week,
 *
 */

import site.yan.kit.Stamp;
import yan.biyan.HttpServer;
import yan.biyan.anno.loader.LoadConfig;
import yan.biyan.anno.loader.Scanner;

public class YanYanApplication {

    public YanYanApplication(Class startClass) throws Exception {
        String packageName=startClass.getPackage().getName();
        String URL = this.getClass().getResource("/").getPath();
        if (URL.charAt(0) == '/')
            URL = URL.substring(1, URL.length());
        URL = URL.replaceAll("%20", " ");
        Stamp.log("项目路径 " + URL);
        new LoadConfig(URL);
        new Scanner(packageName);
        HttpServer httpServer = new HttpServer();
        httpServer.setBasePath("C:/Users/Think/Pictures/fish");
        httpServer.start();
    }
}
