package dev.yerokha.flightstatusapi.infrastructure.aspect;

import dev.yerokha.flightstatusapi.infrastructure.service.TokenService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Autowired
    private TokenService tokenService;

    @AfterReturning(pointcut = "execution(* dev.yerokha.flightstatusapi.application.service.FlightService.addFlight(..)) " +
                            "|| execution(* dev.yerokha.flightstatusapi.application.service.FlightService.updateFlightStatus(..))",
            returning = "result")
    public void logAfterDatabaseChange(JoinPoint joinPoint, Object result) {
        String username = tokenService.getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        logger.info("User '{}' made changes in database. Method: {}. Arguments: {}. Result: {}",
                username,
                methodName,
                methodArgs,
                result);
    }

    @AfterThrowing(pointcut = "execution(* dev.yerokha.flightstatusapi.*(..))", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String username = tokenService.getCurrentUsername();
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        logger.error("User '{}' encountered an error. Method: {}. Arguments: {}. Exception: {}",
                username,
                methodName,
                methodArgs,
                exception.getMessage(),
                exception);
    }
}
