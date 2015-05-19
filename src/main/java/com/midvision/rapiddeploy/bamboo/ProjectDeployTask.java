package com.midvision.rapiddeploy.bamboo;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import org.jetbrains.annotations.NotNull;
import com.midvision.rapiddeploy.connector.RapidDeployConnector;

public class ProjectDeployTask implements TaskType
{
    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        final String serverUrl = taskContext.getConfigurationMap().get("serverUrl");
        final String authenticationToken =  taskContext.getConfigurationMap().get("authenticationToken");
        final String project =  taskContext.getConfigurationMap().get("rapiddeployProjectName");
        final String server =  taskContext.getConfigurationMap().get("rapiddeployServerName");
        final String environment =  taskContext.getConfigurationMap().get("rapiddeployEnvironmentName");
        final String application =  taskContext.getConfigurationMap().get("rapiddeployAppName");
        final String packageName =  taskContext.getConfigurationMap().get("packageName");
        // final Boolean isAsynchronous =  taskContext.getConfigurationMap().get("isAsynchronous");

        String output = null;
        try {
          output = RapidDeployConnector.invokeRapidDeployDeployment(authenticationToken, serverUrl, project, server, environment, null, application, packageName, null, null, null, null, null);
        }catch(Exception e){
          buildLogger.addBuildLogEntry("An exception occurred while performing rapiddeploy package build");
          buildLogger.addBuildLogEntry(e.getMessage());
        }

        if(output != null){
          buildLogger.addBuildLogEntry(output);
        }
        buildLogger.addBuildLogEntry("Project Deploy task has been performed!");

        return TaskResultBuilder.create(taskContext).success().build();
    }
}
