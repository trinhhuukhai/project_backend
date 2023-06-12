package com.project.service;

import com.project.dto.request.PaymentRequest;
import com.project.dto.request.ProductRequest;
import com.project.model.*;
import com.project.repository.OrderItemRepository;
import com.project.repository.OrderRepository;
import com.project.repository.PaymentRepository;
import com.project.repository.UserRepository;
import com.project.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<Payment> getAllPayment(){
        return paymentRepository.findAll();
    }

    public Long countPaymentAmount() {
        return paymentRepository.countByAmount();
    }



    public ResponseEntity<ResponseResult> addPayment(PaymentRequest pay) {
        // Get the order
        Order order = orderRepository.findById(pay.getOrderId()).orElseThrow();
        List<OrderItem> orderItem = order.getOrderItemList();

        // Check if the order has already been paid
        if (order.getPaymentStatus().equalsIgnoreCase("Đã thanh toán")) {
            // If order has already been paid, return an error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseResult("error", "Đơn hàng đã thanh toán", null, 0)
            );
        }
        // Calculate the payment amount
        Double paymentAmount = order.getTotal();

        // Get the user from the order
        User user = order.getUser();
        // Check if the user has enough balance in the wallet
        if (user.getWallet().getBalance() < paymentAmount) {
            // If user doesn't have enough balance, return an error response
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseResult("error", "Không đủ tiền trong ví, vui lòng nạp thêm", null, 0)
            );
        }
        // Create a new payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(paymentAmount);
        // Save the payment
        paymentRepository.save(payment);
        // Update the payment status of the order
        order.setPaymentStatus("Đã thanh toán");
        for (OrderItem item : orderItem) {
            item.setPaymentStatus("Đã thanh toán");
        }
        orderItemRepository.saveAll(orderItem);
        orderRepository.save(order);
        // Deduct the payment amount from the user's wallet
        Double newBalance = user.getWallet().getBalance() - paymentAmount;
        user.getWallet().setBalance(newBalance);
        userRepository.save(user);
        // Return a success response
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Thanh toán thành công", payment, 1)
        );
    }



    public List<Payment> getPaymentsMadeToday() {
        Date startDate = new Date();
        startDate.setHours(0);
        startDate.setMinutes(0);
        startDate.setSeconds(0);

        Date endDate = new Date();
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);

        return paymentRepository.findPaymentToday(startDate, endDate);
    }



    public List<Payment> findPaymentByDate(Date date) {
        return paymentRepository.findPaymentByDate(date);
    }

    public List<Payment> findPaymentByDateRange(Date startDate, Date endDate) {
        return paymentRepository.findOPaymentByDateRange(startDate, endDate);
    }

    public List<Payment> findPaymentForCurrentWeek() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        return paymentRepository.findPaymentForCurrentWeek(startDate, endDate);
    }

    public List<Payment> findPaymentForCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        return paymentRepository.findPaymentForCurrentMonth(year, month);
    }

    public List<Payment> findPaymentFromYesterday() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        ZonedDateTime startOfDayUtc = startOfDay.atZone(ZoneOffset.UTC);
        Date startOfDayDate = Date.from(startOfDayUtc.toInstant());
        return paymentRepository.findPaymentFromYesterday(startOfDayDate);
    }

    public List<Payment> findPaymentForPreviousMonth() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        LocalDate previousMonth = currentDate.minusMonths(1);
        int previousYear = previousMonth.getYear();
        int previousMonthValue = previousMonth.getMonthValue();
        return paymentRepository.findPaymentForPreviousMonth(previousYear, previousMonthValue);
    }

    public List<Payment> findPaymentForLastWeek() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastWeekStartDate = currentDate.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastWeekEndDate = lastWeekStartDate.plusDays(6);
        Date startDate = Date.from(lastWeekStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastWeekEndDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        return paymentRepository.findPaymentForLastWeek(startDate, endDate);
    }


//    public ResponseEntity<ResponseResult> updatePayment(@RequestBody Payment newPay, @PathVariable Long id) {
//        Payment updatedPay = paymentRepository.findById(id)
//                .map(pay -> {
//                    pay.setPaymentMethod(newPay.getPaymentMethod());
//                    return paymentRepository.save(pay);
//                }).orElseGet(() -> {
//                    newPay.setId(id);
//                    return paymentRepository.save(newPay);
//                });
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseResult("ok", "Update shipping successfully", updatedPay,1)
//        );
//    }
//
//    //Delete a Product => DELETE method
//    public ResponseEntity<ResponseResult> deletePay(@PathVariable Long id) {
//        boolean exists = paymentRepository.existsById(id);
//        if(exists) {
//            paymentRepository.deleteById(id);
//            return ResponseEntity.status(HttpStatus.OK).body(
//                    new ResponseResult("ok", "Delete payment successfully", "",1)
//            );
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseResult("failed", "Cannot find payment to delete", "",1)
//        );
//    }
//
//    //Let's return an object with: data, message, status
//    public ResponseEntity<ResponseResult> findById(@PathVariable Long id) {
//        Optional<Payment> foundPay = paymentRepository.findById(id);
//        return foundPay.isPresent() ?
//                ResponseEntity.status(HttpStatus.OK).body(
//                        new ResponseResult("ok", "Query payment successfully", foundPay,1)
//                        //you can replace "ok" with your defined "error code"
//                ):
//                ResponseEntity.status(HttpStatus.OK).body(
//                        new ResponseResult("failed", "Cannot find payment with id = "+id, "",1)
//                );
//    }
}
