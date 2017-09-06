package com.midvision.rapiddeploy.bamboo;

import org.jetbrains.annotations.NotNull;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.security.EncryptionService;
import com.atlassian.bamboo.task.CommonTaskContext;
import com.atlassian.bamboo.task.CommonTaskType;
import com.atlassian.bamboo.task.TaskException;
import com.atlassian.bamboo.task.TaskResult;
import com.atlassian.bamboo.task.TaskResultBuilder;
import com.midvision.rapiddeploy.connector.RapidDeployConnector;

public class PackageBuildTask implements CommonTaskType {

	private EncryptionService encryptionService;

	@NotNull
	@java.lang.Override
	public TaskResult execute(@NotNull final CommonTaskContext taskContext) throws TaskException {
		final BuildLogger buildLogger = taskContext.getBuildLogger();

		final String serverUrl = taskContext.getConfigurationMap().get("serverUrl");
		final String authenticationToken = encryptionService.decrypt(taskContext.getConfigurationMap().get(PackageBuildTaskConfigurator.AUTHENTICATION_TOKEN));
		final String project = taskContext.getConfigurationMap().get("rapiddeployProjectName");
		final String packageName = taskContext.getConfigurationMap().get("packageName");
		final String archiveExtension = taskContext.getConfigurationMap().get("archiveExtension");
		buildLogger.addBuildLogEntry("Invoking RapidDeploy package builder via path: " + serverUrl);
		try {
			final String output = RapidDeployConnector.invokeRapidDeployBuildPackage(authenticationToken, serverUrl, project, packageName, archiveExtension, false,
					true);
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
					buildLogger.addBuildLogEntry("Job status: " + jobStatus);
					if ((jobStatus.equals("DEPLOYING")) || (jobStatus.equals("QUEUED")) || (jobStatus.equals("STARTING")) || (jobStatus.equals("EXECUTING"))) {
						buildLogger.addBuildLogEntry("Job running, next check in 30 seconds...");
						milisToSleep = 30000L;
					} else if ((jobStatus.equals("REQUESTED")) || (jobStatus.equals("REQUESTED_SCHEDULED"))) {
						buildLogger.addBuildLogEntry("Job in a REQUESTED state. Approval may be required in RapidDeploy "
								+ "to continue with the execution, next check in 30 seconds...");
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
			if (!success) {
				throw new RuntimeException("RapidDeploy job failed. Please check the output." + System.getProperty("line.separator") + logs);
			}
			buildLogger.addBuildLogEntry("RapidDeploy job successfully run. Please check the output.");
			buildLogger.addBuildLogEntry(System.getProperty("line.separator"));
			buildLogger.addBuildLogEntry(logs);
		} catch (final Exception e) {
			buildLogger.addBuildLogEntry("An exception occurred while performing the RapidDeploy package build: ");
			buildLogger.addBuildLogEntry(e.getMessage());
			return TaskResultBuilder.newBuilder(taskContext).failed().build();
		}
		buildLogger.addBuildLogEntry("Package build successfully requested!");

		return TaskResultBuilder.newBuilder(taskContext).success().build();
	}

	public void setEncryptionService(final EncryptionService encryptionService) {
		this.encryptionService = encryptionService;
	}

}
