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

import java.util.Map;

public class PackageBuildTaskConfigurator extends AbstractTaskConfigurator
{
    private TextProvider textProvider;

    @NotNull
    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params, @Nullable final TaskDefinition previousTaskDefinition)
    {
        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        config.put("serverUrl", params.getString("serverUrl"));
        config.put("authenticationToken", params.getString("authenticationToken"));
        config.put("rapiddeployProjectName", params.getString("rapiddeployProjectName"));
        config.put("packageName", params.getString("packageName"));
        config.put("archiveExtension", params.getString("archiveExtension"));
        return config;
    }

    @Override
    public void populateContextForCreate(@NotNull final Map<String, Object> context)
    {
        super.populateContextForCreate(context);

        context.put("serverUrl", "");
        context.put("authenticationToken", "");
        context.put("rapiddeployProjectName", "");
        context.put("packageName", "");
        context.put("archiveExtension", "jar");
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForEdit(context, taskDefinition);

        context.put("serverUrl", taskDefinition.getConfiguration().get("serverUrl"));
        context.put("authenticationToken", taskDefinition.getConfiguration().get("authenticationToken"));
        context.put("rapiddeployProjectName", taskDefinition.getConfiguration().get("rapiddeployProjectName"));
        context.put("packageName", taskDefinition.getConfiguration().get("packageName"));
        context.put("archiveExtension", taskDefinition.getConfiguration().get("archiveExtension"));
    }

    @Override
    public void populateContextForView(@NotNull final Map<String, Object> context, @NotNull final TaskDefinition taskDefinition)
    {
        super.populateContextForView(context, taskDefinition);
        context.put("serverUrl", taskDefinition.getConfiguration().get("serverUrl"));
        context.put("authenticationToken", taskDefinition.getConfiguration().get("authenticationToken"));
        context.put("rapiddeployProjectName", taskDefinition.getConfiguration().get("rapiddeployProjectName"));
        context.put("packageName", taskDefinition.getConfiguration().get("packageName"));
        context.put("archiveExtension", taskDefinition.getConfiguration().get("archiveExtension"));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params, @NotNull final ErrorCollection errorCollection)
    {
        super.validate(params, errorCollection);

    }

    public void setTextProvider(final TextProvider textProvider)
    {
        this.textProvider = textProvider;
    }
}
