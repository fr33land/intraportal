package org.intraportal.api.service.jobs;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

@Service("AsyncJobsManager")
public class AsyncJobsManager {
    private final ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>> mapOfJobs;
    private final ConcurrentMap<String, BaseJobManagerResponse> mapOfJobsResults;

    public AsyncJobsManager() {
        mapOfJobs = new ConcurrentHashMap<>();
        mapOfJobsResults = new ConcurrentHashMap<>();
    }

    public void putJob(String jobId, CompletableFuture<BaseJobManagerResponse> theJob) {
        mapOfJobs.put(jobId, theJob);
    }

    public CompletableFuture<BaseJobManagerResponse> getJob(String jobId) {
        return mapOfJobs.get(jobId);
    }

    public void removeJob(String jobId) {
        mapOfJobs.remove(jobId);
        mapOfJobsResults.remove(jobId);
    }

    public void setJobStatus(String jobId, BaseJobManagerResponse status) {
        mapOfJobsResults.put(jobId, status);
    }

    public BaseJobManagerResponse getJobStatus(String jobId) {
        return mapOfJobsResults.get(jobId);
    }

    public void deleteJob(String jobId) {
        CompletableFuture<BaseJobManagerResponse> job = mapOfJobs.get(jobId);
        job.complete(new BaseJobManagerResponse(jobId, "job removed from manager", 0, null, Boolean.FALSE, LocalDateTime.now()));
        removeJob(jobId);
    }

    public void removeJobThread(String jobId) {
        mapOfJobs.remove(jobId);
    }
}
