package com.david.jcb;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class QueryMyCardTask {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(QueryMyCardTask.class);

	@Autowired
	private CardRepository cardRepository;

	@Autowired
	private WebDriverPool webDriverPool;

	// XXX PhantomJSDriver not support multiple thread
	// IllegalStateException:
	// The process has not exited yet therefore no result is available ...
	// Multiple node can reduce the Exception chance
	@Autowired
	private ThreadPoolTaskScheduler executor;

	private List<Card> cards;

	@PostConstruct
	void init() {
		this.cards = Collections.unmodifiableList(this.cardRepository.findAll());
	}

	// For local verify
	// @Scheduled(cron = "*/1 27-28 11 28 * ?")
	// @Scheduled(fixedRate = 5000)
	public void run() {
		IntStream.range(0, this.cards.size()).forEach(index -> this.executor.execute(() -> this.goal(index)));
	}

	private final void goal(final int cardIndex) {
		final Card card = this.cards.get(cardIndex);
		final WebDriver wd = this.webDriverPool.getWebDriver(cardIndex);
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
				, StringUtils.defaultIfBlank(wd.findElement(By.className("step2")).getText(), "Not exist."));

		wd.quit();
	}
}
