package com.example.dataflow.controller;

import com.example.dataflow.dto.TaskDefinitionResourceDTO;
import com.example.dataflow.dto.TaskExecutionResourceDTO;
import com.example.dataflow.mapper.DataFlowResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.dataflow.aggregate.task.AggregateTaskExplorer;
import org.springframework.cloud.dataflow.core.TaskDefinition;
import org.springframework.cloud.dataflow.rest.client.DataFlowOperations;
import org.springframework.cloud.dataflow.rest.resource.TaskDefinitionResource;
import org.springframework.cloud.dataflow.rest.resource.TaskExecutionResource;
import org.springframework.cloud.dataflow.schema.AggregateTaskExecution;
import org.springframework.cloud.dataflow.schema.AppBootSchemaVersion;
import org.springframework.cloud.dataflow.schema.SchemaVersionTarget;
import org.springframework.cloud.dataflow.server.controller.support.TaskExecutionAwareTaskDefinition;
import org.springframework.cloud.dataflow.server.repository.TaskDefinitionRepository;
import org.springframework.cloud.dataflow.server.service.TaskJobService;
import org.springframework.cloud.dataflow.server.service.impl.TaskServiceUtils;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.cloud.task.repository.TaskExplorer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/cloud-tasks")
public class CloudTaskController {

    @Autowired(required = false)
    DataFlowResourceMapper mapper;
    @Autowired
    DataFlowOperations dataFlowOperations;

    @Autowired
    AggregateTaskExplorer taskExplorer;
    @Autowired
    TaskExplorer taskExplorerOriginal;
    TaskJobService taskJobService;



    @Autowired
    TaskDefinitionRepository taskDefinitionRepository;
//
//    public CloudTaskController(DataFlowOperations dataFlowOperations, AggregateTaskExplorer taskExplorer, TaskJobService taskJobService, TaskDefinitionRepository taskDefinitionRepository) {
//        this.dataFlowOperations = dataFlowOperations;
//        this.taskExplorer = taskExplorer;
//        this.taskJobService = taskJobService;
//        this.taskDefinitionRepository = taskDefinitionRepository;
//    }

    @GetMapping(value = "/definitions")
    public ResponseEntity<List<TaskDefinitionResource>> getDefinitions(){
        List<TaskDefinitionResource> taskDefinitionResources=dataFlowOperations.taskOperations()
                .list().getContent().parallelStream().collect(Collectors.toList());

        return ResponseEntity.ok(taskDefinitionResources);
    }

    @GetMapping(value = "/definitions", params = "page")
    public ResponseEntity<Page<TaskDefinitionResourceDTO>> getTaskDefinitions(
            @PageableDefault(sort = "taskName")Pageable pageable){

        Page<TaskDefinition> taskDefinitions = taskDefinitionRepository.findAll(pageable);
        final Map<String,TaskDefinition> taskDefinitionMap=taskDefinitions.stream()
                .collect(Collectors.toMap(TaskDefinition::getTaskName, Function.identity()));

        final Map<String, TaskExecution> taskExecutions;
        final Map<String, AggregateTaskExecution> aggregateTaskExecutionMap = new HashMap<>();


        if(!taskDefinitionMap.isEmpty()){
            taskExecutions = this.taskExplorerOriginal.getLatestTaskExecutionsByTaskNames(
                    taskDefinitionMap.keySet().toArray(new String[0]))
                    .stream().collect(Collectors.toMap(TaskExecution::getTaskName,Function.identity()));

        }
        else {
            taskExecutions = Collections.emptyMap();
        }


        Page<TaskDefinition> pageFilterData =
                new PageImpl<TaskDefinition>(taskDefinitions
                        .stream()
                        .filter(taskDefinition -> taskExecutions.get(taskDefinition.getTaskName())!=null)
                        .collect(Collectors.toList()), pageable, taskDefinitions.getTotalElements());

        // İkinci Map'i oluşturalım (Map<String, AggregateTaskExecution>)
        for (Map.Entry<String, TaskExecution> entry : taskExecutions.entrySet()) {
            String key = entry.getKey();
            TaskExecution taskExecution = entry.getValue();

            // TaskExecution nesnesini AggregateTaskExecution'a dönüştürelim
            AggregateTaskExecution aggregateTaskExecution = new AggregateTaskExecution(taskExecution.getExecutionId()
                    , taskExecution.getExitCode(), taskExecution.getTaskName(),taskExecution.getStartTime(),taskExecution.getEndTime(),taskExecution.getExitMessage()
            ,taskExecution.getArguments(),taskExecution.getErrorMessage(),taskExecution.getExternalExecutionId(),
                    taskExecution.getParentExecutionId(), AppBootSchemaVersion.BOOT2.getBootVersion(), SchemaVersionTarget.defaultTarget().getName());
            // Daha fazla alan varsa, burada kopyalama işlemini devam ettirebilirsiniz

            // Yeni oluşturulan AggregateTaskExecution nesnesini ikinci haritaya ekleyelim
            aggregateTaskExecutionMap.put(key, aggregateTaskExecution);
        }

        // İkinci Map'i yazdıralım
        System.out.println("Copied Map:");
        for (Map.Entry<String, AggregateTaskExecution> entry : aggregateTaskExecutionMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        Page<TaskDefinitionResourceDTO> definitions = pageFilterData
                .map(taskDefinition -> new TaskExecutionAwareTaskDefinition(taskDefinition, aggregateTaskExecutionMap.get(taskDefinition.getTaskName())))
                .map(mapper::toTaskDefinitionResourceDTO)
                .map(taskDefinition->taskDefinition.withLastTaskExecution(fetchData(taskDefinition.getLastTaskExecution())))
                .map(taskDefinition -> taskDefinition.withComposed(TaskServiceUtils.isComposedTaskDefinition(taskDefinition.getDslText())));
               // .stream().collect(Collectors.toList());





        return  ResponseEntity.ok(definitions);
    }
    private TaskExecutionResourceDTO fetchData(TaskExecutionResourceDTO execution){
        List<Long> jobExecutionIds = new ArrayList<>(this.taskExplorerOriginal.getJobExecutionIdsByTaskExecutionId(execution.getExecutionId()));
        return execution.withJobExecutionIds(jobExecutionIds);
    }

}

