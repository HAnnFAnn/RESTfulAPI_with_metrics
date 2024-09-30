package com.example.restfulapi.repo;

import com.example.restfulapi.entity.TaskStatus;
import com.example.restfulapi.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface TaskRepository extends JpaRepository<Task, Integer> {

    @Query("SELECT t FROM Task t")
    Page<Task> findAll(Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.taskStatus = :taskStatus")
    Page<Task> findAllByStatus(TaskStatus taskStatus, Pageable pageable);

}
