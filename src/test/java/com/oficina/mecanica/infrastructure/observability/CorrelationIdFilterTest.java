package com.oficina.mecanica.infrastructure.observability;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @Test
    void devePreservarCorrelationIdRecebido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.HEADER_NAME, "correlation-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, emptyChain());

        assertEquals("correlation-123", response.getHeader(CorrelationIdFilter.HEADER_NAME));
    }

    @Test
    void deveGerarCorrelationIdQuandoAusente() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(new MockHttpServletRequest(), response, emptyChain());

        assertFalse(response.getHeader(CorrelationIdFilter.HEADER_NAME).isBlank());
        assertFalse(response.getHeader(CorrelationIdFilter.TRACE_HEADER_NAME).isBlank());
    }

    @Test
    void deveExtrairTraceIdDeTraceparentW3C() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("traceparent", "00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, emptyChain());

        assertEquals("4bf92f3577b34da6a3ce929d0e0e4736", response.getHeader(CorrelationIdFilter.TRACE_HEADER_NAME));
    }

    @Test
    void deveExtrairDatadogTraceIdEIgnorarHeadersInvalidos() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.HEADER_NAME, "   ");
        request.addHeader("traceparent", "formato-invalido");
        request.addHeader("x-datadog-trace-id", "dd-trace-98765");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, emptyChain());

        assertFalse(response.getHeader(CorrelationIdFilter.HEADER_NAME).isBlank());
        assertEquals("dd-trace-98765", response.getHeader(CorrelationIdFilter.TRACE_HEADER_NAME));

        MockHttpServletRequest oversizeReq = new MockHttpServletRequest();
        oversizeReq.addHeader(CorrelationIdFilter.HEADER_NAME, "x".repeat(129));
        oversizeReq.addHeader("traceparent", "00-curto-00f067aa0ba902b7-01");
        oversizeReq.addHeader("x-datadog-trace-id", "   ");
        MockHttpServletResponse oversizeResp = new MockHttpServletResponse();

        filter.doFilter(oversizeReq, oversizeResp, emptyChain());

        assertEquals(36, oversizeResp.getHeader(CorrelationIdFilter.HEADER_NAME).length());
        assertEquals(32, oversizeResp.getHeader(CorrelationIdFilter.TRACE_HEADER_NAME).length());

        MockHttpServletRequest oversizeDdReq = new MockHttpServletRequest();
        oversizeDdReq.addHeader("traceparent", "   ");
        oversizeDdReq.addHeader("x-datadog-trace-id", "y".repeat(65));
        MockHttpServletResponse oversizeDdResp = new MockHttpServletResponse();
        filter.doFilter(oversizeDdReq, oversizeDdResp, emptyChain());
        assertEquals(32, oversizeDdResp.getHeader(CorrelationIdFilter.TRACE_HEADER_NAME).length());
    }

    private FilterChain emptyChain() {
        return (request, response) -> { };
    }
}
