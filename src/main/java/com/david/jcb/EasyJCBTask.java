package com.david.jcb;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.jsoup.Jsoup;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EasyJCBTask {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EasyJCBTask.class);

	private static final Set<Form> CARDS;
	static {
		final Set<Form> tmp = new HashSet<>();
		// XXX input card info here
		tmp.add(Form.form()//
				.add("txtCreditCard1", "3566")//
				.add("txtCreditCard2", "1866")//
				.add("txtCreditCard3", "")// XXX Optional
				.add("txtCreditCard4", "0272")//
				//
				.add("txtEasyCard1", "8280")//
				.add("txtEasyCard1", "8500")//
				.add("txtEasyCard1", "3243")//
				.add("txtEasyCard1", "8048")//
				//
				.add("captcha", "")//
				.add("method", "loginAccept")//
				.add("hidCaptcha", "")//
		);
		tmp.add(Form.form()//
				.add("txtCreditCard1", "3566")//
				.add("txtCreditCard2", "1821")//
				.add("txtCreditCard3", "")// XXX Optional
				.add("txtCreditCard4", "0276")//
				//
				.add("txtEasyCard1", "8280")//
				.add("txtEasyCard1", "8500")//
				.add("txtEasyCard1", "3243")//
				.add("txtEasyCard1", "8055")//
				//
				.add("captcha", "")//
				.add("method", "loginAccept")//
				.add("hidCaptcha", "")//
		);
		tmp.add(Form.form()//
				.add("txtCreditCard1", "3567")//
				.add("txtCreditCard2", "3003")//
				.add("txtCreditCard3", "")// XXX Optional
				.add("txtCreditCard4", "4103")//
				//
				.add("txtEasyCard1", "8300")//
				.add("txtEasyCard1", "9620")//
				.add("txtEasyCard1", "0328")//
				.add("txtEasyCard1", "3448")//
				//
				.add("captcha", "")//
				.add("method", "loginAccept")//
				.add("hidCaptcha", "")//
		);
		CARDS = Collections.unmodifiableSet(tmp);
	}

	private final static PoolingHttpClientConnectionManager CONNMGR;

	static {
		LayeredConnectionSocketFactory ssl = null;
		try {
			ssl = new SSLConnectionSocketFactory(//
					SSLContexts.custom()//
							.loadTrustMaterial(new TrustSelfSignedStrategy() {
								@Override
								public boolean isTrusted(final X509Certificate[] chain, final String authType)
										throws CertificateException {
									return true;
								}
							}).build());
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			LOG.error(e.getMessage(), e);
		}

		CONNMGR = new PoolingHttpClientConnectionManager(//
				RegistryBuilder.<ConnectionSocketFactory> create()
						.register("http", PlainConnectionSocketFactory.getSocketFactory())//
						.register("https", ssl)//
						.build());
		CONNMGR.setDefaultMaxPerRoute(100);
		CONNMGR.setMaxTotal(1000);
		CONNMGR.setValidateAfterInactivity(1000);
	}

	private final AtomicInteger counter = new AtomicInteger(0);

	private Async async;

	@PostConstruct
	void init() {
		this.async = Async.newInstance();
		this.async.use(Executor.newInstance(//
				HttpClients.custom()//
						.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)//
						.setConnectionManager(CONNMGR)//
						.build()));
		LOG.info("====================Ready====================");
	}

	@Scheduled(cron = "*/1 0-6 9 1 * ?")
	// @Scheduled(fixedRate = 500) // XXX For local test
	public void run() {
		CARDS.forEach(this::goal);
	}

	private void goal(Form form) {
		this.async.execute(//
				Request.Post("https://ezweb.easycard.com.tw/Event01/JCBLoginServlet")//
						.bodyForm(form.build())//
				, new FutureCallback<Content>() {
					@Override
					public void failed(Exception ex) {
						LOG.error("failed: {}", ex.getMessage());
					}

					@Override
					public void completed(Content result) {
						final String responseMessage = Jsoup.parse(result.asString()).select("#content").first().text();
						if (StringUtils.contains(responseMessage, "登錄名額已滿")) {
							LOG.info("====================GG====================");
							// done();
						} else if (StringUtils.contains(responseMessage, "恭喜")) {
							// TODO check is complete or not
							LOG.info("====================WIN======================");
							if (counter.incrementAndGet() == CARDS.size()) {
								done();
							}
						}
						LOG.info("response : {}", responseMessage);
					}

					@Override
					public void cancelled() {
						LOG.error("cancelled");
					}
				});

		LOG.debug("Execute at %tc%n", new java.util.Date());
	}

	private void done() {
		System.exit(0);
	}
}
