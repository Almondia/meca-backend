package com.almondia.meca.data.random;

import java.util.Random;

public class RandomHangulGenerator implements RandomStringGenerator {

	@Override
	public String generate(int length) {
		Random random = new Random();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			char hangul = (char)(random.nextInt(('힣' - '가') + 1) + '가');
			builder.append(hangul);
		}
		return builder.toString();
	}
}
