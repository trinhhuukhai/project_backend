package com.project.service;

import com.project.dto.request.ProductRequest;
import com.project.model.*;
import com.project.repository.CategoryRepository;
import com.project.repository.ProductRepository;
import com.project.repository.ShopRepository;
import com.project.repository.UserRepository;
import com.project.response.ResponseResult;
import com.project.service.iService.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ImageStorageService storageService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ShopRepository shopRepository;


    public List<Product> getAllProduct(){
        return productRepository.findAll( Sort.by("createdDate").descending());
    }


    public ResponseEntity<ResponseResult> insertProduct(ProductRequest newPro) {
//

        Category category = categoryRepository.findById(newPro.getCategoryId()).orElseThrow();
        Shop shop = shopRepository.findById(newPro.getShopId()).orElseThrow();

        Product product = new Product();
        product.setName(newPro.getName());
        product.setDescription(newPro.getDescription());
        product.setBrand(newPro.getBrand());
        product.setColor(newPro.getColor());
        product.setInputPrice(newPro.getIn_price());
        product.setOutputPrice(newPro.getOut_price());
        product.setInventory(newPro.getInventory());
        product.setCategory(category);
        product.setShop(shop);


        String productImage = "http://localhost:8080/api/v1/getFile/"+ storageService.storageFile(newPro.getProductImage());


        product.setProductImage(productImage);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Insert Product successfully",productRepository.save(product),1)
        );

    }

    public ResponseEntity<ResponseResult> updateProduct( ProductRequest newPro, Long id) {

        Category category = categoryRepository.findById(newPro.getCategoryId()).orElseThrow();
        Shop shop = shopRepository.findById(newPro.getShopId()).orElseThrow();


        String productImage = "http://localhost:8080/api/v1/getFile/"+ storageService.storageFile(newPro.getProductImage());
        Optional<Product> updatedPro = productRepository.findById(id)
                .map(pro -> {
                    pro.setName(newPro.getName());
                    pro.setDescription(newPro.getDescription());
                    pro.setBrand(newPro.getBrand());
                    pro.setColor(newPro.getColor());
                    pro.setInputPrice(newPro.getIn_price());
                    pro.setOutputPrice(newPro.getOut_price());
                    pro.setInventory(newPro.getInventory());
                    pro.setSold(0);
                    pro.setCategory(category);
                    pro.setProductImage(productImage);
                    pro.setShop(shop);
                    return productRepository.save(pro);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Update Customer successfully", updatedPro,1)
        );
    }

    //Delete a Product => DELETE method
    public ResponseEntity<ResponseResult> deleteProduct(@PathVariable Long id) {
        boolean exists = productRepository.existsById(id);
        if(exists) {
            productRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Delete Customer successfully", "",1)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("failed", "Cannot find Customer to delete", "",1)
        );
    }

    //Let's return an object with: data, message, status
    public ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
        Optional<Product> foundProduct = productRepository.findById(id);
        return foundProduct.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query Customer successfully", foundProduct,1)
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find Customer with id = "+id, "",1)
                );
    }

    public Page<Product> getProductPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("createdDate").descending());
        return productRepository.findAll(pageable);
    }

    public Page<Product> getProductPaginationAndSort(Integer pageNumber, Integer pageSize , String field) {
        Pageable pageable = null;
        if (null != field){
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC,field);

        }else {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.ASC,"name");

        }
        return productRepository.findAll(pageable);
    }

    public ResponseEntity<ResponseResult> findProductByCategoryId(@PathVariable Long id) {
        List<Product> foundProduct = productRepository.findByCategoryId(id);
        return !foundProduct.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query product item successfully", foundProduct, foundProduct.size())
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find product item with id = "+id, "",foundProduct.size())
                );
    }

    public ResponseEntity<ResponseResult> findProductByShopId(@PathVariable Long id) {
        List<Product> foundProduct = productRepository.findByShopId(id);
        return !foundProduct.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query product item successfully", foundProduct, foundProduct.size())
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find product item with id = "+id, "",foundProduct.size())
                );
    }

    public List<Product> getProductsBySoldCount() {
        return productRepository.findBySoldGreaterThanOrEqual(50);
    }

    public List<Product> getProductsByNameStartingWith(String name) {
        return productRepository.findByNameStartingWith(name,  Sort.by("createdDate").descending());
    }

}
