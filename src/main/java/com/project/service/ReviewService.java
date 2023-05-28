package com.project.service;

import com.project.dto.request.ReviewRequest;
import com.project.model.Product;
import com.project.model.Review;
import com.project.model.User;
import com.project.repository.ProductRepository;
import com.project.repository.ReviewRepository;
import com.project.repository.UserRepository;
import com.project.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getAllReview(){
        return reviewRepository.findAll( Sort.by("createdDate").descending());
    }


    public ResponseEntity<ResponseResult> insertReview(ReviewRequest newReview) {
//

        Product product = productRepository.findById(newReview.getProductId()).orElseThrow();
        User user = userRepository.findById(newReview.getUserId()).orElseThrow();

        Review review = new Review();
        review.setUser(user);
//        review.setRating(newReview.getRating());
        review.setContent(newReview.getContent());
        review.setProduct(product);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Insert review successfully",reviewRepository.save(review),1)
        );

    }

    public ResponseEntity<ResponseResult> updateReview(ReviewRequest request, Long id) {
        Optional<Review> updatedReview = reviewRepository.findById(id)
                .map(review -> {
                    review.setContent(request.getContent());
                    return reviewRepository.save(review);
                });

        if (updatedReview.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Review content updated successfully", updatedReview.get(), 1)
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseResult("error", "Review not found", null, 0)
            );
        }
    }


    //Delete a Product => DELETE method
    public ResponseEntity<ResponseResult> deleteReview(Long id) {
        boolean exists = reviewRepository.existsById(id);
        if(exists) {
            reviewRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Delete review successfully", "",1)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("failed", "Cannot find review to delete", "",1)
        );
    }

    //Let's return an object with: data, message, status
    public ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
        Optional<Review> foundReview = reviewRepository.findById(id);
        return foundReview.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query review successfully", foundReview,1)
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find review with id = "+id, "",1)
                );
    }

    public ResponseEntity<ResponseResult> findByProductId( Long id) {
        List<Review> foundReview = reviewRepository.findByProductId(id);
        return !foundReview.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query review successfully", foundReview,1)
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find review with id = "+id, "",1)
                );
    }

}
