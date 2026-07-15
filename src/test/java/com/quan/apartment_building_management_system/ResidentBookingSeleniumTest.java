package com.quan.apartment_building_management_system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import com.quan.apartment_building_management_system.entity.UtilityBooking;
import com.quan.apartment_building_management_system.repository.AccountRepository;
import com.quan.apartment_building_management_system.repository.ProfileRepository;
import com.quan.apartment_building_management_system.repository.UtilityBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ResidentBookingSeleniumTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UtilityBookingRepository bookingRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private WebDriver driver;
    private WebDriverWait wait;

    private void cleanUpTestBookings() {
        try {
            accountRepository.findByUsername("resident@gmail.com").ifPresent(acc -> {
                profileRepository.findByAccountAccountId(acc.getAccountId()).ifPresent(prof -> {
                    List<UtilityBooking> bookings = bookingRepository.findByProfileProfileId(prof.getProfileId());
                    for (UtilityBooking b : bookings) {
                        // Clean up bookings created for 2026-07-16 by our test (09:00 - 11:00)
                        if (b.getStartTime().toLocalDate().isEqual(LocalDate.of(2026, 7, 16)) &&
                            b.getStartTime().getHour() == 9) {
                            bookingRepository.delete(b);
                            System.out.println("Cleaned up test booking ID: " + b.getBookingId());
                        }
                    }
                });
            });
        } catch (Exception e) {
            System.err.println("Clean up failed: " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        cleanUpTestBookings(); // Clean up before running

        ChromeOptions options = new ChromeOptions();
        // Bỏ comment dòng dưới nếu muốn chạy ẩn danh (headless mode) trong CI/CD:
        // options.addArguments("--headless=new"); 
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        cleanUpTestBookings(); // Clean up after running
    }

    private void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void setInputValue(WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].value = arguments[1];" +
            "arguments[0].dispatchEvent(new Event('change'));" +
            "arguments[0].dispatchEvent(new Event('input'));",
            element, value
        );
    }

    private void delay() {
        try {
            Thread.sleep(1500); // Tạm dừng 1.5 giây để nhìn rõ hành động
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testResidentBookingFlow() {
        try {
            // Step 1: Go to homepage
            String baseUrl = "http://localhost:" + port;
            System.out.println("Navigating to base URL: " + baseUrl);
            driver.get(baseUrl);
            delay();

            // Verify redirect to /features
            wait.until(ExpectedConditions.urlContains("/features"));
            System.out.println("Current URL after load: " + driver.getCurrentUrl());
            assertTrue(driver.getCurrentUrl().contains("/features"), "Should redirect to /features page");

            // Step 2: Click the Login button
            System.out.println("Locating Login button...");
            WebElement loginBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.btn-login")));
            System.out.println("Clicking Login button via JS...");
            jsClick(loginBtn);
            delay();

            // Verify on login page
            wait.until(ExpectedConditions.urlContains("/login"));
            System.out.println("Current URL after clicking login: " + driver.getCurrentUrl());
            assertTrue(driver.getCurrentUrl().contains("/login"), "Should be on the login page");

            // Step 3: Login as Resident
            System.out.println("Entering credentials...");
            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            WebElement passwordInput = driver.findElement(By.id("password"));
            WebElement submitBtn = driver.findElement(By.className("btn-submit"));

            emailInput.sendKeys("resident@gmail.com");
            passwordInput.sendKeys("123456");
            delay();
            System.out.println("Submitting login form via JS...");
            jsClick(submitBtn);
            delay();

            // Step 4: Verify Dashboard Redirect
            wait.until(ExpectedConditions.urlContains("/resident/dashboard"));
            System.out.println("Current URL after login: " + driver.getCurrentUrl());
            assertTrue(driver.getCurrentUrl().contains("/resident/dashboard"), "Should redirect to Resident Dashboard");

            // Step 5: Navigate to Amenity Bookings in the Sidebar
            System.out.println("Locating Amenity Bookings sidebar item...");
            WebElement bookingMenuLink = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@href, '/resident/utilities') or .//span[text()='Amenity Bookings']]")
            ));
            System.out.println("Clicking sidebar item via JS...");
            jsClick(bookingMenuLink);
            delay();

            // Verify on utilities list page
            wait.until(ExpectedConditions.urlContains("/resident/utilities"));
            System.out.println("Current URL after navigation: " + driver.getCurrentUrl());
            assertTrue(driver.getCurrentUrl().endsWith("/resident/utilities"), "Should be on Utilities list page");

            // Step 6: Select first available Utility and click "Book Now"
            System.out.println("Locating active utilities...");
            List<WebElement> bookNowButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.cssSelector(".utility-card-body a.btn-book-now")
            ));
            System.out.println("Found " + bookNowButtons.size() + " active utilities");
            assertFalse(bookNowButtons.isEmpty(), "There should be at least one active utility");
            
            System.out.println("Clicking first utility's 'Book Now' via JS...");
            jsClick(bookNowButtons.get(0));
            delay();

            // Verify on physical resources list page
            System.out.println("Waiting for resources list redirection...");
            wait.until(ExpectedConditions.urlContains("/resources"));
            System.out.println("Current URL after utility selection: " + driver.getCurrentUrl());
            assertTrue(driver.getCurrentUrl().contains("/resources"), "Should be on utility resources list page");

            // Step 7: Select a resource and click "Book Now"
            System.out.println("Locating resources...");
            List<WebElement> resourceBookButtons = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
                    By.xpath("//a[contains(@href, '/resident/utilities/book/') and not(contains(@class, 'disabled'))]")
            ));
            System.out.println("Found " + resourceBookButtons.size() + " bookable resources");
            assertFalse(resourceBookButtons.isEmpty(), "There should be at least one bookable resource");
            
            System.out.println("Clicking first resource's 'Book Now' via JS...");
            jsClick(resourceBookButtons.get(0));
            delay();

            // Verify on booking page
            System.out.println("Waiting for booking page redirection...");
            wait.until(ExpectedConditions.urlContains("/resident/utilities/book/"));
            System.out.println("Current URL: " + driver.getCurrentUrl());
            assertTrue(driver.getCurrentUrl().contains("/book/"), "Should be on resource booking page");

            // Step 8: Fill the booking form (Date: 2026-07-16, Start: 09:00, End: 11:00)
            LocalDate testDate = LocalDate.of(2026, 7, 16);
            String testDateStr = testDate.toString(); // format: yyyy-MM-dd
            System.out.println("Booking for date: " + testDateStr);

            WebElement bookingDateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bookingDate")));
            setInputValue(bookingDateInput, testDateStr);

            WebElement startTimeInput = driver.findElement(By.id("startTime"));
            setInputValue(startTimeInput, "09:00");

            WebElement endTimeInput = driver.findElement(By.id("endTime"));
            setInputValue(endTimeInput, "11:00");
            delay();

            // Click "Save All" to calculate total price
            System.out.println("Clicking 'Save All' button to calculate price...");
            WebElement saveAllBtn = driver.findElement(By.xpath("//button[@formaction='/resident/utilities/calculate']"));
            jsClick(saveAllBtn);

            // Wait for page reload/calculation
            System.out.println("Waiting for page recalculation...");
            wait.until(ExpectedConditions.stalenessOf(bookingDateInput));
            bookingDateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bookingDate")));
            delay();

            // Choose Cash payment method if paymentMethod is visible (not hasMembership)
            try {
                WebElement cashRadio = driver.findElement(By.xpath("//input[@name='paymentMethod' and @value='CASH']"));
                System.out.println("Setting payment method to CASH...");
                if (!cashRadio.isSelected()) {
                    jsClick(cashRadio);
                    delay();
                }
            } catch (Exception e) {
                System.out.println("Payment method radio button not found or hidden: " + e.getMessage());
            }

            // Step 9: Confirm Booking and handle JS confirm dialog
            System.out.println("Clicking 'Confirm Booking' button...");
            WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(By.className("btn-confirm-booking")));
            jsClick(confirmBtn);
            delay();

            // Bỏ phần xử lý alert vì click Confirm Booking sẽ chuyển hướng trực tiếp sang trang lịch sử

            // Step 10: Verify redirection to Booking History & check list
            System.out.println("Waiting for history page redirection...");
            wait.until(ExpectedConditions.urlContains("/resident/utilities/history"));
            System.out.println("Current URL: " + driver.getCurrentUrl());
            assertTrue(driver.getCurrentUrl().contains("/resident/utilities/history"), "Should redirect to Booking History page");
            delay();

            // Verify success message
            WebElement successAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert-success")));
            System.out.println("Alert message: " + successAlert.getText());
            assertTrue(successAlert.getText().contains("Đăng ký sử dụng tiện ích thành công!"), "Success alert should confirm registration");

            // Verify booking in the history table
            List<WebElement> rows = driver.findElements(By.cssSelector("table.history-table tbody tr"));
            System.out.println("Found " + rows.size() + " history entries");
            assertFalse(rows.isEmpty(), "Booking history table should not be empty");

            // The newest booking should be the first row
            WebElement firstRow = rows.get(0);
            String timeText = firstRow.findElement(By.cssSelector("td:nth-child(2)")).getText();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String testDateFormatted = testDate.format(formatter);
            System.out.println("First row booking time: " + timeText + " (Expected: " + testDateFormatted + ")");
            
            assertTrue(timeText.contains(testDateFormatted), "Booking time text '" + timeText + "' should contain expected date '" + testDateFormatted + "'");
            System.out.println("Test PASSED successfully!");
            delay();
        } catch (Exception | AssertionError e) {
            System.err.println("Test FAILED with exception: " + e.getMessage());
            System.err.println("Current URL at failure: " + driver.getCurrentUrl());
            try {
                List<WebElement> errorAlerts = driver.findElements(By.cssSelector(".alert-danger"));
                if (!errorAlerts.isEmpty()) {
                    System.err.println("ERROR ALERT DETECTED: " + errorAlerts.get(0).getText());
                }
            } catch (Exception ex) {
                // Ignore
            }
            try {
                System.err.println("Page Source preview: " + driver.getPageSource().substring(0, Math.min(2000, driver.getPageSource().length())));
            } catch (Exception ex) {
                // Ignore
            }
            throw e;
        }
    }
}
