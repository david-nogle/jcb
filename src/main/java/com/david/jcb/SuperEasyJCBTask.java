package com.david.jcb;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class SuperEasyJCBTask {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SuperEasyJCBTask.class);

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private WebDriverPool webDriverPool;

	@Autowired
	private ThreadPoolTaskScheduler executor;

	private List<Card> cards;

	@PostConstruct
	void init() {
		this.cards = Collections.unmodifiableList(this.cardRepository.findAll());
		LOG.info("====================Ready====================");
	}

	@Scheduled(cron = "*/1 0-6 9 1 * ?")
	// @Scheduled(fixedRate = 5000) // XXX HF
	public void run() {
		// IntStream.range(0, this.cards.size()).forEach(this::goal);
		IntStream.range(0, this.cards.size()).forEach(index -> this.executor.execute(() -> this.goal(index)));
	}

	private final void goal(final int cardIndex) {
		final Card card = this.cards.get(cardIndex);
		final WebDriver driver = this.webDriverPool.get(cardIndex);
		driver.get("https://ezweb.easycard.com.tw/Event01/JCBLoginServlet");
		driver.switchTo().defaultContent();

		if (!this.isExist(driver, By.tagName("iframe"))) {
			LOG.info("iframe is not exist");
			return;
		}
		try {
			driver.switchTo().frame(0);// XXX confirm
		} catch (final NoSuchFrameException e) {
			LOG.error(e.getMessage(), e);
			return;
		}
		if (!this.isExist(driver, By.id("hidCaptcha"))) {
			LOG.info("hidCaptcha is not exist");
			return;
		}
		final String captcha = driver.findElement(By.id("hidCaptcha")).getAttribute("value");
		// final String hidCaptcha =
		// LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd
		// HH:mm:ss.SSS"));
		LOG.info("read captcha {}", captcha);
		driver.switchTo().defaultContent();

		driver.findElement(By.id("txtCreditCard1")).sendKeys(card.getTxtCreditCard1());
		driver.findElement(By.id("txtCreditCard2")).sendKeys(card.getTxtCreditCard2());
		// driver.findElement(By.id("txtCreditCard3")).sendKeys("");//XXX
		// confirm
		driver.findElement(By.id("txtCreditCard4")).sendKeys(card.getTxtCreditCard4());

		driver.findElement(By.id("txtEasyCard1")).sendKeys(card.getTxtEasyCard1());
		driver.findElement(By.id("txtEasyCard2")).sendKeys(card.getTxtEasyCard2());
		driver.findElement(By.id("txtEasyCard3")).sendKeys(card.getTxtEasyCard3());
		driver.findElement(By.id("txtEasyCard4")).sendKeys(card.getTxtEasyCard4());

		driver.findElement(By.id("captcha")).sendKeys(captcha);

		// XXX confirm these elements
		// driver.findElement(By.id("method")).sendKeys("loginAccept");
		// driver.findElement(By.id("hidCaptcha")).sendKeys(hidCaptcha);
		// driver.findElement(By.id("CP")).sendKeys(captcha);
		// driver.findElement(By.id("accept")).sendKeys("");
		//
		// set hidden value
		// this.setHiddenValue(driver, "method", "loginAccept");
		// this.setHiddenValue(driver, "hidCaptcha", hidCaptcha);
		// this.setHiddenValue(driver, "CP", captcha);
		// this.setHiddenValue(driver, "accept", "");

		driver.findElement(By.tagName("form")).submit();// XXX confirm

		driver.quit();
	}

	private boolean isExist(final WebDriver driver, final By by) {
		return !driver.findElements(by).isEmpty();
	}

	// Plan B
	@SuppressWarnings("unused")
	private void setHiddenValue(final WebDriver driver, final String id, final String value) {
		final JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("document.getElementById('" + id + "').value = '" + value + "'");
	}
}
