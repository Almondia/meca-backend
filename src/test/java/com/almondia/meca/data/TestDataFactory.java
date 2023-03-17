package com.almondia.meca.data;

import java.util.List;

public interface TestDataFactory<T> {

	List<T> createTestData();
}
