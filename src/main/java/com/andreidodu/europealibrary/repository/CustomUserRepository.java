package com.andreidodu.europealibrary.repository;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.PairDTO;
import com.andreidodu.europealibrary.model.security.User;

import java.util.List;

public interface CustomUserRepository {
    PairDTO<List<User>, Long> retrieveAllCursored(CommonCursoredRequestDTO commonCursoredRequestDTO);
}
