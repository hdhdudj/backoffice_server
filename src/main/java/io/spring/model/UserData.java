package io.spring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
    private String id;
    private String email;
    private String username;
    private String bio;
    private String image;
}
