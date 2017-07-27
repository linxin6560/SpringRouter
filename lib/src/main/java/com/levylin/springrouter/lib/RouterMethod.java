package com.levylin.springrouter.lib;

import com.levylin.springrouter.lib.annotation.MethodInfo;
import com.levylin.springrouter.lib.annotation.SRouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 路由方法
 * Created by LinXin on 2017/6/5.
 */
class RouterMethod {

    private Method method;
    private final String routerPath;
    private static final HashMap<String, Object> ROUTER_CLASS_MAP = new HashMap<>();
    static final HashMap<String, MethodInfo> ROUTER_INFO_MAP = new HashMap<>();

    RouterMethod(Build build) {
        this.routerPath = build.methodPath;
        this.method = build.method;
    }

    public Object invoke(Object[] args) throws Throwable {
        Object target = ROUTER_CLASS_MAP.get(routerPath);
        MethodInfo info = ROUTER_INFO_MAP.get(routerPath);
        if (target == null) {
            String className = info.getClassName();
            Class clazz = Class.forName(className);
            target = clazz.newInstance();
            ROUTER_CLASS_MAP.put(routerPath, target);
        }
        Method targetMethod;
        if (method.getParameterTypes() == null) {
            targetMethod = target.getClass().getMethod(info.getMethodName());
        } else {
            targetMethod = target.getClass().getMethod(info.getMethodName(), method.getParameterTypes());
        }
        return targetMethod.invoke(target, args);
    }

    static class Build {
        final Class<?> router;
        final Method method;
        final Annotation[] methodAnnotations;

        String methodPath;

        public Build(Class<?> router, Method method) {
            this.router = router;
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
        }

        RouterMethod build() {
            for (Annotation annotation : methodAnnotations) {
                parserMethodAnnotation(annotation);
            }
            return new RouterMethod(this);
        }

        private void parserMethodAnnotation(Annotation annotation) {
            if (annotation instanceof SRouter) {
                methodPath = ((SRouter) annotation).value();
            }
        }
    }
}
