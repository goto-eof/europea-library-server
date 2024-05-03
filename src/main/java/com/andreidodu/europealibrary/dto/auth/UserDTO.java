package com.andreidodu.europealibrary.dto.auth;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private List<AuthorityDTO> authorityList;
    private LocalDateTime recoveryRequestTimestamp;
}
