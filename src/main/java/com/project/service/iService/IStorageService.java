package com.project.service.iService;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface IStorageService {

    public String storageFile(MultipartFile file);

    public Stream<Path> loadAll(); // load all file inside a folder

    public byte[] readFileContent(String fileName); // tra ve mang cac byte => xem anh, server tra ve mang cac byte

    public void deleteFiles();


}
