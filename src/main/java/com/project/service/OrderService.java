package com.project.service;

import com.project.dto.request.OrderRequest;
import com.project.dto.request.ProductRequest;
import com.project.dto.response.OrderItemResponse;
import com.project.dto.response.OrderResponse;
import com.project.dto.response.ProductResponse;
import com.project.model.*;
import com.project.repository.*;
import com.project.response.ResponseResult;
import com.project.service.iService.ImageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;



    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    public ResponseEntity<ResponseResult> getAllOrder(){
        List<Order> list = orderRepository.findAll( Sort.by("orderDate").descending());
        return !list.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "list Orrder", list,list.size())
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "no list ", "",1)
                );
    }


    public ResponseEntity<ResponseResult> insertOrder(@RequestBody OrderRequest newOrder) {


        User user = userRepository.findById(newOrder.getUser_id()).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setStatus("Đang xử lý");
        order.setPaymentStatus("Chưa thanh toán");
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Đặt hàng thành công",orderRepository.save(order),1)
        );
    }

    public ResponseEntity<ResponseResult> updateOrder(@RequestBody OrderRequest newOrder, @PathVariable Long id) {

        User user = userRepository.findById(newOrder.getUser_id()).orElseThrow();
//        Payment payment = paymentRepository.findById(newOrder.getPaymentId()).orElseThrow();


        Optional<Order> updatedOrder = orderRepository.findById(id)
                .map(order -> {

//                    order.setPayment(payment);
                    order.setUser(user);
                    return orderRepository.save(order);
                });
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Update order successfully", updatedOrder,1)
        );
    }

    public ResponseEntity<ResponseResult> CancelOrder(String status, Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        List<OrderItem> orderItemList = optionalOrder.get().getOrderItemList();

        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseResult("failed", "Order not found", "", 1)
            );
        }

        Order order = optionalOrder.get();
        String currentStatus = order.getStatus();

        if (currentStatus.equals("Hủy đơn hàng")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseResult("failed", "Đơn hàng đã hủy", "", 1)
            );
        }

        for (OrderItem item:
             orderItemList) {
//            item.setPaymentStatus("Hoàn tiền");
            item.setStatus("Huỷ đơn hàng");
        }
        orderItemRepository.saveAll(orderItemList);

        order.setStatus(status);

        orderRepository.save(order);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Change status successfully", order, 1)
        );
    }

//    public ResponseEntity<ResponseResult> RefundPayment(String paymentStatus, Long id) {
//        Optional<Order> optionalOrder = orderRepository.findById(id);
//        List<OrderItem> orderItemList = optionalOrder.get().getOrderItemList();
//
//        if (optionalOrder.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ResponseResult("failed", "Order not found", "", 1)
//            );
//        }
//
//        Order order = optionalOrder.get();
//        String currentStatus = order.getStatus();
//
//        if (currentStatus.equals("Đơn hàng hủy") && order.getPaymentStatus().equals("Đã thanh toán")) {
//            Double orderAmount = order.getBillInvoice();
//            Double walletAmount = order.getUser().getWallet().getBalance();
//            Double totalAmount = orderAmount + walletAmount;
//
//            Payment payment = paymentRepository.findByOrderId(order.getId());
//            payment.setAmount(0.0);
//            paymentRepository.save(payment);
//
//            Wallet wallet = walletRepository.findByUserId(order.getUser().getId());
//            wallet.setBalance(totalAmount);
//            walletRepository.save(wallet);
//            for (OrderItem item:
//                    orderItemList) {
//                item.setPaymentStatus(paymentStatus);
////                item.setStatus("Huỷ đơn hàng");
//            }
//            orderItemRepository.saveAll(orderItemList);
//
////            order.setStatus(paymentStatus);
//            order.setPaymentStatus(paymentStatus);
//            orderRepository.save(order);
//
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ResponseResult("ok", "Change status successfully", order, 1)
//        );
//    }



    //Delete a Product => DELETE method
    public ResponseEntity<ResponseResult> deleteOrder(@PathVariable Long id) {
        boolean exists = orderRepository.existsById(id);
        if(exists) {
            orderRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Delete order successfully", "",1)
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("failed", "Cannot find order to delete", "",1)
        );
    }

    //Let's return an object with: data, message, status
    public ResponseEntity<ResponseResult> findById( Long id) {
        Optional<Order> foundOrder = orderRepository.findById(id);
        return foundOrder.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query order successfully", foundOrder,1)
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find order with id = "+id, "",1)
                );
    }

    public ResponseEntity<ResponseResult> findOrderByUserId( Long id) {
        List<Order> foundOrder = orderRepository.findByUserIdOrderByOrderDateDesc(id);
        return !foundOrder.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query order item successfully", foundOrder, foundOrder.size())
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find order item with id = "+id, "",foundOrder.size())
                );
    }


    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getOrdersByPaymentStatus(String status) {
        return orderRepository.findByPaymentStatus(status);
    }

    public List<Order> findOrderByDate(Date date) {
        return orderRepository.findOrderByDate(date);
    }

    public List<Order> findOrdersByDateRange(Date startDate, Date endDate) {
        return orderRepository.findOrdersByDateRange(startDate, endDate);
    }

    public List<Order> findOrdersForCurrentWeek() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        return orderRepository.findOrdersForCurrentWeek(startDate, endDate);
    }

    public List<Order> findOrdersForCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        return orderRepository.findOrdersForCurrentMonth(year, month);
    }

    public List<Order> findOrdersFromYesterday() {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime yesterday = currentDate.minusDays(1);
        Date yesterdayDate = Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());
        return orderRepository.findOrdersFromYesterday(yesterdayDate);
    }

    public List<Order> findOrdersForPreviousMonth() {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        LocalDate previousMonth = currentDate.minusMonths(1);
        int previousYear = previousMonth.getYear();
        int previousMonthValue = previousMonth.getMonthValue();
        return orderRepository.findOrdersForPreviousMonth(previousYear, previousMonthValue);
    }

    public List<Order> findOrdersForLastWeek() {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastWeekStartDate = currentDate.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastWeekEndDate = lastWeekStartDate.plusDays(6);
        Date startDate = Date.from(lastWeekStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastWeekEndDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        return orderRepository.findOrdersForLastWeek(startDate, endDate);
    }

    public ResponseEntity<ResponseResult> findByIds(Long id) {
        Optional<Order> foundOrder = orderRepository.findById(id);
//        List<OrderResponse> orderResponses = new ArrayList<>();
        if (!foundOrder.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("error", "can't find order with id:" + id, null, 0)
            );
        }
        Order order = foundOrder.get();
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(order.getId());
        orderResponse.setCustomer(order.getUser().getName());
        orderResponse.setPhone(order.getUser().getPhone());
        orderResponse.setAddress(order.getUser().getAddress());
        orderResponse.setEmail(order.getUser().getEmail());
//        orderResponse.setPayment(order.getPayment().getPaymentMethod());
//        orderResponse.setInvest(order.getInvest());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setPaymentStatus(order.getPaymentStatus());
        orderResponse.setOrderDate(order.getOrderDate());
//        orderResponse.setTax(order.getTax());
        orderResponse.setTotal(order.getTotal());
//        orderResponse.setBillInvoice(order.getBillInvoice());

        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

        for (OrderItem orderItem : orderItems) {
            OrderItemResponse orderItemResponse = new OrderItemResponse();
            orderItemResponse.setId(orderItem.getId());
            orderItemResponse.setQuantity(orderItem.getQuantity());
            orderItemResponse.setInvest(orderItem.getInvest());
            orderItemResponse.setTotal(orderItem.getTotal());

            ProductResponse productResponse = new ProductResponse();
            productResponse.setName(orderItem.getProduct().getName());
            productResponse.setBrand(orderItem.getProduct().getBrand());
            productResponse.setColor(orderItem.getProduct().getColor());
            productResponse.setInventory(orderItem.getProduct().getInventory());
            productResponse.setDescription(orderItem.getProduct().getDescription());
            productResponse.setInputPrice(orderItem.getProduct().getInputPrice());
            productResponse.setOutputPrice(orderItem.getProduct().getOutputPrice());
            productResponse.setId(orderItem.getProduct().getId());
            productResponse.setQuantity(orderItem.getQuantity());
//            productResponse.setTotal(orderItem.getProduct().getId());
            productResponse.setProductImage(orderItem.getProduct().getProductImage());

            orderItemResponse.setProductResponses(productResponse);
            orderItemResponses.add(orderItemResponse);
        }

        orderResponse.setOrderItemResponses(orderItemResponses);
//        orderResponses.add(orderResponse);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("success", "found order with id:" + id, orderResponse, 1)
        );
    }


}
