package com.levylin.springrouter.lib.complier;

import com.google.auto.service.AutoService;
import com.levylin.springrouter.lib.annotation.Constants;
import com.levylin.springrouter.lib.annotation.MethodInfo;
import com.levylin.springrouter.lib.annotation.SRouterPath;
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
import javax.tools.Diagnostic;

/**
 * 路由解析器
 * Created by LinXin on 2017/6/22.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.levylin.springrouter.lib.annotation.SRouterPath"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class SpringRouterProcessor extends AbstractProcessor {

    private static final String ROUTER_NAME = "%s$$Router";
    /**
     * 日志相关的辅助类
     */
    private Messager mMessager;
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            info(">>> annotations is null... <<<");
            return true;
        }
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            HashMap<String, HashMap<String, String>> classNameRouterMap = new HashMap<>();//类名和路由的map
            String className;
            for (Element element : elements) {
                // 打印
                ExecutableElement executableElement = (ExecutableElement) element;
                TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                className = String.format(ROUTER_NAME, typeElement.getSimpleName());
                String classPath = typeElement.toString();
                String fullyMethod = classPath + "." + executableElement;
                info("fullyMethod=" + fullyMethod + ",className=" + className);
                SRouterPath sRouterPath = element.getAnnotation(SRouterPath.class);
                String routerPath = sRouterPath.value();
                HashMap<String, String> tmp = classNameRouterMap.get(className);
                if (tmp == null) {
                    tmp = new HashMap<>();
                    classNameRouterMap.put(className, tmp);
                }
                tmp.put(routerPath, fullyMethod);
            }
            makeJavaFiles(classNameRouterMap);
        }
        return true;
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    private void info(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args));
    }

    /**
     * 生成java类
     *
     * @param map
     */
    private void makeJavaFiles(HashMap<String, HashMap<String, String>> map) {
        for (Map.Entry<String, HashMap<String, String>> entry : map.entrySet()) {
            makeOneJavaFile(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 生成单个java类
     *
     * @param className
     * @param map
     */
    private void makeOneJavaFile(String className, HashMap<String, String> map) {
        TypeName hashMapType = ParameterizedTypeName.get(HashMap.class, String.class, MethodInfo.class);

        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getRouterMap")
                .returns(hashMapType)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
        methodBuilder.addStatement("$T map = new $T<>()", hashMapType, HashMap.class);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String routerInfo = String.format("new MethodInfo(\"%1$s\")", entry.getValue());
            methodBuilder.addCode(String.format("map.put(\"%1$s\",%2$s);\n", entry.getKey(), routerInfo));
        }
        methodBuilder.addCode("return map;\n");


        TypeSpec finderClass = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodBuilder.build())
                .build();
        JavaFile file = JavaFile.builder(Constants.PACKAGE_NAME, finderClass).build();
        try {
            file.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
            error("生成文件失败：" + e.getLocalizedMessage());
        }
    }
}
