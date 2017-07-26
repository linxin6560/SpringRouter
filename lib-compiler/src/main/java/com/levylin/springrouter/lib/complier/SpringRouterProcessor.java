package com.levylin.springrouter.lib.complier;

import com.google.auto.service.AutoService;
import com.levylin.springrouter.lib.annotation.Constants;
import com.levylin.springrouter.lib.annotation.RouterInfo;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * 路由解析器
 * Created by LinXin on 2017/6/22.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.levylin.springrouter.lib.annotation.SRServerPath"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SpringRouterProcessor extends AbstractProcessor {

    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;
    private Filer mFiler;
    private Elements mElements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        mElements = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            info(">>> annotations is null... <<<");
            return true;
        }
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            HashMap<String, ProcessingMethod> map = new HashMap<>();
            TypeElement typeElement = null;
            for (Element element : elements) {
                // 打印
                ExecutableElement executableElement = (ExecutableElement) element;
                typeElement = (TypeElement) executableElement.getEnclosingElement();
                ProcessingMethod method = new ProcessingMethod(executableElement);
                String path = method.getPath();
                map.put(path, method);
            }
            try {
                if (typeElement != null) {
                    makeJavaFile(typeElement, map).writeTo(mFiler);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private String getPackageName(TypeElement type) {
        return mElements.getPackageOf(type).getQualifiedName().toString();
    }


    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

    private JavaFile makeJavaFile(TypeElement element, HashMap<String, ProcessingMethod> map) {
        TypeName hashMapType = ParameterizedTypeName.get(HashMap.class, String.class, RouterInfo.class);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getRouterMap")
                .returns(hashMapType)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        methodBuilder.addStatement("$T map = new $T<>()", hashMapType, HashMap.class);
        for (Map.Entry<String, ProcessingMethod> entry : map.entrySet()) {
            ProcessingMethod method = entry.getValue();
            String[] typeArray = method.getTypeArray();
            StringBuilder typeStr = new StringBuilder();
            for (String s : typeArray) {
                typeStr.append("\"").append(s).append("\"").append(",");
            }
            String type;
            if (typeStr.length() > 1) {
                type = typeStr.substring(0, typeStr.length() - 1);
            } else {
                type = "";
            }
            info("type=" + type);
            String routerMethod;
            if (!type.equals("")) {
                routerMethod = String.format("new RouterInfo(\"%1$s\",\"%2$s\",%3$s)", method.getClassName(), method.getMethodName(), type);
            } else {
                routerMethod = String.format("new RouterInfo(\"%1$s\",\"%2$s\")", method.getClassName(), method.getMethodName());
            }
            methodBuilder.addCode(String.format("map.put(\"%1$s\",%2$s);\n", entry.getKey(), routerMethod));
        }
        methodBuilder.addCode("return map;\n");


        TypeSpec finderClass = TypeSpec.classBuilder(element.getSimpleName() + "$$Router")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        return JavaFile.builder(Constants.PACKAGE_NAME, finderClass).build();
    }
}
