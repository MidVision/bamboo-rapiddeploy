package com.midvision.rapiddeploy.bamboo;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import org.jetbrains.annotations.NotNull;
import com.midvision.rapiddeploy.connector.RapidDeployConnector;

public class PackageBuildTask implements TaskType
{
    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();


        final String serverUrl = taskContext.getConfigurationMap().get("serverUrl");
        final String authenticationToken =  taskContext.getConfigurationMap().get("authenticationToken");
        final String project =  taskContext.getConfigurationMap().get("rapiddeployProjectName");
        final String packageName =  taskContext.getConfigurationMap().get("packageName");
        final String archiveExension =  taskContext.getConfigurationMap().get("archiveExension");
        buildLogger.addBuildLogEntry("Invoking RapidDeploy package builder via path: " + serverUrl);
        String output = null;
        try {
          output = RapidDeployConnector.invokeRapidDeployBuildPackage(authenticationToken, serverUrl, project, packageName, archiveExension, true);
        }catch(Exception e){
          buildLogger.addBuildLogEntry("An exception occurred while performing rapiddeploy package build: ");
          buildLogger.addBuildLogEntry(e.getMessage());
        }
        if(output != null){
          buildLogger.addBuildLogEntry(output);
        }
        buildLogger.addBuildLogEntry("Package build successfully requested!");

        return TaskResultBuilder.create(taskContext).success().build();
    }
}
