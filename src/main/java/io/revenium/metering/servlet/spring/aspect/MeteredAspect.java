package io.revenium.metering.servlet.spring.aspect;

import io.revenium.metering.api.MeteringApi;
import io.revenium.metering.model.ElementDTO;
import io.revenium.metering.model.MeteringRequestDTO;
import io.revenium.metering.servlet.spring.annotation.Metered;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
public class MeteredAspect {

    final MeteringApi meteringApi;

    private final ExpressionParser parser = new SpelExpressionParser();

    public MeteredAspect(@Autowired MeteringApi meteringApi) {
        this.meteringApi = meteringApi;
    }

    @Around("@annotation(metered)")
    public Object aroundMeteredMethod(ProceedingJoinPoint joinPoint, Metered metered) throws Throwable {
        Object result = joinPoint.proceed();

        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("result", result);
        context.setVariable("args", joinPoint.getArgs());

        String subscriptionId = evaluateExpression(metered.subscriptionId(), context, String.class);
        String sourceId = evaluateExpression(metered.sourceId(), context, String.class);
        Map<String, Object> elements = evaluateExpression(metered.elements(), context, Map.class);

        MeteringRequestDTO requestDTO = new MeteringRequestDTO();
        requestDTO.method("GET");
        requestDTO.url(sourceId);
        requestDTO.application(subscriptionId);
        requestDTO.setResponseCode(200);
        requestDTO.setRequestHeaders(Collections.emptyList());
        requestDTO.setResponseHeaders(Collections.emptyList());
        requestDTO.setElements(elements.entrySet().stream()
                .map(entry -> new ElementDTO().name(entry.getKey()).value(entry.getValue().toString()))
                .collect(Collectors.toList()));
        requestDTO.setSource("SDK_SPRING");
        meteringApi.meter(requestDTO);
        return result;
    }


    private <T> T evaluateExpression(String expression, StandardEvaluationContext context, Class<T> clazz) {
        if (expression.isEmpty()) {
            return null;
        }
        return parser.parseExpression(expression).getValue(context, clazz);
    }
}
