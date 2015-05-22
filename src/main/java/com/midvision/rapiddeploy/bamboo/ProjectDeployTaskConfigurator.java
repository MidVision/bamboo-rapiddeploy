package com.midvision.rapiddeploy.bamboo;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.opensymphony.xwork.TextProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.atlassian.bamboo.security.EncryptionService;

import java.util.Map;

public class ProjectDeployTaskConfigurator extends AbstractTaskConfigurator
{
    private TextProvider textProvider;
    private EncryptionService encryptionService;
    public static final String CHANGE_INSTANCE = "change_instance";
    public static final String PLAIN_AUTHENTICATION_TOKEN = "authenticationToken";
    public static final String AUTHENTICATION_TOKEN = "encAuthenticationToken";
    public static final String CHANGE_AUTHENTICATION_TOKEN = "change_authentication_token";

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
      final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
      config.put("serverUrl", params.getString("serverUrl"));
      String passwordChange = params.getString(CHANGE_AUTHENTICATION_TOKEN);
              if ("true".equals(passwordChange))
              {
                  final String password = params.getString(PLAIN_AUTHENTICATION_TOKEN);
                  config.put(AUTHENTICATION_TOKEN, encryptionService.encrypt(password));
              }
              else if (previousTaskDefinition != null)
              {
                  config.put(AUTHENTICATION_TOKEN, previousTaskDefinition.getConfiguration().get(AUTHENTICATION_TOKEN));
              }
              else
              {
                  final String password = params.getString(PLAIN_AUTHENTICATION_TOKEN);
                  config.put(AUTHENTICATION_TOKEN, encryptionService.encrypt(password));
              }
      config.put("rapiddeployProjectName", params.getString("rapiddeployProjectName"));
      config.put("rapiddeployServerName", params.getString("rapiddeployServerName"));
      config.put("rapiddeployEnvironmentName", params.getString("rapiddeployEnvironmentName"));
      config.put("rapiddeployAppName", params.getString("rapiddeployAppName"));
      config.put("packageName", params.getString("packageName"));
      if(params.getBoolean("isAsynchronous")){
        config.put("isAsynchronous", "true");
      }else{
        config.put("isAsynchronous", "false");
      }

      String instanceChange = params.getString(CHANGE_INSTANCE);
      if ("true".equals(instanceChange))
      {
        final String password = params.getString("instance");
        config.put("instance", password);
        config.put(CHANGE_INSTANCE, "true");
      }else{
        config.put(CHANGE_INSTANCE, "false");
        config.put("instance", "");
      }

      return config;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);

        context.put("serverUrl", "");
        context.put("rapiddeployProjectName", "");
        context.put("rapiddeployServerName", "");
        context.put("rapiddeployEnvironmentName", "");
        context.put("instance", "");
        context.put("rapiddeployAppName", "");
        context.put("packageName", "");
        context.put("isAsynchronous", true);
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);

        context.put("serverUrl", taskDefinition.getConfiguration().get("serverUrl"));

        context.put(PLAIN_AUTHENTICATION_TOKEN, taskDefinition.getConfiguration().get(AUTHENTICATION_TOKEN));
        context.put("rapiddeployProjectName", taskDefinition.getConfiguration().get("rapiddeployProjectName"));
        context.put("rapiddeployServerName", taskDefinition.getConfiguration().get("rapiddeployServerName"));
        context.put("rapiddeployEnvironmentName", taskDefinition.getConfiguration().get("rapiddeployEnvironmentName"));
        context.put("rapiddeployAppName", taskDefinition.getConfiguration().get("rapiddeployAppName"));
        context.put("packageName", taskDefinition.getConfiguration().get("packageName"));
        if(taskDefinition.getConfiguration().get("isAsynchronous").equals("true")){
          context.put("isAsynchronous", true);
        }else{
          context.put("isAsynchronous", false);
        }
        if(taskDefinition.getConfiguration().get(CHANGE_INSTANCE).equals("true")){
          context.put(CHANGE_INSTANCE, true);
        }else{
          context.put(CHANGE_INSTANCE, false);
        }
        context.put("instance", taskDefinition.getConfiguration().get("instance"));
    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put("serverUrl", taskDefinition.getConfiguration().get("serverUrl"));
        context.put("rapiddeployProjectName", taskDefinition.getConfiguration().get("rapiddeployProjectName"));
        context.put("rapiddeployServerName", taskDefinition.getConfiguration().get("rapiddeployServerName"));
        context.put("rapiddeployEnvironmentName", taskDefinition.getConfiguration().get("rapiddeployEnvironmentName"));
        context.put("rapiddeployAppName", taskDefinition.getConfiguration().get("rapiddeployAppName"));
        context.put("packageName", taskDefinition.getConfiguration().get("packageName"));
        if(taskDefinition.getConfiguration().get("isAsynchronous").equals("true")){
          context.put("isAsynchronous", true);
        }else{
          context.put("isAsynchronous", false);
        }
        if(taskDefinition.getConfiguration().get(CHANGE_INSTANCE).equals("true")){
          context.put(CHANGE_INSTANCE, true);
        }else{
          context.put(CHANGE_INSTANCE, false);
        }
        context.put("instance", taskDefinition.getConfiguration().get("instance"));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);
        if ("true".equals(params.getString(CHANGE_AUTHENTICATION_TOKEN)))
                        {
                            String password = params.getString(PLAIN_AUTHENTICATION_TOKEN);
                            if (StringUtils.isEmpty(password))
                            {
                                errorCollection.addError(PLAIN_AUTHENTICATION_TOKEN, "You must specify password");
                            }
                        }
    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }

    public void setEncryptionService(EncryptionService encryptionService)
    {
        this.encryptionService = encryptionService;
    }
}
