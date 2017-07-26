package com.levylin.springrouter.lib.annotation;

import java.util.Arrays;

/**
 * Created by LinXin on 2017/7/25.
 */
public class RouterInfo {

    private String className;
    private String methodName;
    private Class[] classes;

    public RouterInfo(String className, String methodName, String... typeArray) {
        this.className = className;
        this.methodName = methodName;
        if (typeArray == null)
            return;
        this.classes = new Class[typeArray.length];
        for (int i = 0; i < typeArray.length; i++) {
            String typeName = typeArray[i];
            if (typeName == null || typeName.equals(""))
                continue;
            typeName = typeName.replaceAll("<.*>", "");
            try {
                classes[i] = Class.forName(typeName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getTypeArray() {
        return classes;
    }

    @Override
    public String toString() {
        return "RouterInfo{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", classes=" + Arrays.toString(classes) +
                '}';
    }
}
