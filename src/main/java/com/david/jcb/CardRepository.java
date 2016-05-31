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
		tmp.add(Form.form()//
				.add("txtCreditCard1", "3567")//
				.add("txtCreditCard2", "3000")//
				.add("txtCreditCard3", "")// XXX Optional
				.add("txtCreditCard4", "7107")//
				//
				.add("txtEasyCard1", "8300")//
				.add("txtEasyCard1", "9620")//
				.add("txtEasyCard1", "0079")//
				.add("txtEasyCard1", "7390")//
				//
				.add("captcha", "")//
				.add("method", "loginAccept")//
				.add("hidCaptcha", "")//
		);
		CARDS = Collections.unmodifiableSet(tmp);
	}

	public Set<Form> findAll() {
		return CARDS;
	}
}
