package com.bezkoder.spring.datajpa.repository;

import com.bezkoder.spring.datajpa.model.User;

import java.util.List;

public interface IUserDAO {
    List<User> searchUser(List<SearchCriteria> params);

    void save(User entity);
}
