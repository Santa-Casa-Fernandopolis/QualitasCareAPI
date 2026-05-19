package com.erp.qualitascareapi.cme.api.dto;

public record CmeDashboardDto(
        double sterilizationRate,
        double turnaroundTimeMinutes,
        long pendingLoads,
        long processosAbertos
) {}
