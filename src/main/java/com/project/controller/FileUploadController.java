package com.project.controller;

import com.project.response.ResponseResult;
import com.project.service.iService.IStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/api/v1/getFile")
@CrossOrigin("http://localhost:3000")
public class FileUploadController {
    //this controller receive file/image from client

    @Autowired
    private IStorageService storageService;

    @PostMapping("")
    public ResponseEntity<ResponseResult> uploadFile(@RequestParam("file")MultipartFile file){
        try {
            //save files to a folder => use a service

            String generatedFileName = storageService.storageFile(file);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok","upload file successfully", generatedFileName,1)
            );

        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
                    new ResponseResult("Ok", ex.getMessage(),"",1)
            );
        }
    }

    //get image url
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<byte[]> readDetailFile(@PathVariable String fileName){
        try {
            byte[] bytes = storageService.readFileContent(fileName);
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        }catch (Exception exception) {
            return ResponseEntity.noContent().build();
        }
    }

    //How to load all uploaded files ?
    @GetMapping("")
    public ResponseEntity<ResponseResult> getUploadedFiles() {
        try {
            List<String> urls = storageService.loadAll()
                    .map(path -> {
                        //convert fileName to url(send request "readDetailFile")
                        String urlPath = MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "readDetailFile", path.getFileName().toString()).build().toUri().toString();
                        return urlPath;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ResponseResult("ok", "List files successfully", urls,1));
        }catch (Exception exception) {
            return ResponseEntity.ok(new
                    ResponseResult("failed", "List files failed", new String[] {},1));
        }
    }

}
