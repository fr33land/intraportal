package org.intraportal.webtool.controller.jobs;

import org.intraportal.api.service.jobs.AsyncJobsManager;
import org.intraportal.api.service.jobs.BaseJobManagerResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("JobsController")
@RequestMapping("/web/jobs")
public class JobsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobsController.class);

    private final AsyncJobsManager jobsManager;

    @Autowired
    public JobsController(AsyncJobsManager jobsManager) {
        this.jobsManager = jobsManager;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'VIEWER')")
    @GetMapping(path = "/{job-id}", produces = "application/json")
    public ResponseEntity<BaseJobManagerResponse> getJobStatus(@PathVariable(name = "job-id") String jobId) throws Throwable {
        LOGGER.debug("Received request for job status job-id: {}", jobId);
        BaseJobManagerResponse jobStatus = jobsManager.getJobStatus(jobId);
        return ResponseEntity.ok(jobStatus == null ? new BaseJobManagerResponse(jobId, null, 0, null, null) : jobStatus);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(path = "/{job-id}", produces = "application/json")
    public ResponseEntity<Void> deleteJobAndAssociatedData(@PathVariable(name = "job-id") String jobId) throws Throwable {
        LOGGER.debug("Received request to delete job-id: {}", jobId);
        jobsManager.deleteJob(jobId);
        return ResponseEntity.noContent().build();
    }
}

