package com.david.jcb;

import java.util.Collections;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueryMyCardTask {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(QueryMyCardTask.class);

	@Autowired
	private WebDriverPool webDriverPool;

	@Autowired
	private CardRepository cardRepository;

	private Set<Card> cards;

	// XXX PhantomJSDriver not support multiple thread
	// IllegalStateException:
	// The process has not exited yet therefore no result is available ...
	// @Autowired
	// private ThreadPoolTaskScheduler executor;

	@PostConstruct
	void init() {
		this.cards = Collections.unmodifiableSet(this.cardRepository.findAll());
	}

	// For local verify
	// @Scheduled(cron = "*/1 42-43 17 27 * ?")
	// @Scheduled(fixedRate = 5000)
	public void run() {
		// this.cards.stream().forEach(card -> this.executor.execute(() ->
		// this.goal(card)));
		this.cards.stream().forEach(this::goal);
	}

	private final void goal(final Card card) {
		final WebDriver wd = this.webDriverPool.getWebDriver();
		wd.get("https://ezweb.easycard.com.tw/Event01/JCBLoginRecordServlet");
		wd.switchTo().defaultContent();
		wd.switchTo().frame(0);

		final String captcha = wd.findElement(By.id("hidCaptcha")).getAttribute("value");
		LOG.info("read captcha {}", captcha);
		wd.switchTo().defaultContent();

		wd.findElement(By.id("txtEasyCard1")).sendKeys(card.getTxtEasyCard1());
		wd.findElement(By.id("txtEasyCard2")).sendKeys(card.getTxtEasyCard2());
		wd.findElement(By.id("txtEasyCard3")).sendKeys(card.getTxtEasyCard3());
		wd.findElement(By.id("txtEasyCard4")).sendKeys(card.getTxtEasyCard4());
		wd.findElement(By.id("captcha")).sendKeys(captcha);
		wd.findElement(By.tagName("form")).submit();

		LOG.info("{} Result: {}", card.getTxtEasyCard4()//
				, StringUtils.defaultString(wd.findElement(By.className("step2")).getText(), "Not exist."));

		wd.quit();
	}
}
