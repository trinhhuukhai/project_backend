package com.project.controller;


import com.project.dto.request.ReviewRequest;
import com.project.dto.response.AllResponse;
import com.project.model.Review;
import com.project.response.ResponseResult;
import com.project.service.ProductService;
import com.project.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/review")
@CrossOrigin("http://localhost:3000")
public class ReviewController {
    @Autowired
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;


        @GetMapping("/getAllReview")
    private AllResponse<List<Review>> getAll(){
        List<Review> allReview = reviewService.getAllReview();
        return new AllResponse<>(allReview.size(), allReview);
    }


    @GetMapping("/{id}")
        //Let's return an object with: data, message, status
    ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @GetMapping("/product/{id}")
        //Let's return an object with: data, message, status
    ResponseEntity<ResponseResult> findByProductId(@PathVariable Long id) {
        return reviewService.findByProductId(id);
    }

    @PostMapping(value = {"/insert"})
    ResponseEntity<ResponseResult> insertReview( @RequestBody ReviewRequest newReview) {
        return reviewService.insertReview(newReview);

    }

    @PutMapping("/{id}")
    ResponseEntity<ResponseResult> updateReview(@RequestBody ReviewRequest request, @PathVariable Long id) {
        return reviewService.updateReview(request,id);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ResponseResult> deleteReview(@PathVariable Long id) {
        return reviewService.deleteReview(id);
    }


}
