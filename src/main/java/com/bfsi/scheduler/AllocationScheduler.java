package com.bfsi.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bfsi.service.OperationsBO;

/**
 * Fallback so investors are never left with a PENDING investment forever.
 * Periodically allocates any PENDING invest transaction older than the
 * configured threshold using the fund's current NAV.
 *
 * Config (application.properties):
 *   app.allocation.auto-threshold-days   default 1
 *   app.allocation.scheduler-rate-ms     default 3600000 (1 hour)
 */
@Component
public class AllocationScheduler {

    private final OperationsBO operationsBO;

    @Value("${app.allocation.auto-threshold-days:1}")
    private int thresholdDays;

    public AllocationScheduler(OperationsBO operationsBO) {
        this.operationsBO = operationsBO;
    }

    @Scheduled(fixedRateString = "${app.allocation.scheduler-rate-ms:3600000}")
    public void runAutoAllocation() {
        try {
            int allocated = operationsBO.autoAllocateStalePending(thresholdDays);
            if (allocated > 0) {
                System.out.println("[AllocationScheduler] Auto-allocated " + allocated
                        + " stale pending transaction(s).");
            }
        } catch (Exception e) {
            System.out.println("[AllocationScheduler] Auto-allocation run failed: " + e.getMessage());
        }
    }
}
