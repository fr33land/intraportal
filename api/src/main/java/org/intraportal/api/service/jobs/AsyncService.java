package org.intraportal.api.service.jobs;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface AsyncService {

    CompletableFuture<BaseJobManagerResponse> initOperation(String jobId, Object jobData) throws IOException;
    CompletableFuture<BaseJobManagerResponse> getOperation(String jobId);

}
