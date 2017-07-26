package com.levylin.springrouter.lib;

import android.content.Context;

import com.levylin.springrouter.lib.annotation.Constants;
import com.levylin.springrouter.lib.annotation.RouterInfo;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * 路由
 * Created by LinXin on 2017/6/5.
 */
public class SpringRouter {

    private static final Map<Method, RouterMethod> ROUTER_METHOD_CACHE = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T create(final Class<T> router) {
        return (T) Proxy.newProxyInstance(router.getClassLoader(), new Class<?>[]{router},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }
                        RouterMethod routerMethod = loadRouterMethod(router, method);
                        return routerMethod.invoke(args);
                    }
                });
    }

    private static RouterMethod loadRouterMethod(Class<?> router, Method method) {
        RouterMethod result;
        synchronized (ROUTER_METHOD_CACHE) {
            result = ROUTER_METHOD_CACHE.get(method);
            if (result == null) {
                result = new RouterMethod.Build(router, method).build();
                ROUTER_METHOD_CACHE.put(method, result);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void init(Context context) {
        long start = System.currentTimeMillis();
        ArrayList<String> classes = getClassesFromPackage(context, Constants.PACKAGE_NAME);
        for (String aClass : classes) {
            try {
                Class clazz = Class.forName(aClass);
                Method method = clazz.getDeclaredMethod("getRouterMap");
                method.setAccessible(true);
                HashMap<String, RouterInfo> map = (HashMap<String, RouterInfo>) method.invoke(null);
                RouterMethod.ROUTER_INFO_MAP.putAll(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
    }

    /**
     * 获取包名下所有的类
     */
    private static ArrayList<String> getClassesFromPackage(Context context, String packageName) {
        ArrayList<String> classes = new ArrayList<>();
        try {
            DexFile df = new DexFile(context.getPackageCodePath());
            Enumeration<String> entries = df.entries();
            while (entries.hasMoreElements()) {
                String className = entries.nextElement();
                if (className.contains(packageName)) {
                    classes.add(className);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

}
