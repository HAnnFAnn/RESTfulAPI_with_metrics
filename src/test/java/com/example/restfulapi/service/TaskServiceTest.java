package com.example.restfulapi.service;

import com.example.restfulapi.entity.Task;
import com.example.restfulapi.entity.TaskStatus;
import com.example.restfulapi.repo.TaskRepository;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class TaskServiceTest {

    @Autowired
    private TaskRepository taskRepository;

    private SimpleMeterRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
    }

    @Test
    void testInsert100kTasksWithMeterRegistry() {

        Timer timer = Timer.builder("task.insertion.time")
                .description("Time taken to insert 100k tasks")
                .register(registry);

        timer.record(() -> {
            List<Task> tasks = IntStream.range(0, 100_000).mapToObj(createTask()).toList();
            taskRepository.saveAll(tasks);
        });

        assertTrue(timer.count() > 0, "Tasks should be inserted at least once");
        assertTrue(timer.totalTime(TimeUnit.MILLISECONDS) > 0, "Total time should be greater than 0 ms");
    }

    @Test
    void testConcurrentSelectsWithSnapshot() throws InterruptedException {

        DistributionSummary summary = DistributionSummary.builder("query.execution.time")
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);

        var executor = Executors.newFixedThreadPool(100);
        IntStream.range(0, 1_000_000)
                .<Runnable>mapToObj(i -> () -> {
                    long startTime = System.nanoTime();
                    taskRepository.findById(new Random().nextInt(100_000) + 1);
                    long duration = System.nanoTime() - startTime;
                    summary.record(duration / 1_000_000.0);
                }).forEach(executor::execute);

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        var snapshot = summary.takeSnapshot();
        assertTrue(snapshot.mean() < 100, "Average query time should be less than 100 ms");

        var expectedExecutionTime = Map.of(
                50.0, 200,
                95.0, 200,
                99.0, 300

        );
        Arrays.stream(snapshot.percentileValues())
                .forEach(percentileValue -> assertTrue(
                        percentileValue.value() < expectedExecutionTime.get(percentileValue.percentile()), "95th percentile should "
                                + "be less than "
                                + "200 ms"));
    }

    private IntFunction<Task> createTask() {
        return i -> {
            Task task = new Task();
            task.setHeader("Task " + i);
            task.setDescription("Description " + i);
            task.setDateCreated(LocalDate.now().atStartOfDay());
            task.setTaskStatus(TaskStatus.IN_PROGRESS);
            task.setDateFinish(LocalDate.now().plusDays(i));
            return task;
        };
    }
}