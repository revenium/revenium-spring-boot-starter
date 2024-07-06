package io.revenium.metering;

import io.revenium.metering.api.MeteringApi;
import io.revenium.metering.spring.Metered;
import io.revenium.metering.spring.MeteredAspect;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeteredAspectTest {

    @Mock
    private MeteringApi meteringApi;

    @Mock
    private ProceedingJoinPoint joinPoint;

    private MeteredAspect meteredAspect;

    @BeforeEach
    void setUp() {
        meteredAspect = new MeteredAspect(meteringApi);
    }

    @Test
    void testAroundMeteredMethod() throws Throwable {
        TestService testService = new TestService();
        Method method = TestService.class.getMethod("testMethod", String.class, int.class);
        Metered metered = method.getAnnotation(Metered.class);

        when(joinPoint.getArgs()).thenReturn(new Object[]{"test-subscription", 5});
        when(joinPoint.proceed()).thenReturn(new TestResult("test-id", 10));

        meteredAspect.aroundMeteredMethod(joinPoint, metered);

        Map<String, Object> expectedElements = new HashMap<>();
        expectedElements.put("key1", 5);
        expectedElements.put("key2", 10);

        verify(meteringApi).meter(any());
    }

    private static class TestService {
        @Metered(
                subscriptionId = "#args[0]",
                sourceId = "#result.id",
                elements = "{'key1': #args[1], 'key2': #result.value}"
        )
        public TestResult testMethod(String subscriptionId, int someValue) {
            return new TestResult("test-id", 10);
        }
    }

    private static class TestResult {
        private final String id;
        private final int value;

        TestResult(String id, int value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public int getValue() {
            return value;
        }
    }
}