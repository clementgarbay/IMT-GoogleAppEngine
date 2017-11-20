package com.zenika.zencontact.persistence;

import java.util.List;
import java.util.Optional;

import com.zenika.zencontact.domain.User;

public interface UserDao {
	long save(User contact);
	void delete(Long id);
	Optional<User> get(Long id);
	List<User> getAll();
}
