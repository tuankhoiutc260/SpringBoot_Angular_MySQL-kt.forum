package com.tuankhoi.backend.service;

public interface RedisService {
    void saveValue(String key, String value);

    Object getValue(String key);
}
