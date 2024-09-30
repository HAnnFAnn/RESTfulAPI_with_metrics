package com.example.restfulapi.dto;

import com.example.restfulapi.entity.TaskStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {

  String header;

  String description;

  TaskStatus status;

  LocalDate dateFinish;
}
