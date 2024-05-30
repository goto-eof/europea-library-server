package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.dto.CommonCursoredRequestDTO;
import com.andreidodu.europealibrary.dto.CommonGenericCursoredResponseDTO;
import com.andreidodu.europealibrary.dto.PairDTO;
import com.andreidodu.europealibrary.dto.security.UserDTO;
import com.andreidodu.europealibrary.exception.ValidationException;
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
    public CommonGenericCursoredResponseDTO<UserDTO> getAll(CommonCursoredRequestDTO commonCursoredRequestDTO) {
        CommonGenericCursoredResponseDTO<UserDTO> cursoredResult = new CommonGenericCursoredResponseDTO<>();
        PairDTO<List<User>, Long> userPairDTO = this.userRepository.retrieveAllCursored(commonCursoredRequestDTO);
        cursoredResult.setChildrenList(userPairDTO.getVal1().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList()));
        cursoredResult.setNextCursor(userPairDTO.getVal2());
        return cursoredResult;
    }

    @Override
    public UserDTO getById(Long id) {
        User user = checkUserExistence(id);
        return this.userMapper.toDTO(user);
    }

    private User checkUserExistence(Long id) {
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ValidationException("Entity not found"));
    }

    @Override
    public UserDTO enable(Long id, boolean isEnabled) {
        User user = checkUserExistence(id);
        user.setEnabled(isEnabled);
        user = this.userRepository.save(user);
        return this.userMapper.toDTO(user);
    }

}
