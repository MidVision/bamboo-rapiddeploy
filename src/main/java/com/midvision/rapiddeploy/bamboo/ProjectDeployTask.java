package com.midvision.rapiddeploy.bamboo;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import org.jetbrains.annotations.NotNull;
import com.midvision.rapiddeploy.connector.RapidDeployConnector;
import com.atlassian.bamboo.security.EncryptionService;

public class ProjectDeployTask implements TaskType
{

  private EncryptionService encryptionService;

    @NotNull
    @java.lang.Override
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException
    {
        final BuildLogger buildLogger = taskContext.getBuildLogger();
        final String serverUrl = taskContext.getConfigurationMap().get("serverUrl");
        final String authenticationToken = encryptionService.decrypt(taskContext.getConfigurationMap().get(PackageBuildTaskConfigurator.AUTHENTICATION_TOKEN));
        final String project =  taskContext.getConfigurationMap().get("rapiddeployProjectName");
        final String server =  taskContext.getConfigurationMap().get("rapiddeployServerName");
        final String environment =  taskContext.getConfigurationMap().get("rapiddeployEnvironmentName");
        final String application =  taskContext.getConfigurationMap().get("rapiddeployAppName");
        final String packageName =  taskContext.getConfigurationMap().get("packageName");
        final Boolean isAsynchronous =  taskContext.getConfigurationMap().get("isAsynchronous").equals("true") ? true : false;
        final Boolean change_instance =  taskContext.getConfigurationMap().get("change_instance").equals("true") ? true : false;
        String instance = null;
        if(change_instance && taskContext.getConfigurationMap().get("instance") != null && taskContext.getConfigurationMap().get("instance").equals("") == false){
           instance = taskContext.getConfigurationMap().get("instance");
        }
        String output = null;
        try {
          output = RapidDeployConnector.invokeRapidDeployDeployment(authenticationToken, serverUrl, project, server, environment, instance, application, packageName, null, null, null, null, null);
          buildLogger.addBuildLogEntry("RapidDeploy job has successfully started!");
          if(!isAsynchronous){
            String jobId = RapidDeployConnector.extractJobId(output);
            if(jobId != null){
              buildLogger.addBuildLogEntry("Checking job status in every 30 seconds...");
              boolean runningJob = true;
              long millisToSleep = 30000;
              while(runningJob){
                Thread.sleep(millisToSleep);
                String jobDetails = RapidDeployConnector.pollRapidDeployJobDetails(authenticationToken, serverUrl, jobId);
                String jobStatus = RapidDeployConnector.extractJobStatus(jobDetails);
                buildLogger.addBuildLogEntry("Job status is: " + jobStatus);
                if(jobStatus.equals("DEPLOYING") || jobStatus.equals("QUEUED") || jobStatus.equals("STARTING") || jobStatus.equals("EXECUTING")){
                  buildLogger.addBuildLogEntry("Job is running, next check in 30 seconds..");
                  millisToSleep = 30000;
                } else if(jobStatus.equals("REQUESTED") || jobStatus.equals("REQUESTED_SCHEDULED")){
                  buildLogger.addBuildLogEntry("Job is in a REQUESTED state. Approval may be required in RapidDeploy to continue with execution, next check in 30 seconds..");
                } else if(jobStatus.equals("SCHEDULED")){
                  buildLogger.addBuildLogEntry("Job is in a SCHEDULED state, execution will start in a future date, next check in 5 minutes..");
                  buildLogger.addBuildLogEntry("Printing out job details");
                  buildLogger.addBuildLogEntry(jobDetails);
                  millisToSleep = 300000;
                } else {
                  runningJob = false;
                  buildLogger.addBuildLogEntry("Job is finished with status " + jobStatus);
							    if(jobStatus.equals("FAILED") || jobStatus.equals("REJECTED") || jobStatus.equals("CANCELLED") || jobStatus.equals("UNEXECUTABLE") || jobStatus.equals("TIMEDOUT") || jobStatus.equals("UNKNOWN")){
                    return TaskResultBuilder.create(taskContext).failed().build();
                  }
                }
              }
            } else{
						    throw new Exception("Could not retrieve job id, running asynchronously!");
					  }
            String logs = RapidDeployConnector.pollRapidDeployJobLog(authenticationToken, serverUrl, jobId);
            buildLogger.addBuildLogEntry(logs);
          } else{
            buildLogger.addBuildLogEntry("Job is running asynchronously. You can check the results of the deployments here once finished: " + serverUrl + "/ws/feed/" + project + "/list/jobs");
          }
          if(output != null){
            buildLogger.addBuildLogEntry(output);
          }
          buildLogger.addBuildLogEntry("Project Deploy task has been performed!");
          return TaskResultBuilder.create(taskContext).success().build();
        }catch(Exception e){
          buildLogger.addBuildLogEntry("An exception occurred while performing rapiddeploy project deployment: ");
          buildLogger.addBuildLogEntry(e.getMessage());
          return TaskResultBuilder.create(taskContext).failed().build();
        }
    }

    public void setEncryptionService(EncryptionService encryptionService)
    {
        this.encryptionService = encryptionService;
    }
}
