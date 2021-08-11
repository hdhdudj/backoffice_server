package io.spring.model;

import lombok.Getter;

@Getter
public class UserWithToken {
	private String id;
    private String email;
    private String username;
    private String bio;
    private String image;
    private String token;
	private String refreshToken;

	public UserWithToken(UserData userData, String token, String refreshToken) {
		this.id = userData.getId();
        this.email = userData.getEmail();
        this.username = userData.getUsername();
        this.bio = userData.getBio();
        this.image = userData.getImage();
        this.token = token;
		this.refreshToken = refreshToken;
    }

}
