package com.david.jcb;

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
	/**
	 * XXX out of memory
	 * 
	 * @return
	 */
	public WebDriver getWebDriver(final int nodeIndex) {
		final DesiredCapabilities phantomConfig = DesiredCapabilities.phantomjs();
		phantomConfig.setCapability("phantomjs.binary.path",
				"/Users/david/server/phantomjs-2.1.1-macosx_" + nodeIndex + "/bin/phantomjs");
		return new PhantomJSDriver(phantomConfig);
	}
}
