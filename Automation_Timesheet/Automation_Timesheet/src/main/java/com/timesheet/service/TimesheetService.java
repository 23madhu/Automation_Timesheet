package com.timesheet.service;

import com.timesheet.config.AppConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class TimesheetService {

    private final AppConfig config;

    public TimesheetService(AppConfig config) {
        this.config = config;
    }

    // ================= DROPDOWN =================
    private void selectDropdown(WebDriver driver, WebDriverWait wait,
                                String fieldId, String value) {

        WebElement dropdown = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id(fieldId))
        );

        Select select = new Select(dropdown);

        wait.until(d -> select.getOptions().size() > 1);

        for (WebElement option : select.getOptions()) {
            if (option.getText().trim().equalsIgnoreCase(value.trim())) {
                option.click();
                break;
            }
        }

        wait.until(ExpectedConditions.stalenessOf(dropdown));
    }

    // ================= IFRAME =================
    private void switchToCorrectFrame(WebDriver driver) {

        driver.switchTo().defaultContent();

        List<WebElement> iframes = driver.findElements(By.tagName("iframe"));

        for (int i = 0; i < iframes.size(); i++) {

            driver.switchTo().defaultContent();
            driver.switchTo().frame(i);

            if (driver.findElements(By.id("ddl_Projectname")).size() > 0) {
                return;
            }
        }

        throw new RuntimeException("Frame not found");
    }

    // ================= MAIN =================
    public String runBot() {

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.manage().window().maximize();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            driver.get(config.getUrl());

            // ===== LOGIN ONLY ONCE =====
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("LoginCon_UserName")))
                    .sendKeys(config.getUsername());

            driver.findElement(By.id("LoginCon_Password"))
                    .sendKeys(config.getPassword());

            driver.findElement(By.id("LoginCon_Login_Button")).click();

            System.out.println("Login successful");

            // ===== HANDLE POPUP =====
            try {
                wait.until(d -> driver.getWindowHandles().size() > 1);
                driver.close();
                driver.switchTo().window(driver.getWindowHandles().iterator().next());
            } catch (TimeoutException e) {}

            // ===== PROCESS ALL NE =====
            processAllNE(driver, wait);

            return "All NE entries processed successfully";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();

        } finally {
            // driver.quit(); // enable later
        }
    }

    // ================= PROCESS ALL NE =================
    private void processAllNE(WebDriver driver, WebDriverWait wait) {

        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("fc-event")));

        int totalEvents = driver.findElements(By.cssSelector("a.fc-event")).size();

        System.out.println("Total events: " + totalEvents);

        for (int i = 0; i < totalEvents; i++) {

            // Re-fetch events every loop (VERY IMPORTANT)
            List<WebElement> events = driver.findElements(By.cssSelector("a.fc-event"));

            WebElement event = events.get(i);

            List<WebElement> titles = event.findElements(By.className("fc-event-title"));

            if (!titles.isEmpty() && titles.get(0).getText().trim().equals("NE")) {

                System.out.println("Processing NE index: " + i);

                event.click();

                // ===== ALERT (locked date) =====
                try {
                    wait.until(ExpectedConditions.alertIsPresent());
                    Alert alert = driver.switchTo().alert();
                    alert.accept();
                    continue;
                } catch (TimeoutException e) {
                    // no alert
                }

                WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(15));

                longWait.until(d ->
                        d.findElements(By.id("ddl_Projectname")).size() > 0 ||
                        d.findElements(By.tagName("iframe")).size() > 0
                );

                switchToCorrectFrame(driver);

                // ===== FILL FORM =====
                selectDropdown(driver, longWait, "ddl_Projectname", config.getDdlProjectname());
                selectDropdown(driver, longWait, "ddl_milestone", config.getDdlMilestone());
                selectDropdown(driver, longWait, "ddl_task_group", config.getDdlTaskGroup());
                selectDropdown(driver, longWait, "ddl_task_name", config.getDdlTaskName());

                WebElement desc = longWait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.id("txt_Task_Desc"))
                );
                desc.clear();
                desc.sendKeys(config.getTxtTaskDesc());

                selectDropdown(driver, longWait, "ddl_Task_Hours", config.getDdlTaskHours());
                selectDropdown(driver, longWait, "ddl_Task_minutes", config.getDdlTaskMinutes());

                // ===== SAVE =====
                longWait.until(ExpectedConditions.elementToBeClickable(By.id("btn_Save"))).click();

                try {
                    Alert alert = longWait.until(ExpectedConditions.alertIsPresent());
                    alert.accept();
                } catch (TimeoutException e) {}

                // ===== CLOSE POPUP =====
                driver.switchTo().defaultContent();

                longWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("displayLookupFrame1"));

                longWait.until(ExpectedConditions.elementToBeClickable(By.id("btn_Cancel"))).click();

                driver.switchTo().defaultContent();

                // ===== WAIT FOR PAGE RESET =====
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("fc-event")));

                System.out.println("Completed NE index: " + i);
            }
        }
    }

}