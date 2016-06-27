package com.david.jcb;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

//XXX Function test
public class A {

	public static void main(final String[] args) {
		// System.setProperty("webdriver.chrome.driver",
		// "/Users/david/Downloads/chromedriver");

		final DesiredCapabilities phantomConfig = DesiredCapabilities.phantomjs();
		phantomConfig.setCapability("phantomjs.binary.path",
				"/Users/david/Downloads/phantomjs-2.1.1-macosx/bin/phantomjs");
		final WebDriver wd = new PhantomJSDriver(phantomConfig);
		wd.get("https://ezweb.easycard.com.tw/Event01/JCBLoginRecordServlet");
		wd.switchTo().defaultContent();
		wd.switchTo().frame(0);

		final String captcha = wd.findElement(By.id("hidCaptcha")).getAttribute("value");
		final String hidCaptcha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		System.out.println("read captcha:" + captcha);
		wd.switchTo().defaultContent();

		wd.findElement(By.id("txtEasyCard1")).sendKeys("");
		wd.findElement(By.id("txtEasyCard2")).sendKeys("");
		wd.findElement(By.id("txtEasyCard3")).sendKeys("");
		wd.findElement(By.id("txtEasyCard4")).sendKeys("");
		wd.findElement(By.id("captcha")).sendKeys(captcha);
		// wd.findElement(By.id("method")).sendKeys("queryLoginDate");//XXX
		// wd.findElement(By.id("hidCaptcha")).sendKeys(hidCaptcha);/XXX
		wd.findElement(By.className("search_go")).click();

		System.out.println("Resule:" + wd.findElement(By.className("step2")).getText());
		// System.out.println(wd.getPageSource());

		// wd.quit();
	}

}
