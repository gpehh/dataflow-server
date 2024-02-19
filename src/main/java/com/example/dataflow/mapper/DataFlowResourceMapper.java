package com.example.dataflow.mapper;

import com.example.dataflow.dto.TaskDefinitionResourceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.cloud.dataflow.rest.resource.JobExecutionThinResource;
import org.springframework.cloud.dataflow.rest.resource.TaskDefinitionResource;
import org.springframework.cloud.dataflow.server.controller.support.TaskExecutionAwareTaskDefinition;

import static org.mapstruct.NullValuePropertyMappingStrategy.SET_TO_DEFAULT;

@Mapper(nullValuePropertyMappingStrategy = SET_TO_DEFAULT,componentModel = "spring")
public interface DataFlowResourceMapper {

    DataFlowResourceMapper INSTANCE = Mappers.getMapper(DataFlowResourceMapper.class);

//    JobExecutionThinResourceDTO toJobExecutionThinRsourceDTO(JobExecutionThinResource resource);

    @Mapping(source="taskDefinition.taskName", target="name")
    @Mapping(source="taskDefinition.dslText",  target="dslText")
    @Mapping(source="taskDefinition.description",  target="description")
    @Mapping(source="latestTaskExecution",  target="lastTaskExecution")
    TaskDefinitionResourceDTO toTaskDefinitionResourceDTO(TaskExecutionAwareTaskDefinition definition);
}
