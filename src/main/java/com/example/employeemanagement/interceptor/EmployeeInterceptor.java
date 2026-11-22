package com.example.employeemanagement.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class EmployeeInterceptor implements HandlerInterceptor {

    private static final Logger logger =
            LoggerFactory.getLogger(EmployeeInterceptor.class);

    // 1. Controller execute হওয়ার আগে কাজ করে
    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {

        logger.info(
                "Interceptor PRE-HANDLE: {} {}",
                request.getMethod(),
                request.getRequestURI()
        );

        // true মানে request Controller-এর দিকে যেতে পারবে
        return true;
    }

    // 2. Controller execute হওয়ার পরে কাজ করে
    @Override
    public void postHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable ModelAndView modelAndView
    ) throws Exception {

        logger.info(
                "Interceptor POST-HANDLE: {} {}",
                request.getMethod(),
                request.getRequestURI()
        );
    }

    // 3. সম্পূর্ণ request processing শেষ হওয়ার পরে কাজ করে
    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            @Nullable Exception ex
    ) throws Exception {

        logger.info(
                "Interceptor AFTER-COMPLETION: {} {} | Status: {}",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus()
        );

        if (ex != null) {
            logger.error("Request completed with exception", ex);
        }
    }
}