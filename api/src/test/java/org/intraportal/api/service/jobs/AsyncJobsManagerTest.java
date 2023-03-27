package org.intraportal.api.service.jobs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AsyncJobsManagerTest {

    private ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>> mapOfJobs = new ConcurrentHashMap<>();
    private ConcurrentMap<String, BaseJobManagerResponse> mapOfJobsResults = new ConcurrentHashMap<>();

    @InjectMocks
    AsyncJobsManager asyncJobsManager;

    @BeforeEach
    void setup() {

        mapOfJobs.put("1", CompletableFuture.completedFuture(new BaseJobManagerResponse(
                "1", "Started", 0, null, LocalDateTime.now()))
        );

        mapOfJobs.put("2", CompletableFuture.completedFuture(new BaseJobManagerResponse(
                "2", "Progress", 10, null, LocalDateTime.now()))
        );

        mapOfJobs.put("3", CompletableFuture.completedFuture(new BaseJobManagerResponse(
                "3", "Finished", 100, null, LocalDateTime.now()))
        );

        ReflectionTestUtils.setField(asyncJobsManager, "mapOfJobs", mapOfJobs);

        mapOfJobsResults.put("1", new BaseJobManagerResponse(
                "1", "Finished", 100, "http://localhost/jobs/download/1", LocalDateTime.now())
        );

        mapOfJobsResults.put("2", new BaseJobManagerResponse(
                "2", "Finished", 100, null, LocalDateTime.now())
        );

        mapOfJobsResults.put("3", new BaseJobManagerResponse(
                "3", "Finished", 100, null, LocalDateTime.now())
        );

        ReflectionTestUtils.setField(asyncJobsManager, "mapOfJobsResults", mapOfJobsResults);
    }

    @Test
    void putJob() throws NoSuchFieldException, IllegalAccessException {
        CompletableFuture<BaseJobManagerResponse> job5 =
                CompletableFuture.completedFuture(new BaseJobManagerResponse("1", "Started", 0, null, LocalDateTime.now()));
        asyncJobsManager.putJob("5", job5);

        Field mapOfJobs = AsyncJobsManager.class.getDeclaredField("mapOfJobs");
        mapOfJobs.setAccessible(true);
        ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>> fieldValue =
                (ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>>) mapOfJobs.get(asyncJobsManager);

        assertEquals(fieldValue.size(), 4);
        assertTrue(asyncJobsManager.getJob("5").equals(job5));
    }

    @Test
    void getJob() throws ExecutionException, InterruptedException {
        CompletableFuture<BaseJobManagerResponse> result = asyncJobsManager.getJob("1");
        assertNotNull(result);
        assertEquals(result.get(), mapOfJobs.get("1").get());
    }

    @Test
    void getJob_NonExisting() throws ExecutionException, InterruptedException {
        CompletableFuture<BaseJobManagerResponse> result = asyncJobsManager.getJob("11");
        assertNull(result);
    }

    @Test
    void removeJob() throws NoSuchFieldException, IllegalAccessException {
        asyncJobsManager.removeJob("1");

        Field mapOfJobs = AsyncJobsManager.class.getDeclaredField("mapOfJobs");
        mapOfJobs.setAccessible(true);

        Field mapOfJobsResults = AsyncJobsManager.class.getDeclaredField("mapOfJobsResults");
        mapOfJobsResults.setAccessible(true);

        ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>> mapOfJobsField =
                (ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>>) mapOfJobs.get(asyncJobsManager);

        ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>> mapOfJobsResultsField =
                (ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>>) mapOfJobs.get(asyncJobsManager);

        assertEquals(mapOfJobsField.size(), 2);
        assertEquals(mapOfJobsResultsField.size(), 2);
    }

    @Test
    void setJobStatus() throws NoSuchFieldException, IllegalAccessException, ExecutionException, InterruptedException {
        BaseJobManagerResponse jobStatus = new BaseJobManagerResponse("1", "In progress", 50, null, LocalDateTime.now());
        asyncJobsManager.setJobStatus("1", jobStatus);

        Field mapOfJobsResults = AsyncJobsManager.class.getDeclaredField("mapOfJobsResults");
        mapOfJobsResults.setAccessible(true);

        ConcurrentMap<String, BaseJobManagerResponse> fieldValue =
                (ConcurrentMap<String, BaseJobManagerResponse>) mapOfJobsResults.get(asyncJobsManager);

        assertNotNull(fieldValue.get("1"));
        assertEquals(fieldValue.get("1").getProgress(), 50);
        assertEquals(fieldValue.get("1").getMessage(), "In progress");
    }

    @Test
    void getJobStatus() {
        BaseJobManagerResponse result = asyncJobsManager.getJobStatus("2");

        assertNotNull(result);
        assertEquals(result.getProgress(), 100);
        assertEquals(result.getMessage(), "Finished");
    }

    @Test
    void getJobStatus_NonExisting() {
        BaseJobManagerResponse result = asyncJobsManager.getJobStatus("22");
        assertNull(result);
    }

    @Test
    void deleteJob() throws NoSuchFieldException, IllegalAccessException {
        asyncJobsManager.deleteJob("1");
        asyncJobsManager.deleteJob("2");

        Field mapOfJobs = AsyncJobsManager.class.getDeclaredField("mapOfJobs");
        mapOfJobs.setAccessible(true);

        Field mapOfJobsResults = AsyncJobsManager.class.getDeclaredField("mapOfJobsResults");
        mapOfJobsResults.setAccessible(true);

        ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>> mapOfJobsField =
                (ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>>) mapOfJobs.get(asyncJobsManager);

        ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>> mapOfJobsResultsField =
                (ConcurrentMap<String, CompletableFuture<BaseJobManagerResponse>>) mapOfJobs.get(asyncJobsManager);

        assertEquals(mapOfJobsField.size(), 1);
        assertEquals(mapOfJobsResultsField.size(), 1);
    }
}