package com.project.service;

import com.project.dto.request.ImageRequest;
import com.project.model.Product;
import com.project.model.ProductImage;
import com.project.repository.ProductImageRepository;
import com.project.repository.ProductRepository;
import com.project.response.ResponseResult;
import com.project.service.iService.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
public class ProductImageService {
    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ImageStorageService storageService;

    @Autowired
    private ProductRepository productRepository;

    public ResponseEntity<ResponseResult> insertImage(ImageRequest newImage) {
//

        Product product = productRepository.findById(newImage.getProductId()).orElseThrow();

        ProductImage img = new ProductImage();
        img.setProduct(product);

        String productImage = "http://192.168.43.199:8443/api/v1/getFile/"+ storageService.storageFile(newImage.getUrl());

        img.setUrl(productImage);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Insert Product successfully",productImageRepository.save(img),1)
        );

    }

    public List<ProductImage> getAllImageProduct() {
        return productImageRepository.findAll();
    }


    public ResponseEntity<ResponseResult> deleteImage(@PathVariable Long id) {
        boolean exists = productImageRepository.existsById(id);
        if(exists) {
            productImageRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Delete image successfully", "",1)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("failed", "Cannot find image to delete", "",1)
        );
    }
}
