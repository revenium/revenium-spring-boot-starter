package io.revenium.metering.servlet.spring.filter;

import io.revenium.metering.api.MeteringApi;
import io.revenium.metering.invoker.ApiCallback;
import io.revenium.metering.invoker.ApiException;
import io.revenium.metering.model.MeteringRequestDTO;
import io.revenium.metering.model.Unit;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class MeteringFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(MeteringFilter.class);

    private final MeteringApi meteringApi;

    public MeteringFilter(@Autowired MeteringApi meteringApi) {
        this.meteringApi = meteringApi;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {

        long startTime = System.currentTimeMillis();
        chain.doFilter(request, response);
        long elapsed = System.currentTimeMillis() - startTime;

        if (response.isCommitted() && response instanceof HttpServletResponse) {
            try {
                meteringApi.meterAsync(
                        getMeteringRequestDTO((HttpServletRequest) request,
                                (HttpServletResponse) response, elapsed), new MeteringApiCallback());
            } catch (Exception e) {
                logger.error("Error while sending metering data", e);
            }
        }
    }

    @NotNull
    private static MeteringRequestDTO getMeteringRequestDTO(HttpServletRequest request,
                                                            HttpServletResponse response,
                                                            double elapsed) {

        MeteringRequestDTO requestDTO = new MeteringRequestDTO();
        requestDTO.method(request.getMethod());
        requestDTO.url(request.getRequestURL().toString());
        requestDTO.application(response.getHeader("clientId"));
        requestDTO.setResponseCode(response.getStatus());
        requestDTO.setRequestHeaders(Collections.emptyList());
        requestDTO.setResponseHeaders(Collections.emptyList());
        requestDTO.setContentType(request.getContentType());
        requestDTO.setRequestMessageSize(request.getContentLengthLong());
        requestDTO.setResponseMessageSize((long) response.getBufferSize());
        requestDTO.backendLatency(elapsed);
        requestDTO.setSource("SDK_SPRING");
        return requestDTO;
    }
}

/**
 * Callback for metering API
 */
class MeteringApiCallback implements ApiCallback<Unit> {

    private static final Logger logger = LoggerFactory.getLogger(MeteringApiCallback.class);

    @Override
    public void onFailure(ApiException e, int statusCode, Map responseHeaders) {
        logger.error(String.format("Error while sending metering data: %d", statusCode), e);
    }

    @Override
    public void onSuccess(Unit result, int statusCode, Map responseHeaders) {
        logger.debug("Metering metadata sent successfully");
    }

    @Override
    public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
        // NoOp
    }

    @Override
    public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
        // NoOp
    }
}
