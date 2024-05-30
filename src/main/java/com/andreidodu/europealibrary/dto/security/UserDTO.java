package com.andreidodu.europealibrary.dto.security;

import com.andreidodu.europealibrary.annotation.validation.ShouldBeAlwaysTrue;
import jakarta.validation.constraints.NotNull;
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
    @ShouldBeAlwaysTrue
    private Boolean consensus1Flag;
    @ShouldBeAlwaysTrue
    private Boolean consensus2Flag;
    @NotNull
    private Boolean consensus3Flag;
    private Boolean enabled;
}
