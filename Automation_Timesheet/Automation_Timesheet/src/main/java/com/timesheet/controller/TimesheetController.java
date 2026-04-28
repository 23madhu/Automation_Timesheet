package com.timesheet.controller;

import com.timesheet.service.TimesheetService;
import org.springframework.web.bind.annotation.*;

@RestController
public class TimesheetController {

    private final TimesheetService timesheetService;

    public TimesheetController(TimesheetService timesheetService) {
        this.timesheetService = timesheetService;
    }

    @GetMapping("/submit")
    public String submitTimesheet() {
        timesheetService.runBot();
        return "Timesheet Submitted!";
    }
}