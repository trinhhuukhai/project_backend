package com.project.controller;

import com.project.dto.request.CategoryRequest;
import com.project.model.Category;
import com.project.response.ResponseResult;
import com.project.service.CategoryService;
import com.project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:3000")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/getAllCategory")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    List<Category> getAll(){
        return categoryService.getAllCategory();
    }


    @GetMapping("/{id}")
        //Let's return an object with: data, message, status
    ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
        return categoryService.findById(id);
    }

    @PostMapping("/insert")
//    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<ResponseResult> insertCategory(@RequestBody Category newCat) {
        return categoryService.insertCategory(newCat);

    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseResult> updateCategory(@RequestBody Category newCat, @PathVariable Long id) {
        return categoryService.updateCategory(newCat,id);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseResult> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }

    @GetMapping("/{id}/product")
    ResponseEntity<ResponseResult> findByCategoryId(@PathVariable Long id) {
        return productService.findProductByCategoryId(id);
    }
}
