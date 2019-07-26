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

import java.lang.reflect.Method;

public class MethodAndClass {

    public Method method;

    public Class<?> cla;

    public MethodAndClass(Method method, Class<?> cla) {
        this.method = method;
        this.cla = cla;
    }
}
