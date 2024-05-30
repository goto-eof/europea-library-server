package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CursorDTO;
import com.andreidodu.europealibrary.dto.PairDTO;
import com.andreidodu.europealibrary.dto.security.UserDTO;
import com.andreidodu.europealibrary.mapper.UserMapper;
import com.andreidodu.europealibrary.model.security.User;
import com.andreidodu.europealibrary.repository.security.UserRepository;
import com.andreidodu.europealibrary.service.UserManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserManagerServiceImpl extends CursoredServiceCommon implements UserManagerService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public CursorDTO<UserDTO> getAll(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        CursorDTO<UserDTO> cursoredResult = new CursorDTO<>();
        PairDTO<List<User>, Long> userPairDTO = this.userRepository.retrieveAllCursored(commonCursoredRequestDTO);
        cursoredResult.setItems(userPairDTO.getVal1().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList()));
        cursoredResult.setNextCursor(userPairDTO.getVal2());
        return cursoredResult;
    }

}
