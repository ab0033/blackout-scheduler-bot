package com.blackout.scheduler.blackoutscheduler.parser;

import com.blackout.scheduler.blackoutscheduler.factory.Driver;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;
import java.io.IOException;

@Data
@AllArgsConstructor
public class WebPageParser {

    private Driver driver;

    public void parseWebPage(String streetName, String houseNumber, String fileName) {
        WebDriver webDriver = driver.createWebDriver();
        makeScheduleScreenShot(chooseHouseNumber(chooseStreet(chooseCity(webDriver), streetName), houseNumber), fileName);
        webDriver.quit();
    }

    public WebDriver chooseCity(WebDriver webDriver) {
        webDriver.get("https://yasno.com.ua/schedule-turn-off-electricity");
        WebElement element = webDriver.findElement(By.xpath("//*[@id=\"app\"]/div/section/div/div/div/a[1]"));
        element.click();
        return webDriver;
    }

    public WebDriver chooseStreet(WebDriver webDriver, String streetName) {
        WebElement address = webDriver.findElement(By.xpath("//*[@id=\"vs2__combobox\"]/div[1]/input"));
        address.sendKeys(streetName);
        address.sendKeys(Keys.ENTER);
        return webDriver;
    }

    public WebDriver chooseHouseNumber(WebDriver webDriver, String houseNumber) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        WebElement houseNumberWebElement = webDriver.findElement(By.xpath("//*[@id=\"vs3__combobox\"]/div[1]/input"));
        houseNumberWebElement.sendKeys(houseNumber);
        houseNumberWebElement.sendKeys(Keys.ENTER);
        return webDriver;
    }

    public void makeScheduleScreenShot(WebDriver webDriver, String fileName) {
        TakesScreenshot scrShot = ((TakesScreenshot) webDriver);
        File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
        File destFile = new File("/Users/ab/Desktop/screens", fileName);
        try {
            FileUtils.copyFile(srcFile, destFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
