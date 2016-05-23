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

		CARDS = Collections.unmodifiableSet(tmp);
	}

	public Set<Form> findAll() {
		return CARDS;
	}
}
