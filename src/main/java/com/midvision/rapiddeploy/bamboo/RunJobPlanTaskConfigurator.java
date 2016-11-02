package com.midvision.rapiddeploy.bamboo;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.security.EncryptionService;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;

public class RunJobPlanTaskConfigurator extends AbstractTaskConfigurator {

	private EncryptionService encryptionService;
	// public static final String CHANGE_INSTANCE = "change_instance";
	public static final String PLAIN_AUTHENTICATION_TOKEN = "authenticationToken";
	public static final String AUTHENTICATION_TOKEN = "encAuthenticationToken";
	public static final String CHANGE_AUTHENTICATION_TOKEN = "change_authentication_token";

	@NotNull
	@Override
	public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition) {
		final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
		config.put("serverUrl", params.getString("serverUrl"));
		String passwordChange = params.getString(CHANGE_AUTHENTICATION_TOKEN);
		if ("true".equals(passwordChange)) {
			final String password = params.getString(PLAIN_AUTHENTICATION_TOKEN);
			config.put(AUTHENTICATION_TOKEN, encryptionService.encrypt(password));
		} else if (previousTaskDefinition != null) {
			config.put(AUTHENTICATION_TOKEN, previousTaskDefinition.getConfiguration().get(AUTHENTICATION_TOKEN));
		} else {
			final String password = params.getString(PLAIN_AUTHENTICATION_TOKEN);
			config.put(AUTHENTICATION_TOKEN, encryptionService.encrypt(password));
		}
		config.put("jobPlanId", params.getString("jobPlanId"));

		if (params.getBoolean("isAsynchronous")) {
			config.put("isAsynchronous", "true");
		} else {
			config.put("isAsynchronous", "false");
		}
		if (params.getBoolean("showFullLogs")){
			config.put("showFullLogs", "true");
		}else {
			config.put("showFullLogs", "false");
		}


		return config;
	}

	@Override
	public void populateContextForCreate(@NotNull final Map<String, Object> context) {
		super.populateContextForCreate(context);

		context.put("serverUrl", "");
		context.put("jobPlanId", "");
		context.put("isAsynchronous", true);
		context.put("showFullLogs", false);
	}

	@Override
	public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition) {
		super.populateContextForEdit(context, taskDefinition);

		context.put("serverUrl", taskDefinition.getConfiguration().get("serverUrl"));

		context.put(PLAIN_AUTHENTICATION_TOKEN, taskDefinition.getConfiguration().get(AUTHENTICATION_TOKEN));
		context.put("jobPlanId", taskDefinition.getConfiguration().get("jobPlanId"));
		if (taskDefinition.getConfiguration().get("isAsynchronous").equals("true")) {
			context.put("isAsynchronous", true);
		} else {
			context.put("isAsynchronous", false);
		}
		if (taskDefinition.getConfiguration().get("showFullLogs").equals("true")) {
			context.put("showFullLogs", true);
		} else {
			context.put("showFullLogs", false);
		}
	}

	@Override
	public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition) {
		super.populateContextForView(context, taskDefinition);
		context.put("serverUrl", taskDefinition.getConfiguration().get("serverUrl"));
		context.put("jobPlanId", taskDefinition.getConfiguration().get("jobPlanId"));
		if (taskDefinition.getConfiguration().get("isAsynchronous").equals("true")) {
			context.put("isAsynchronous", true);
		} else {
			context.put("isAsynchronous", false);
		}
		if (taskDefinition.getConfiguration().get("showFullLogs").equals("true")) {
			context.put("showFullLogs", true);
		} else {
			context.put("showFullLogs", false);
		}
	}

	@Override
	public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection) {
		super.validate(params, errorCollection);
		if ("true".equals(params.getString(CHANGE_AUTHENTICATION_TOKEN))) {
			String password = params.getString(PLAIN_AUTHENTICATION_TOKEN);
			if (StringUtils.isEmpty(password)) {
				errorCollection.addError(PLAIN_AUTHENTICATION_TOKEN, "You must specify password");
			}
		}
	}

	public void setEncryptionService(EncryptionService encryptionService) {
		this.encryptionService = encryptionService;
	}
}
