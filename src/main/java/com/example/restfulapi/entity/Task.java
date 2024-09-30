package com.example.restfulapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "task")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;

    @Column(name = "header_of_task", nullable = false)
    private String header;

    @Column(name = "description_of_task", nullable = false)
    private String description;

    @Column(name = "date_created", nullable = false)
    private LocalDateTime dateCreated;

    @Column(name = "status_of_task", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private TaskStatus taskStatus;

    @Column(name = "date_finish", nullable = false)
    private LocalDate dateFinish;

    public Task(String header, String description, TaskStatus taskStatus, LocalDate dateFinish) {
        this.header = header;
        this.description = description;
        this.dateCreated = LocalDate.now().atStartOfDay();
        this.taskStatus = taskStatus;
        this.dateFinish = dateFinish;
    }
}
