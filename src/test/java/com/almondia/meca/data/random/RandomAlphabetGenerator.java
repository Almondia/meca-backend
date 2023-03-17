package com.almondia.meca.data.random;

import java.util.Random;

public class RandomAlphabetGenerator implements RandomStringGenerator {

	@Override
	public String generate(int length) {
		Random random = new Random();
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < length; ++i) {
			String ALPHABETS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
			stringBuilder.append(ALPHABETS.charAt(random.nextInt(ALPHABETS.length())));
		}
		return stringBuilder.toString();
	}
}
