package com.levylin.springrouter.lib.annotation;

/**
 * 方法信息
 * Created by LinXin on 2017/7/25.
 */
public class MethodInfo {

    private String className;
    private String methodName;

    public MethodInfo(String fullyMethod) {
        System.out.println("fullyMethod=" + fullyMethod);
        fullyMethod = fullyMethod.replaceAll("\\(.*\\)", "");
        System.out.println("new fullyMethod=" + fullyMethod);
        int lastPotIndex = fullyMethod.lastIndexOf(".");
        if (lastPotIndex == -1)
            return;
        this.className = fullyMethod.substring(0, lastPotIndex);
        this.methodName = fullyMethod.substring(lastPotIndex + 1, fullyMethod.length());
        System.out.println("className=" + className + ",methodName=" + methodName);
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
