package com.timesheet.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {

    @Value("${timesheet.username}")
    private String username;

    @Value("${timesheet.password}")
    private String password;

    @Value("${timesheet.url}")
    private String url;

    @Value("${timesheet.ddlProjectname}")
    private String ddlProjectname;

    @Value("${timesheet.ddlmilestone}")
    private String ddlMilestone;

    @Value("${timesheet.ddltask_group}")
    private String ddlTaskGroup;

    @Value("${timesheet.ddltask_name}")
    private String ddlTaskName;

    @Value("${timesheet.txtTask_Desc}")
    private String txtTaskDesc;

    @Value("${timesheet.ddlTask_Hours}")
    private String ddlTaskHours;

    @Value("${timesheet.ddlTaskminutes}")
    private String ddlTaskMinutes;

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getUrl() { return url; }
    public String getDdlProjectname() { return ddlProjectname; }
    public String getDdlMilestone() { return ddlMilestone; }
    public String getDdlTaskGroup() { return ddlTaskGroup; }
    public String getDdlTaskName() { return ddlTaskName; }
    public String getTxtTaskDesc() { return txtTaskDesc; }
    public String getDdlTaskHours() { return ddlTaskHours; }
    public String getDdlTaskMinutes() { return ddlTaskMinutes; }
}