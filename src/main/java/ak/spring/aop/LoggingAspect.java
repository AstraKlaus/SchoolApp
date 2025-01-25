package ak.spring.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private static final int MAX_PARAM_LENGTH = 100;
    private static final String SENSITIVE_KEYWORDS = "password|token|secret";

    @Around("execution(* ak.spring..*(..)) && !execution(* ak.spring.configs..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = getMethodSignature(joinPoint);

        // Логирование входа
        log.info("===> {} - Вход", methodName);
        log.debug("Аргументы: {}", maskSensitiveData(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();

            // Логирование выхода
            log.info("<=== {} - Выход [{} ms]", methodName, System.currentTimeMillis() - startTime);
            log.debug("Результат: {}", truncate(convertToString(result)));

            return result;
        } catch (Exception ex) {
            // Логирование ошибок
            log.error("<=== {} - ОШИБКА [{} ms] || {}: {}",
                    methodName,
                    System.currentTimeMillis() - startTime,
                    ex.getClass().getSimpleName(),
                    ex.getMessage());

            log.debug("Stack trace for {}:", methodName, ex);
            throw ex;
        }
    }

    private String getMethodSignature(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return String.format("%s.%s",
                signature.getDeclaringType().getSimpleName(),
                signature.getName());
    }

    private Object maskSensitiveData(Object[] args) {
        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    String str = arg.toString();
                    if (str.matches("(?i).*(" + SENSITIVE_KEYWORDS + ").*")) {
                        return "[СКРЫТО]";
                    }
                    return truncate(str);
                })
                .toList();
    }

    private String truncate(String value) {
        return value.length() > MAX_PARAM_LENGTH
                ? value.substring(0, MAX_PARAM_LENGTH) + "... [TRUNCATED]"
                : value;
    }

    private String convertToString(Object obj) {
        try {
            return obj != null ? obj.toString() : "null";
        } catch (Exception ex) {
            return "[ToString Error]";
        }
    }
}

