package com.timesheet.service;

import com.timesheet.config.AppConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TimesheetService {

    private final AppConfig config;

    public TimesheetService(AppConfig config) {
        this.config = config;
    }

    public String runBot() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            driver.get(config.getUrl());
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // --- Authentication ---
            WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("LoginCon_UserName")));
            username.sendKeys(config.getUsername());

            WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("LoginCon_Password")));
            password.sendKeys(config.getPassword());

            WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("LoginCon_Login_Button")));
            loginBtn.click();
            System.out.println("Login successful!");

            // --- Handle Popups ---
            try {
                wait.until(popup -> driver.getWindowHandles().size() > 1);
                driver.close();
                driver.switchTo().window((String) driver.getWindowHandles().toArray()[0]);
            } catch (TimeoutException e) {
                System.out.println("No popup appeared, continuing...");
            }

            // Wait for calendar loading
            Thread.sleep(5000);

            // --- Event Analysis ---
            Map<String, Integer> eventMap = new HashMap<>();
            List<WebElement> allEvents = driver.findElements(By.className("fc-event-title"));

            for (WebElement event : allEvents) {
                String text = event.getText().trim();
                eventMap.put(text, eventMap.getOrDefault(text, 0) + 1);
            }
            System.out.println("Event Summary: " + eventMap);

            // --- Process NE (New Entry) Events ---
            if (eventMap.getOrDefault("NE", 0) > 0) {
                List<WebElement> neEvents = driver.findElements(By.cssSelector("a.fc-event"));

                for (WebElement event : neEvents) {
                    List<WebElement> spans = event.findElements(By.className("fc-event-title"));

                    if (!spans.isEmpty() && spans.get(0).getText().trim().equals("NE")) {
                        System.out.println("Opening timesheet for NE entry...");
                        event.click();
                        
                        // Wait for the form modal/fields to load
                        Thread.sleep(3000);

                        // 1. Project Name (Using JS to trigger dependent dropdowns)
                        WebElement projField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ddl_Projectname")));
                        js.executeScript("arguments[0].value='" + config.getDdlProjectname() + "'; arguments[0].dispatchEvent(new Event('change'));", projField);
                        Thread.sleep(2000); 

                        // 2. Milestone
                        WebElement milestoneField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ddl_milestone")));
                        js.executeScript("arguments[0].value='" + config.getDdlMilestone() + "'; arguments[0].dispatchEvent(new Event('change'));", milestoneField);
                        Thread.sleep(1500);

                        // 3. Task Group
                        WebElement groupField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ddl_task_group")));
                        js.executeScript("arguments[0].value='" + config.getDdlTaskGroup() + "'; arguments[0].dispatchEvent(new Event('change'));", groupField);
                        Thread.sleep(1500);

                        // 4. Task Name
                        WebElement nameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ddl_task_name")));
                        js.executeScript("arguments[0].value='" + config.getDdlTaskName() + "'; arguments[0].dispatchEvent(new Event('change'));", nameField);
                        Thread.sleep(1000);

                        // 5. Task Description
                        WebElement descField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("txt_Task_Desc")));
                        descField.clear();
                        descField.sendKeys(config.getTxtTaskDesc());

                        // 6. Task Hours & Minutes
                        WebElement hoursField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ddl_Task_Hours")));
                        js.executeScript("arguments[0].value='" + config.getDdlTaskHours() + "'; arguments[0].dispatchEvent(new Event('change'));", hoursField);

                        WebElement minsField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("ddl_Task_minutes")));
                        js.executeScript("arguments[0].value='" + config.getDdlTaskMinutes() + "'; arguments[0].dispatchEvent(new Event('change'));", minsField);

                        System.out.println("Filled timesheet details successfully.");
                        Thread.sleep(2000);
                        
                        // Note: Add logic here to click 'Save' or 'Submit' if required.
                        // Login button
                         //   WebElement savebtn = wait.until(
                           //     ExpectedConditions.elementToBeClickable(By.id("btn_Save"))
                            //);
                            //btn_Save.click();
                    }
                }
            }

            return "Process Complete: " + eventMap.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        } finally {
            // driver.quit(); // Uncomment to close browser automatically
        }
    }
}