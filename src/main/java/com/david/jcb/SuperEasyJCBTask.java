package com.david.jcb;

import java.util.Collections;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SuperEasyJCBTask {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SuperEasyJCBTask.class);

	@Autowired
	private WebDriverPool webDriverPool;

	@Autowired
	private CardRepository cardRepository;

	private Set<Card> cards;

	@PostConstruct
	void init() {
		this.cards = Collections.unmodifiableSet(this.cardRepository.findAll());
		LOG.info("====================Ready====================");
	}

	// @Scheduled(cron = "*/1 0-6 9 1 * ?")
	@Scheduled(fixedRate = 5000) // XXX HFT
	public void run() {
		this.cards.stream().forEach(this::goal);
	}

	private final void goal(final Card card) {
		final WebDriver wd = this.webDriverPool.getWebDriver();
		wd.get("https://ezweb.easycard.com.tw/Event01/JCBLoginServlet");
		wd.switchTo().defaultContent();
		wd.switchTo().frame(0);

		final String captcha = wd.findElement(By.id("hidCaptcha")).getAttribute("value");
		// final String hidCaptcha =
		// LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd
		// HH:mm:ss.SSS"));
		LOG.info("read captcha {}", captcha);
		wd.switchTo().defaultContent();

		wd.findElement(By.id("txtCreditCard1")).sendKeys(card.getTxtCreditCard1());
		wd.findElement(By.id("txtCreditCard2")).sendKeys(card.getTxtCreditCard2());
		// wd.findElement(By.id("txtCreditCard3")).sendKeys("");//XXX confirm
		wd.findElement(By.id("txtCreditCard4")).sendKeys(card.getTxtCreditCard4());

		wd.findElement(By.id("txtEasyCard1")).sendKeys(card.getTxtEasyCard1());
		wd.findElement(By.id("txtEasyCard2")).sendKeys(card.getTxtEasyCard2());
		wd.findElement(By.id("txtEasyCard3")).sendKeys(card.getTxtEasyCard3());
		wd.findElement(By.id("txtEasyCard4")).sendKeys(card.getTxtEasyCard4());

		wd.findElement(By.id("captcha")).sendKeys(captcha);
		// wd.findElement(By.id("method")).sendKeys("loginAccept");//XXX confirm
		// wd.findElement(By.id("hidCaptcha")).sendKeys(hidCaptcha);/XXX confirm
		// wd.findElement(By.id("CP")).sendKeys(captcha);//XXX confirm
		// wd.findElement(By.id("accept")).sendKeys("");// XXX confirm

		wd.findElement(By.tagName("form")).submit();// XXX confirm

		wd.quit();
	}
}
