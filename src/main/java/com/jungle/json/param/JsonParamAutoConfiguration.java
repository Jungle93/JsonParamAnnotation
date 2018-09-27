package com.jungle.json.param;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author jungle
 * @version V1.0
 * @date 2018/9/27 19:55
 * @Title: JsonParamAutoConfiguration.java
 * @Package com.jungle.json.param
 * @Description: 自定义配置控制器
 * copyright © 2018- holmes.com
 */
@Configuration
public class JsonParamAutoConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new JsonPathArgumentResolver());
    }

}
