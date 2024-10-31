package tcc.com.mapper;

import tcc.com.controller.request.user.UserRequest;
import tcc.com.controller.response.user.UserResponse;
import tcc.com.domain.user.User;

public class UserMapper {

    public static User toEntity(UserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        return user;
    }

    public static UserResponse toResponse(User user, int percentage) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .color(user.getColor())
                .percentageToNextLevel(percentage)
                .rankingPoint(user.getRankingPoint())
                .avatar("http://localhost:8080/files/" + user.getAvatar())
                .banner("http://localhost:8080/files/" + user.getBanner())
                .initialForm(user.isInitialForm())
                .build();
    }

    public static UserResponse toResponse(User user) {
        int defaultPercentage = 0;
        return toResponse(user, defaultPercentage);
    }
}
