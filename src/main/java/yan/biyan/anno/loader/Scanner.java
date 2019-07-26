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

import yan.biyan.anno.Load;
import yan.biyan.anno.PostMatching;
import yan.biyan.anno.PrimeMatching;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Scanner {


    public static Map<String, MethodAndClass> URL_METHOD_AND_CLASS = new HashMap<>();


    static {
        Set<Class<?>> classList = null;
        try {
            classList = getControllers("crabapple.http_server.test");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Class<?> cla : classList) {
            System.out.println(cla);
            PrimeMatching primeMatching = cla.getAnnotation(PrimeMatching.class);
            String pre = primeMatching.value();

            Method[] methods = cla.getMethods();

            for (Method method : methods) {
                PostMatching annotation = method.getAnnotation(PostMatching.class);
                if (annotation != null) {
                    String url = pre + annotation.value();

                    url = url.charAt(url.length() - 1) == '/' ? url : url + '/';

                    MethodAndClass methodAndClass = new MethodAndClass(method, cla);
                    URL_METHOD_AND_CLASS.put(url, methodAndClass);
                }
            }

        }


        try {
            classList = getClasses("crabapple.http_server");


            for (Class<?> cla : classList) {

                Field[] fields = cla.getFields();
                for (Field field : fields) {
                    Load load = field.getDeclaredAnnotation(Load.class);
                    if (load != null) {
                        String name = load.value();
                        String param = LoadConfig.parameter.get(name);
                        System.out.println(param);
                        if (param != null) {
                            if (field.getType().getName().indexOf("int") != -1)
                                field.set(cla.newInstance(), Integer.valueOf(Integer.valueOf(param)));
                            else if (field.getType().getName().indexOf("boolean") != -1)
                                field.set(cla.newInstance(), Boolean.valueOf(param));
                            else
                                field.set(cla.newInstance(), param);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 从包package中获取所有的Class
     *
     * @param packageName
     * @return
     */
    private static Set<Class<?>> getClasses(String packageName) throws Exception {

        // 第一个class类的集合
        //List<Class<?>> classes = new ArrayList<Class<?>>();
        Set<Class<?>> classes = new HashSet<>();
        // 是否循环迭代
        boolean recursive = true;
        // 获取包的名字 并进行替换
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);


            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();

                System.out.println(url.getPath());
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中，以下俩种方法都可以
                    //网上的第一种方法，
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                    //网上的第二种方法
                    //addClass(classes,filePath,packageName);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }


    private static void doAddClass(Set<Class<?>> classes, final String classsName) throws Exception {
        ClassLoader classLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                return super.loadClass(name);
            }
        };
        //Class<?> cls= ClassLoader.loadClass(classsName);
        classes.add(classLoader.loadClass(classsName));
    }

    //找也用了Controller注解的类
    private static Set<Class<?>> controllers;

    private static Set<Class<?>> getControllers(String packageName) throws Exception {
        if (controllers == null) {
            controllers = new HashSet<>();
            Set<Class<?>> clsList = getClasses(packageName);
            if (clsList != null && clsList.size() > 0) {
                for (Class<?> cls : clsList) {
                    if (cls.getAnnotation(PrimeMatching.class) != null) {
                        Map<Class<?>, Object> map = new HashMap<>();
                        controllers.add(cls);
                    }
                }
            }
        }
        return controllers;
    }


    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    private static void findAndAddClassesInPackageByFile(String packageName,
                                                         String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                                + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    //classes.add(Class.forName(packageName + '.' + className));
                    //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }


}