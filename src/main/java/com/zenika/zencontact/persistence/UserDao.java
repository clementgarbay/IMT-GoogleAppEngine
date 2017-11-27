package com.zenika.zencontact.persistence;

import com.zenika.zencontact.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
	long save(User contact);
	void delete(Long id);
	Optional<User> get(Long id);
	List<User> getAll();
}
