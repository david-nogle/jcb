package com.david.jcb;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.client.fluent.Form;
import org.springframework.stereotype.Repository;

@Repository
public class CardRepository {
	private static final Set<Form> CARDS;
	static {
		final Set<Form> tmp = new HashSet<>();
		// XXX input card info here
		tmp.add(buildCard("", "", "", "", "", "", ""));
		tmp.add(buildCard("", "", "", "", "", "", ""));

		CARDS = Collections.unmodifiableSet(tmp);
	}

	private static Form buildCard(final String txtCreditCard1, final String txtCreditCard2, final String txtCreditCard4//
			, final String txtEasyCard1, final String txtEasyCard2, final String txtEasyCard3,
			final String txtEasyCard4) {
		return Form.form()//
				.add("txtCreditCard1", txtCreditCard1)//
				.add("txtCreditCard2", txtCreditCard2)//
				.add("txtCreditCard3", "")// XXX Optional
				.add("txtCreditCard4", txtCreditCard4)//
				//
				.add("txtEasyCard1", txtEasyCard1)//
				.add("txtEasyCard2", txtEasyCard2)//
				.add("txtEasyCard3", txtEasyCard3)//
				.add("txtEasyCard4", txtEasyCard4)//
				//
				.add("captcha", "")//
				.add("method", "loginAccept")//
				.add("hidCaptcha", "");
	}

	public Set<Form> findAll() {
		return CARDS;
	}
}
