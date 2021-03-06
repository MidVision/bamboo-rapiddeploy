[@ww.textfield labelKey="com.midvision.rapiddeploy.bamboo.server.url" name="serverUrl" required='true'/]
[#if context.get("authenticationToken")?has_content]
    [@ww.checkbox labelKey="com.midvision.rapiddeploy.bamboo.authentication.token.change" toggle=true name="change_authentication_token" /]
    [@ui.bambooSection dependsOn="change_authentication_token" showOn=true]
        [@ww.password labelKey="com.midvision.rapiddeploy.bamboo.authentication.token" name="authenticationToken" required="true" /]
    [/@ui.bambooSection]
[#else]
    [@ww.hidden name="change_authentication_token" value=true /]
    [@ww.password labelKey="com.midvision.rapiddeploy.bamboo.authentication.token" name="authenticationToken" required="true" /]
[/#if]
[@ww.textfield labelKey="com.midvision.rapiddeploy.bamboo.jobPlanId" name="jobPlanId" required='true'/]
[@ww.checkbox labelKey="com.midvision.rapiddeploy.bamboo.isAsynchronous" toggle=true name="isAsynchronous"/]
[@ww.checkbox labelKey="com.midvision.rapiddeploy.bamboo.showFullLogs" toggle=true name="showFullLogs"/]
