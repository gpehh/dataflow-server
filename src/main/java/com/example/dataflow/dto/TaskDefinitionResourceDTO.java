package com.example.dataflow.dto;

import lombok.*;
import org.springframework.cloud.dataflow.rest.resource.TaskExecutionResource;



public class TaskDefinitionResourceDTO {
    private String name;

    private String dslText;

    private String description;

    /**
     * Indicates whether this is a composed task.
     */
    @With
    private boolean composed;

    /**
     * Indicates whether this is a composed task element.
     */
    /**
     * The last execution of this task execution.
     */
    @With
    private TaskExecutionResourceDTO lastTaskExecution;


    public TaskDefinitionResourceDTO(String name, String dslText, String description, boolean composed, TaskExecutionResourceDTO lastTaskExecution) {
        this.name = name;
        this.dslText = dslText;
        this.description = description;
        this.composed = composed;
        this.lastTaskExecution = lastTaskExecution;
    }

    public TaskDefinitionResourceDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDslText() {
        return dslText;
    }

    public void setDslText(String dslText) {
        this.dslText = dslText;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isComposed() {
        return composed;
    }

    public void setComposed(boolean composed) {
        this.composed = composed;
    }

    public TaskExecutionResourceDTO getLastTaskExecution() {
        return lastTaskExecution;
    }

    public void setLastTaskExecution(TaskExecutionResourceDTO lastTaskExecution) {
        this.lastTaskExecution = lastTaskExecution;
    }
}
