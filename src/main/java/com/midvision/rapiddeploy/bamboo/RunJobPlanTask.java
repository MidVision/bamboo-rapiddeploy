package com.midvision.rapiddeploy.bamboo;

import org.jetbrains.annotations.NotNull;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.security.EncryptionService;
import com.atlassian.bamboo.task.TaskContext;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.atlassian.bamboo.task.TaskType;
import com.midvision.rapiddeploy.connector.RapidDeployConnector;

public class RunJobPlanTask implements TaskType {

	private EncryptionService encryptionService;

	@NotNull
	@java.lang.Override
	public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException {
		final BuildLogger buildLogger = taskContext.getBuildLogger();
		final String serverUrl = taskContext.getConfigurationMap().get("serverUrl");
		final String authenticationToken = encryptionService.decrypt(taskContext.getConfigurationMap().get(PackageBuildTaskConfigurator.AUTHENTICATION_TOKEN));
		final String jobPlanId = taskContext.getConfigurationMap().get("jobPlanId");

		final Boolean isAsynchronous = taskContext.getConfigurationMap().get("isAsynchronous").equals("true") ? true : false;
		final Boolean showFullLogs = taskContext.getConfigurationMap().get("showFullLogs").equals("true") ? true : false;

		String output = null;
		try {

			output = RapidDeployConnector.invokeRapidDeployJobPlanPollOutput(authenticationToken, serverUrl, jobPlanId, true);
			if (!isAsynchronous) {
				buildLogger.addBuildLogEntry("The RapidDeploy job has successfully started!");
				boolean success = true;
				final String jobId = RapidDeployConnector.extractJobId(output);
				if (jobId != null) {
					buildLogger.addBuildLogEntry("Checking job status every 30 seconds...");
					boolean runningJob = true;
					long milisToSleep = 30000L;
					while (runningJob) {
						Thread.sleep(milisToSleep);
						final String jobDetails = RapidDeployConnector.pollRapidDeployJobDetails(authenticationToken, serverUrl, jobId);
						final String jobStatus = RapidDeployConnector.extractJobStatus(jobDetails);
						buildLogger.addBuildLogEntry("Job details: " + jobDetails);
						buildLogger.addBuildLogEntry("Job status: " + jobStatus);
						if ((jobStatus.equals("DEPLOYING")) || (jobStatus.equals("QUEUED")) || (jobStatus.equals("STARTING"))
								|| (jobStatus.equals("EXECUTING"))) {
							buildLogger.addBuildLogEntry("Job running, next check in 30 seconds...");
							milisToSleep = 30000L;
						} else if ((jobStatus.equals("REQUESTED")) || (jobStatus.equals("REQUESTED_SCHEDULED"))) {
							buildLogger
									.addBuildLogEntry("Job in a REQUESTED state. Approval may be required in RapidDeploy to continue with the execution, next check in 30 seconds...");
						} else if (jobStatus.equals("SCHEDULED")) {
							buildLogger.addBuildLogEntry("Job in a SCHEDULED state, the execution will start in a future date, next check in 5 minutes...");
							buildLogger.addBuildLogEntry("Printing out job details: ");
							buildLogger.addBuildLogEntry(jobDetails);
							milisToSleep = 300000L;
						} else {
							runningJob = false;
							buildLogger.addBuildLogEntry("Job finished with status: " + jobStatus);
							if ((jobStatus.equals("FAILED")) || (jobStatus.equals("REJECTED")) || (jobStatus.equals("CANCELLED"))
									|| (jobStatus.equals("UNEXECUTABLE")) || (jobStatus.equals("TIMEDOUT")) || (jobStatus.equals("UNKNOWN"))) {
								success = false;
							}
						}
					}
				} else {
					throw new RuntimeException("Could not retrieve job id, running asynchronously!");
				}
				final String logs = RapidDeployConnector.pollRapidDeployJobLog(authenticationToken, serverUrl, jobId);
				final StringBuilder fullLogs = new StringBuilder();
				fullLogs.append(logs).append("\n");

				if(showFullLogs){
					List<String> includedJobIds = extractIncludedJobIdsUnderPipelineJob(jobDetails);
					for(String internalJobId : includedJobIds){
						fullLogs.append("LOGS RELATED TO JOB ID: ").append(internalJobId).append("\n");
						fullLogs.append(RapidDeployConnector.pollRapidDeployJobLog(authenticationToken, serverUrl, internalJobId));
					}
				}

				if (!success) {
					throw new RuntimeException("RapidDeploy job failed. Please check the output." + System.getProperty("line.separator") + fullLogs.toString());
				}
				buildLogger.addBuildLogEntry("RapidDeploy job successfully run. Please check the output.");
				buildLogger.addBuildLogEntry(System.getProperty("line.separator"));
				buildLogger.addBuildLogEntry(fullLogs.toString());
			} else {
				buildLogger.addBuildLogEntry("The project Deploy task has been successfully requested!");
			}
			return TaskResultBuilder.newBuilder(taskContext).success().build();
		} catch (final Exception e) {
			buildLogger.addBuildLogEntry("An exception occurred while performing rapiddeploy project deployment: ");
			buildLogger.addBuildLogEntry(e.getMessage());
			return TaskResultBuilder.newBuilder(taskContext).failed().build();
		}
	}

	public void setEncryptionService(final EncryptionService encryptionService) {
		this.encryptionService = encryptionService;
	}
}
