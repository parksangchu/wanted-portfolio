package com.wanted.portfolio.file.contoller;

import com.wanted.portfolio.file.dto.FileCreateResponse;
import com.wanted.portfolio.file.model.File;
import com.wanted.portfolio.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<FileCreateResponse> createFile(MultipartFile multipartFile) {
        File file = fileService.createFile(multipartFile);
        FileCreateResponse fileCreateResponse = FileCreateResponse.from(file);

        return ResponseEntity.ok(fileCreateResponse);
    }
}
