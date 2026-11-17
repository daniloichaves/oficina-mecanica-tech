package com.oficina.mecanica.infrastructure.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-Correlation-ID";
    public static final String TRACE_HEADER_NAME = "X-Trace-ID";
    public static final String MDC_KEY = "correlationId";
    public static final String TRACE_MDC_KEY = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader(HEADER_NAME);
        if (correlationId == null || correlationId.isBlank() || correlationId.length() > 128) {
            correlationId = UUID.randomUUID().toString();
        }
        String traceId = resolveTraceId(request);

        MDC.put(MDC_KEY, correlationId);
        MDC.put(TRACE_MDC_KEY, traceId);
        response.setHeader(HEADER_NAME, correlationId);
        response.setHeader(TRACE_HEADER_NAME, traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
            MDC.remove(TRACE_MDC_KEY);
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String traceparent = request.getHeader("traceparent");
        if (traceparent != null && !traceparent.isBlank()) {
            String[] parts = traceparent.split("-");
            if (parts.length >= 2 && parts[1].length() == 32) {
                return parts[1];
            }
        }
        String datadogTraceId = request.getHeader("x-datadog-trace-id");
        if (datadogTraceId != null && !datadogTraceId.isBlank() && datadogTraceId.length() <= 64) {
            return datadogTraceId;
        }
        return UUID.randomUUID().toString().replace("-", "");
    }
}
