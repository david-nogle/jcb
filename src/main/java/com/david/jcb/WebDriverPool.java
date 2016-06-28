package com.david.jcb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.stereotype.Component;

/**
 * download phantomjs-2.1.1-macosx.zip
 * 
 * @see http://phantomjs.org/download.html
 * @see https://github.com/detro/ghostdriver
 */
@Component
public class WebDriverPool {
	private final static int DEFAULT_CAPACITY = 5;

	private final int capacity;

	private final static int STAT_RUNNING = 1;

	private final static int STAT_CLODED = 2;

	private final AtomicInteger stat = new AtomicInteger(STAT_RUNNING);

	/**
	 * store webDrivers created
	 */
	private final List<WebDriver> webDriverList = Collections.synchronizedList(new ArrayList<WebDriver>());

	/**
	 * store webDrivers available
	 */
	private final BlockingDeque<WebDriver> innerQueue = new LinkedBlockingDeque<WebDriver>();

	public WebDriverPool(final int capacity) {
		this.capacity = capacity;
	}

	public WebDriverPool() {
		this(DEFAULT_CAPACITY);
	}

	/**
	 * XXX out of memory
	 *
	 * @return
	 */
	public WebDriver get(final int nodeIndex) {
		return this.create(nodeIndex);
	}

	// TODO reuse WebDriver
	@Deprecated
	public WebDriver get() {
		this.checkRunning();
		final WebDriver poll = this.innerQueue.poll();
		if (poll != null) {
			return poll;
		}
		if (this.webDriverList.size() < this.capacity) {
			synchronized (this.webDriverList) {
				if (this.webDriverList.size() < this.capacity) {
					final WebDriver e = this.create(0);
					this.innerQueue.add(e);
					this.webDriverList.add(e);
				}
			}

		}

		try {
			return this.innerQueue.take();
		} catch (final InterruptedException e) {
			return null;
		}
	}

	private WebDriver create(final int nodeIndex) {
		final DesiredCapabilities phantomConfig = DesiredCapabilities.phantomjs();
		phantomConfig.setCapability("phantomjs.binary.path",
				"/Users/david/server/phantomjs-2.1.1-macosx_" + nodeIndex + "/bin/phantomjs");
		final WebDriver driver = new PhantomJSDriver(phantomConfig);
		driver.manage().timeouts()//
				.implicitlyWait(10, TimeUnit.SECONDS)//
				.pageLoadTimeout(10, TimeUnit.SECONDS);
		return driver;
	}

	public void returnToPool(final WebDriver webDriver) {
		this.checkRunning();
		this.innerQueue.add(webDriver);
	}

	protected void checkRunning() {
		if (!this.stat.compareAndSet(STAT_RUNNING, STAT_RUNNING)) {
			throw new IllegalStateException("Already closed!");
		}
	}

	public void closeAll() {
		final boolean b = this.stat.compareAndSet(STAT_RUNNING, STAT_CLODED);
		if (!b) {
			throw new IllegalStateException("Already closed!");
		}
		for (final WebDriver webDriver : this.webDriverList) {
			webDriver.close();
		}

	}
}
