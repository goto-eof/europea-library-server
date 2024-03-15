package com.andreidodu.europealibrary.service.impl;

import com.andreidodu.europealibrary.repository.FileMetaInfoRepository;
import com.andreidodu.europealibrary.service.FileMetaInfoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@RequiredArgsConstructor
public class FileMetaInfoServiceImpl implements FileMetaInfoService {
    private final FileMetaInfoRepository repository;


}
