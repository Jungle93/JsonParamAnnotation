package com.jungle.json.param;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.springframework.core.MethodParameter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author jungle
 * @version V1.0
 * @date 2018/9/27 19:56
 * @Title: JsonPathArgumentResolver.java
 * @Package com.jungle.json.param
 * @Description: Json路径参数处理器
 * copyright © 2018- holmes.com
 */
public class JsonPathArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    /**
     * Whether the given {@linkplain MethodParameter method parameter} is
     * supported by this resolver.
     *
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver supports the supplied parameter;
     * {@code false} otherwise
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonParam.class);
    }

    /**
     * Resolves a method parameter into an argument value from a given request.
     * A {@link ModelAndViewContainer} provides access to the model for the
     * request. A {@link WebDataBinderFactory} provides a way to create
     * type conversion purposes.
     *
     * @param parameter    the method parameter to resolve. This parameter must
     *                     have previously been passed to {@link #supportsParameter} which must
     *                     have returned {@code true}.
     * @param mavContainer the ModelAndViewContainer for the current request
     * @param webRequest   the current request
     * @return the resolved argument value, or {@code null}
     * @throws Exception in case of errors with the preparation of argument values
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String body = getRequestBody(webRequest);
        Object val = null;
        try {
            val = JsonPath.parse(body).read(parameter.getParameterAnnotation(JsonParam.class).value(), parameter.getParameterType());
//            val = JsonPath.read(body, parameter.getParameterAnnotation(JsonParam.class).value());
            if (parameter.getParameterAnnotation(JsonParam.class).required() && val == null) {
                throw new RuntimeException(parameter.getParameterAnnotation(JsonParam.class).value() + "不能为空");
            }
        } catch (PathNotFoundException exception) {
            if (parameter.getParameterAnnotation(JsonParam.class).required()) {
                throw new RuntimeException(parameter.getParameterAnnotation(JsonParam.class).value() + "不能为空");
            }
        }
        return val;
    }

    /**
     * 获取请求体。
     *
     * @param webRequest 请求
     * @return String
     */
    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String jsonBody = (String) servletRequest.getAttribute(JSON_REQUEST_BODY);
        if (jsonBody == null) {
            try {
                jsonBody = FileCopyUtils.copyToString(new InputStreamReader(servletRequest.getInputStream()));
                servletRequest.setAttribute(JSON_REQUEST_BODY, jsonBody);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonBody;

    }


}
