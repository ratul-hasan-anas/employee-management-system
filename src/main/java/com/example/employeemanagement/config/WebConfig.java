
package com.example.employeemanagement.config;

import com.example.employeemanagement.interceptor.EmployeeInterceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final EmployeeInterceptor employeeInterceptor;

    // Constructor Injection
    public WebConfig(EmployeeInterceptor employeeInterceptor) {
        this.employeeInterceptor = employeeInterceptor;
    }

    // Register Interceptor
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(employeeInterceptor)
                .addPathPatterns("/employee/**");
    }
}
