package com.david.jcb;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Set;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EasyJCBTask {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EasyJCBTask.class);

	@Autowired
	private CardRepository cardRepository;

	private Set<Form> cards;

	private Async async;

	@PostConstruct
	void init() {
		this.async = Async.newInstance();
		{
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

			final PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(//
					RegistryBuilder.<ConnectionSocketFactory> create()
							.register("http", PlainConnectionSocketFactory.getSocketFactory())//
							.register("https", ssl)//
							.build());
			connManager.setDefaultMaxPerRoute(100);
			connManager.setMaxTotal(1000);
			connManager.setValidateAfterInactivity(1000);

			this.async.use(Executor.newInstance(//
					HttpClients.custom()//
							.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)//
							.setConnectionManager(connManager)//
							.build()));
		}

		this.cards = Collections.unmodifiableSet(this.cardRepository.findAll());
		LOG.info("====================Ready====================");
	}

	@Scheduled(cron = "*/1 0-6 9 1 * ?")
	public void run() {
		this.cards.forEach(this::goal);
	}

	private void goal(final Form form) {
		this.async.execute(//
				Request.Post("https://ezweb.easycard.com.tw/Event01/JCBLoginServlet")//
						.bodyForm(form.build())//
				, new FutureCallback<Content>() {
					@Override
					public void failed(final Exception ex) {
						LOG.error("failed: {}", ex.getMessage());
					}

					@Override
					public void completed(final Content result) {
						final String responseMessage = Jsoup.parse(result.asString()).select("#content").first().text();
						if (StringUtils.contains(responseMessage, "登錄名額已滿")) {
							// TODO check is full or not
							LOG.info("====================GG====================");
							LOG.info("full.");
						} else if (StringUtils.contains(responseMessage, "恭喜")) {
							// TODO check which card is completed
							LOG.info("====================WIN======================");
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
}
