package com.example.dataflow.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.batch.core.JobExecution;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties
@With
@NoArgsConstructor
@AllArgsConstructor
public class TaskExecutionResourceDTO {

    private long executionId;

    /**
     * The recorded exit code for the task.
     */
    private Integer exitCode;

    /**
     * User defined name for the task.
     */
    private String taskName;

    /**
     * Time of when the task was started.
     */
    private Date startTime;

    /**
     * Timestamp of when the task was completed/terminated.
     */
    private Date endTime;

    /**
     * Message returned from the task.
     */
    private String exitMessage;

    /**
     * The command line arguments that were used for this task execution.
     */
    private List<String> arguments;

    /**
     * List of {@link JobExecution}s that are associated with this task.
     */
    private List<Long> jobExecutionIds;

    /**
     * Error Message returned from a task execution.
     */
    private String errorMessage;

    /**
     * Task Execution ID that is set from an external source. i.e. CloudFoundry Deployment
     * Id.
     */
    private String externalExecutionId;

    /**
     * This Task Execution might be a child task as part of a composed
     * task and have a parent task execution id. This property can be null.
     */
    private Long parentExecutionId;

    /**
     * The resource that defines the task.
     */
    private String resourceUrl;

    /**
     * The application arguments, typically set via SPRING_APPLICATION_JSON env-var on deployment
     */
    private Map<String, String> appProperties;

    private Map<String, String> deploymentProperties;

    /**
     * The platform for which the task was executed.
     */
    private String platformName;

    private String taskExecutionStatus;

    private String composedTaskJobExecutionStatus;

    private String schemaTarget;
}
