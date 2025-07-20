package com.s21.devops.sample.gatewayservice.Metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class MetricsFilter implements Filter {

    private final Counter requestCounter;

    public MetricsFilter(MeterRegistry registry) {
        this.requestCounter = Counter.builder("gateway_requests_total")
                .description("Total number of HTTP requests received by gateway")
                .register(registry);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        requestCounter.increment();
        chain.doFilter(request, response);
    }
}
