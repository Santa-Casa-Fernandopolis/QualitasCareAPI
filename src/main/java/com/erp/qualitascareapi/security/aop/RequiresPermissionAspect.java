package com.erp.qualitascareapi.security.aop;

import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.permission.CheckPermission;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequiresPermissionAspect {

    private final CheckPermission checkPermission;

    public RequiresPermissionAspect(CheckPermission checkPermission) {
        this.checkPermission = checkPermission;
    }

    @Around("@annotation(annotation)")
    public Object enforce(ProceedingJoinPoint joinPoint, RequiresPermission annotation) throws Throwable {
        checkPermission.check(annotation.resource(), annotation.action(), annotation.feature());
        return joinPoint.proceed();
    }
}
