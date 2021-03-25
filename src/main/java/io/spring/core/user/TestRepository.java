package io.spring.core.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository {
	void save(Test test);

	Optional<Test> findById(int id);

	List<Test> findTests();

}
