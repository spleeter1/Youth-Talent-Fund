package com.graduation.youthtalentfund.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

@Component
public class ProjectionS3Util {
    @Value("${cdn.base-url}")
    private String domain;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @SuppressWarnings("unchecked")
    public <T> T addS3(T original, Class<T> projectionInterface, String imageGetter) {
        return (T) Proxy.newProxyInstance(
                projectionInterface.getClassLoader(),
                new Class[]{projectionInterface},
                (proxy, method, args) -> {
                    if (method.getName().equals(imageGetter)) {
                        Object value = method.invoke(original, args); // giá trị gốc
                        if (value != null) {
                            return domain + "/" + bucketName + "/" + value;
                        }
                        return null;
                    }
                    return method.invoke(original, args); // các getter khác
                }
        );
    }
}
