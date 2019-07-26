package yan.biyan.anno.loader;

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
import java.util.HashMap;
import java.util.Map;

public class LoadConfig {

    public static Map<String, String> parameter = new HashMap<>();

    static {
        String path = "D:\\300 Project\\301 CODE\\raspberry\\code\\src\\crabapple\\http_server_test\\src\\main\\resource\\carambola.yan";

        try {

            FileReader fileReader = new FileReader(path);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 0) continue;
                if (line.charAt(0) == '#') continue;
                String[] params = line.split(":");
                try {
                    String key = params[0].trim();
                    String value = params[1].trim();
                    if (!"".equals(key) && !"".equals(value)) ;
                    parameter.put(key, value);
                } catch (Exception e) {
                }
            }

            fileReader.close();
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
