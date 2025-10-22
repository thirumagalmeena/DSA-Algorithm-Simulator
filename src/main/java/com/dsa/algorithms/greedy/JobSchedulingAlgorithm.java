package com.dsa.algorithms.greedy;

import java.util.*;

/**
 * Job Scheduling Algorithm (Greedy)
 *
 * Schedules jobs to maximize profit given deadlines.
 * Each job has a deadline and a profit.
 * Only one job can be scheduled at a time unit.
 * Steps are recorded for visualization.
 */
public class JobSchedulingAlgorithm {

    /** Step representation for visualization */
    public static class Step {
        public int jobId;
        public int slot;
        public boolean scheduled;
        public int[] schedule; // current schedule

        public Step(int jobId, int slot, boolean scheduled, int[] schedule) {
            this.jobId = jobId;
            this.slot = slot;
            this.scheduled = scheduled;
            this.schedule = schedule.clone();
        }
    }

    public static class Job {
        int id, deadline, profit;
        Job(int id, int deadline, int profit) {
            this.id = id;
            this.deadline = deadline;
            this.profit = profit;
        }
    }

    private final List<Step> steps = new ArrayList<>();

    /**
     * Schedules jobs to maximize profit
     * @param jobs array of jobs
     * @return array of scheduled job IDs in each time slot (-1 if empty)
     */
    public int[] scheduleJobs(Job[] jobs) {
        steps.clear();

        // Sort jobs by decreasing profit
        Arrays.sort(jobs, (a, b) -> b.profit - a.profit);

        int maxDeadline = Arrays.stream(jobs).mapToInt(j -> j.deadline).max().orElse(0);
        int[] schedule = new int[maxDeadline];
        Arrays.fill(schedule, -1);

        for (Job job : jobs) {
            boolean scheduled = false;
            // Try to schedule in latest free slot before deadline
            for (int t = Math.min(maxDeadline, job.deadline) - 1; t >= 0; t--) {
                if (schedule[t] == -1) {
                    schedule[t] = job.id;
                    scheduled = true;
                    break;
                }
            }
            steps.add(new Step(job.id, scheduled ? Arrays.stream(schedule).reduce(0, (a,b)->b==job.id?Arrays.asList(schedule).indexOf(b):a) : -1, scheduled, schedule));
        }

        return schedule;
    }

    /** Returns total profit of scheduled jobs */
    public int getTotalProfit(Job[] jobs, int[] schedule) {
        Map<Integer, Integer> profitMap = new HashMap<>();
        for (Job job : jobs) profitMap.put(job.id, job.profit);

        int total = 0;
        for (int id : schedule) {
            if (id != -1) total += profitMap.get(id);
        }
        return total;
    }

    /** Returns all recorded steps */
    public List<Step> getSteps() {
        return steps;
    }
}
