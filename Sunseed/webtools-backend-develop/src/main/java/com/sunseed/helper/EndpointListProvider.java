package com.sunseed.helper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.annotation.PostConstruct;

@Component
public class EndpointListProvider {

    private final ApplicationContext applicationContext;
    private Map<String, List<RequestMethod>> endpointMethodsMap;

    @Autowired
    public EndpointListProvider(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        endpointMethodsMap = new HashMap<>();
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Controller.class);
        for (Object controller : beansWithAnnotation.values()) {
            Class<?> controllerClass = controller.getClass();
            String[] classMappings = getClassLevelMappings(controllerClass);

            // Get method-level request mappings
            for (Method method : controllerClass.getDeclaredMethods()) {
                List<String> methodMappings = getMethodMappings(method);
                addEndpoints(classMappings, methodMappings, method);
            }
        }
    }

    private String[] getClassLevelMappings(Class<?> controllerClass) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(controllerClass, RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            return requestMapping.value();
        }
        return new String[]{""};
    }

    private List<String> getMethodMappings(Method method) {
        List<String> methodMappings = new ArrayList<>();
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (AnnotatedElementUtils.isAnnotated(annotation.annotationType(), RequestMapping.class)) {
                try {
                    Method valueMethod = annotation.annotationType().getMethod("value");
                    String[] values = (String[]) valueMethod.invoke(annotation);
                    if (values.length > 0) {
                        methodMappings.addAll(List.of(values));
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        return methodMappings;
    }

    private void addEndpoints(String[] classLevelMappings, List<String> methodLevelMappings, Method method) {
        for (String classMapping : classLevelMappings) {
            for (String methodMapping : methodLevelMappings) {
                String endpoint = classMapping + methodMapping;
                RequestMethod[] requestMethods = getRequestMethods(method);
                List<RequestMethod> methods = endpointMethodsMap.getOrDefault(endpoint, new ArrayList<>());
                for (RequestMethod requestMethod : requestMethods) {
                    if (!methods.contains(requestMethod)) {
                        methods.add(requestMethod);
                    }
                }
                endpointMethodsMap.put(endpoint, methods);
            }
        }
    }

    private RequestMethod[] getRequestMethods(Method method) {
        RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
        if (requestMapping != null) {
            return requestMapping.method();
        }
        return new RequestMethod[0];
    }

    public Map<String, List<RequestMethod>> getEndpointMethodsMap() {
        return endpointMethodsMap;
    }
}
