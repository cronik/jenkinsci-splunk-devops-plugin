package com.splunk.splunkjenkins.listeners;

import com.splunk.splunkjenkins.model.JenkinsJsonConfig;
import com.splunk.splunkjenkins.utils.SplunkLogService;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;

import static com.splunk.splunkjenkins.Constants.JENKINS_CONFIG_PREFIX;
import static com.splunk.splunkjenkins.listeners.LoggingConfigListener.xstream;
import static com.splunk.splunkjenkins.model.EventType.JENKINS_CONFIG;
import static com.splunk.splunkjenkins.utils.LogEventHelper.getUserName;
import static com.splunk.splunkjenkins.utils.LogEventHelper.logUserAction;

@Extension
public class LoggingItemListener extends ItemListener {
    @Override
    public void onCreated(Item item) {
        logUserAction(getUserName(), "created " + item.getUrl());
    }

    @Override
    public void onCopied(Item src, Item item) {
        logUserAction(getUserName(), "copied " + item.getUrl());
    }

    @Override
    public void onDeleted(Item item) {
        logUserAction(getUserName(), "deleted " + item.getUrl());
    }

    @Override
    public void onRenamed(Item item, String oldName, String newName) {
        //no-op, we use onLocationChanged
    }

    @Override
    public void onUpdated(Item item) {
        //prior to delete, makeDisabled was called and onUpdated is triggered
        logUserAction(getUserName(), "updated " + item.getUrl());
        String sourceName = JENKINS_CONFIG_PREFIX + item.getUrl();
        SplunkLogService.getInstance().send(new JenkinsJsonConfig(xstream.toXML(item)), JENKINS_CONFIG, sourceName);
    }

    @Override
    public void onLocationChanged(Item item, String oldFullName, String newFullName) {
        logUserAction(getUserName(), "renamed " + item.getUrl() + " from " + oldFullName + "to " + newFullName);
    }
}
