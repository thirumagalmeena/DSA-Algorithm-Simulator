package com.dsa.algorithms.greedy;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class JobSchedulingAlgorithmTestJUnit {

    @Test
    void testBasicJobScheduling() {
        JobSchedulingAlgorithm algo = new JobSchedulingAlgorithm();

        JobSchedulingAlgorithm.Job[] jobs = {
                new JobSchedulingAlgorithm.Job(1, 2, 100),
                new JobSchedulingAlgorithm.Job(2, 1, 19),
                new JobSchedulingAlgorithm.Job(3, 2, 27),
                new JobSchedulingAlgorithm.Job(4, 1, 25),
                new JobSchedulingAlgorithm.Job(5, 3, 15)
        };

        int[] schedule = algo.scheduleJobs(jobs);
        int totalProfit = algo.getTotalProfit(jobs, schedule);

        // Expected schedule may vary depending on greedy choice, but total profit should be 142
        assertEquals(142, totalProfit, "Total profit should be 142.");

        // Check schedule length
        assertEquals(3, schedule.length, "Schedule length should be equal to max deadline.");
    }

    @Test
    void testStepRecording() {
        JobSchedulingAlgorithm algo = new JobSchedulingAlgorithm();

        JobSchedulingAlgorithm.Job[] jobs = {
                new JobSchedulingAlgorithm.Job(1, 2, 50),
                new JobSchedulingAlgorithm.Job(2, 1, 10)
        };

        algo.scheduleJobs(jobs);
        List<JobSchedulingAlgorithm.Step> steps = algo.getSteps();

        assertEquals(2, steps.size(), "There should be 2 recorded steps.");
        long scheduledCount = steps.stream().filter(s -> s.scheduled).count();
        assertEquals(2, scheduledCount, "Both jobs should be scheduled.");
    }

    @Test
    void testUnscheduledJob() {
        JobSchedulingAlgorithm algo = new JobSchedulingAlgorithm();

        JobSchedulingAlgorithm.Job[] jobs = {
                new JobSchedulingAlgorithm.Job(1, 1, 20),
                new JobSchedulingAlgorithm.Job(2, 1, 15),
                new JobSchedulingAlgorithm.Job(3, 1, 10)
        };

        int[] schedule = algo.scheduleJobs(jobs);
        int totalProfit = algo.getTotalProfit(jobs, schedule);

        assertEquals(20, totalProfit, "Only the most profitable job should be scheduled.");
        assertEquals(1, Arrays.stream(schedule).filter(s -> s != -1).count(), "Only one job should be scheduled.");
    }
}
