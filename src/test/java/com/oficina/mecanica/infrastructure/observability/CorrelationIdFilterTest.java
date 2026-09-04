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
    }

    private FilterChain emptyChain() {
        return (request, response) -> { };
    }
}
