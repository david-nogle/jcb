package com.david.jcb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

@Repository
public class CardRepository {
	private static final Set<Card> CARDS;
	static {
		final Set<Card> tmp = new HashSet<>();
		tmp.add(new Card()//
				.txtCreditCard1("").txtCreditCard2("").txtCreditCard4("")//
				.txtEasyCard1("").txtEasyCard2("").txtEasyCard3("").txtEasyCard4("")//
		);
		tmp.add(new Card()//
				.txtCreditCard1("").txtCreditCard2("").txtCreditCard4("")//
				.txtEasyCard1("").txtEasyCard2("").txtEasyCard3("").txtEasyCard4("")//
		);
		CARDS = Collections.unmodifiableSet(tmp);
	}

	// accept:
	// txtCreditCard1:1234
	// txtCreditCard2:1234
	// txtCreditCard4:1234
	// txtEasyCard1:1234
	// txtEasyCard2:1234
	// txtEasyCard3:1234
	// txtEasyCard4:1234
	// captcha:6768
	// method:loginAccept
	// hidCaptcha:2016-06-01 09:13:35.921
	// CP:6768

	public List<Card> findAll() {
		return new ArrayList<>(CARDS);
	}
}
