package com.eventsphere.dto;

import com.eventsphere.entity.UserEntity;

public record UserSummary(Long id, String name, String email, String phone, String role, boolean enabled) {
    public static UserSummary from(UserEntity user) {
        return new UserSummary(user.getId(), user.getName(), user.getEmail(), user.getPhone(), user.getRole().name(), user.isEnabled());
    }
}
