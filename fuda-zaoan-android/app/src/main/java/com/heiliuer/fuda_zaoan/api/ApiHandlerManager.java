package com.heiliuer.fuda_zaoan.api;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import com.heiliuer.fuda_zaoan.api.anotation.Handler;
import com.heiliuer.fuda_zaoan.api.anotation.HandlerBasePackage;
import com.heiliuer.fuda_zaoan.api.anotation.Handlers;
import com.heiliuer.fuda_zaoan.app.MyApplication;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * Created by Administrator on 2016/12/18 0018.
 */

@HandlerBasePackage("com.heiliuer.fuda_zaoan.api.handler")
public class ApiHandlerManager {

    private static String URL_SUFFIX_REPLACED = "http://api";

    private List<ApiHandlerWrapper> apiHandlerWrappers;


    public ApiHandlerManager() {
        try {
            resolveHandlers();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void resolveHandlers() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        apiHandlerWrappers = new ArrayList<>();
        String handlerBasePackage = getHandlerBasePackage();

        String[] classes = getClassesOfPackage(handlerBasePackage);

        for (String cls : classes) {
            Class<?> clazz = Class.forName(handlerBasePackage + "." + cls);
            Handlers annotationHandlers = clazz.getAnnotation(Handlers.class);
            if (annotationHandlers != null) {
                Method[] methods = clazz.getMethods();
                final Object handler = clazz.newInstance();
                for (final Method method : methods) {
                    final Handler annotationHandler = method.getAnnotation(Handler.class);
                    if (annotationHandler != null) {

                        ApiHandlerWrapper apiHandlerWrapper = new ApiHandlerWrapper() {

                            @Override
                            public boolean isAccept(WebResourceRequest request) {
                                String url = request.getUrl().toString();
                                url = url.replace(URL_SUFFIX_REPLACED, "").split("\\?")[0];
                                String handlerUrl = annotationHandler.value();
                                return url.equals(handlerUrl);
                            }

                            @Override
                            public WebResourceResponse handle(WebResourceRequest request) {
                                try {
                                    return (WebResourceResponse) method.invoke(handler, request);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                } catch (InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }

                        };

                        apiHandlerWrappers.add(apiHandlerWrapper);
                    }
                }
            }
        }
    }

    private String[] getClassesOfPackage(String packageName) {
        ArrayList<String> classes = new ArrayList<String>();
        try {
            String packageCodePath = MyApplication.getAppContext().getPackageCodePath();
            DexFile df = new DexFile(packageCodePath);
            for (Enumeration<String> iter = df.entries(); iter.hasMoreElements(); ) {
                String className = iter.nextElement();
                if (className.contains(packageName)) {
                    classes.add(className.substring(className.lastIndexOf(".") + 1, className.length()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes.toArray(new String[classes.size()]);
    }


    public List<ApiHandlerWrapper> getApiHandlerWrappers() {
        return apiHandlerWrappers;
    }

    private String getHandlerBasePackage() {
        HandlerBasePackage annotation = getClass().getAnnotation(HandlerBasePackage.class);
        return annotation.value();
    }
}
