package com.study.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
@Configuration
public class WebMvcSupport extends WebMvcConfigurationSupport {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置虚拟路径，解决浏览器无法直接访问本地文件的安全性问题
        registry.addResourceHandler("/img/**").addResourceLocations("file:D:/image/");
        registry.addResourceHandler("/image/**").addResourceLocations("file:D:/informationimage/");
        //给静态资源放行
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

    }
}
