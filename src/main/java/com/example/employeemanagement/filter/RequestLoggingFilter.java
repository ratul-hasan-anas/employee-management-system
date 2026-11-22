
package com.example.employeemanagement.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import org.jspecify.annotations.NonNull;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    // Logger object: Console-এ request ও response information দেখাবে
    private static final Logger logger =
            LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Request আসার সময় record করা
        long startTime = System.currentTimeMillis();

        try {

            // Client থেকে আসা Request-এর তথ্য
            logger.info(
                    "Incoming Request: Method = {}, URI = {}",
                    request.getMethod(),
                    request.getRequestURI()
            );

            // Request-কে পরবর্তী processing-এ পাঠানো
            filterChain.doFilter(request, response);

        } finally {

            // Request processing শেষ হওয়ার সময়
            long endTime = System.currentTimeMillis();

            // মোট কত সময় লেগেছে
            long duration = endTime - startTime;

            // Response-এর তথ্য Console-এ দেখানো
            logger.info(
                    "Outgoing Response: Method = {}, URI = {}, Status = {}, Time = {} ms",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    duration
            );
        }
    }
}
