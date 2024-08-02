package com.wanted.portfolio.file.service;

import com.wanted.portfolio.file.model.File;
import com.wanted.portfolio.file.repository.FileRepository;
import com.wanted.portfolio.file.util.FileManager;
import com.wanted.portfolio.file.util.S3Manager;
import com.wanted.portfolio.global.exception.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class FileService {
    private final S3Manager s3Manager;

    private final FileManager fileManager;

    private final FileRepository fileRepository;

    public File createFile(MultipartFile multipartFile) {
        validateFileExists(multipartFile);
        validateFileFormat(multipartFile);

        String originalName = multipartFile.getOriginalFilename();
        String storeName = fileManager.toStoreName(originalName);

        String url = s3Manager.upload(multipartFile, storeName);

        File file = new File(originalName, storeName, url);

        fileRepository.save(file);
        log.info("새로운 파일이 업로드 되었습니다. {}", file);

        return file;
    }

    @Transactional(readOnly = true)
    public List<File> findAll(List<Long> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        List<File> files = fileRepository.findAllById(ids);

        if (files.size() != ids.size()) {
            throw new BadRequestException("잘못된 파일 id가 포함되어 있습니다.");
        }

        return files;
    }

    private void validateFileExists(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BadRequestException("파일이 올바르게 업로드되지 않았습니다.");
        }
    }

    private void validateFileFormat(MultipartFile multipartFile) {
        if (!fileManager.isImage(multipartFile)) {
            throw new BadRequestException("지원하지 않는 파일 형식입니다.");
        }
    }
}
