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
[@ww.textfield labelKey="com.midvision.rapiddeploy.bamboo.rapiddeployProjectName" name="rapiddeployProjectName" required='true'/]
[@ww.textfield labelKey="com.midvision.rapiddeploy.bamboo.rapiddeployServerName" name="rapiddeployServerName" required='true'/]
[@ww.textfield labelKey="com.midvision.rapiddeploy.bamboo.rapiddeployEnvironmentName" name="rapiddeployEnvironmentName" required='true'/]
[@ww.checkbox labelKey="com.midvision.rapiddeploy.bamboo.rapiddeployInstanceName.change" toggle=true name="change_instance" /]
[@ui.bambooSection dependsOn="change_instance" showOn=true]
    [@ww.textfield labelKey="com.midvision.rapiddeploy.bamboo.rapiddeployInstanceName" name="instance" required="true" /]
[/@ui.bambooSection]
[@ww.textfield labelKey="com.midvision.rapiddeploy.bamboo.rapiddeployAppName" name="rapiddeployAppName" required='true'/]
[@ww.textfield labelKey="com.midvision.rapiddeploy.bamboo.deploy.package.name" name="packageName" required='false'/]
[@ww.checkbox labelKey="com.midvision.rapiddeploy.bamboo.isAsynchronous" toggle=true name="isAsynchronous"/]
