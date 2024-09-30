package com.example.restfulapi.service;

import com.example.restfulapi.dto.TaskDTO;
import com.example.restfulapi.entity.Task;
import com.example.restfulapi.entity.TaskStatus;
import com.example.restfulapi.exception.TaskNotFoundException;
import com.example.restfulapi.repo.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    private final ModelMapper modelMapper;

    public Page<TaskDTO> getTasks(int page, int size, TaskStatus taskStatus) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks;
        if (taskStatus != null) {
            tasks = taskRepository.findAllByStatus(taskStatus, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }
        return tasks.map(task -> modelMapper.map(task, TaskDTO.class));
    }

    @Transactional
    public Task createTask(TaskDTO taskDTO)  {
        LocalDate dateFinish = taskDTO.getDateFinish();

        Task task = new Task(
                taskDTO.getHeader(),
                taskDTO.getDescription(),
                TaskStatus.IN_PROGRESS,
                dateFinish
        );

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Integer id, TaskDTO request) {
        Optional<Task> existingTask = taskRepository.findById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            if (request.getHeader() != null) {
                task.setHeader(request.getHeader());
            }
            if (request.getDescription() != null) {
                task.setDescription(request.getDescription());
            }
            if (request.getStatus() != null) {
                task.setTaskStatus(request.getStatus());
            }
            return taskRepository.save(task);
        } else {
            throw new TaskNotFoundException("Задача с ID " + id + " не найдена");
        }
    }

    @Transactional
    public void deleteTask(Integer id) {
        taskRepository.deleteById(id);
    }
}