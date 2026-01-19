package com.example.service;

import com.example.domain.Photo;
import com.example.domain.PhotoExample;
import com.example.repository.PhotoRepository;
import io.github.simple.dynamodb.processor.KeyPair;
import io.github.simple.jdbc.processor.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class PhotoService extends BaseService<Photo, KeyPair, PhotoExample> {

    private final PhotoRepository photoRepository;
    
}
