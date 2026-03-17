package com.jalvaviel.config;

/**
 * A generic storage interface that mirrors the structure used by Sodium.
 * This ensures consistency and compatibility between different config screen builders.
 * * @param <T> The configuration class type being stored.
 */
public interface OptionStorage<T> {
    T getData();
    void save();
}