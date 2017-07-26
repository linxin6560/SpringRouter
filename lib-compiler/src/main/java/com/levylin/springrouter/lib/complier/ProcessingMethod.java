package com.levylin.springrouter.lib.complier;

import com.levylin.springrouter.lib.annotation.SRServerPath;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

/**
 * Created by LinXin on 2017/7/25.
 */
public class ProcessingMethod {

    private String className;
    private String methodName;
    private String[] typeArray;
    private ExecutableElement element;

    public ProcessingMethod(ExecutableElement element) {
        this.element = element;
        className = element.getEnclosingElement().toString();
        methodName = element.getSimpleName().toString();
        List<? extends VariableElement> variableElements = element.getParameters();
        typeArray = new String[variableElements.size()];
        for (int i = 0; i < variableElements.size(); i++) {
            VariableElement variableElement = variableElements.get(i);
            typeArray[i] = variableElement.asType().toString();
        }
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getTypeArray() {
        return typeArray;
    }

    public String getPath() {
        SRServerPath serverPath = element.getAnnotation(SRServerPath.class);
        return serverPath.value();
    }
}
