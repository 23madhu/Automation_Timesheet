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

    @Value("${timesheet.ddl.projectname}")
    private String ddlProjectname;

    @Value("${timesheet.ddl.milestone}")
    private String ddlMilestone;

    @Value("${timesheet.ddl.taskgroup}")
    private String ddlTaskGroup;

    @Value("${timesheet.ddl.taskname}")
    private String ddlTaskName;

    @Value("${timesheet.txt.taskdesc}")
    private String txtTaskDesc;

    @Value("${timesheet.ddl.taskhours}")
    private String ddlTaskHours;

    @Value("${timesheet.ddl.taskminutes}")
    private String ddlTaskMinutes;

    public String getUsername()        { return username; }
    public String getPassword()        { return password; }
    public String getUrl()             { return url; }
    public String getDdlProjectname()  { return ddlProjectname; }
    public String getDdlMilestone()    { return ddlMilestone; }
    public String getDdlTaskGroup()    { return ddlTaskGroup; }
    public String getDdlTaskName()     { return ddlTaskName; }
    public String getTxtTaskDesc()     { return txtTaskDesc; }
    public String getDdlTaskHours()    { return ddlTaskHours; }
    public String getDdlTaskMinutes()  { return ddlTaskMinutes; }
}