package com.project.service;

import com.project.dto.request.*;
import com.project.model.*;
import com.project.repository.*;
import com.project.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private WalletRepository walletRepository;


    @Autowired
    PaymentRepository paymentRepository;


    public List<OrderItem> getAllOrderItem(){
        return orderItemRepository.findAll();
    }


    public ResponseEntity<ResponseResult> insertOrderItem(OrderItemRequest newOrderItem) {


        Order order = orderRepository.findById(newOrderItem.getOrderId()).orElseThrow();
        Product product = productRepository.findById(newOrderItem.getProductId()).orElseThrow();

        OrderItem orderItem = new OrderItem();

        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(newOrderItem.getQuantity());

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Insert Order successfully",orderItemRepository.save(orderItem),1)
        );
    }

    public ResponseEntity<ResponseResult> updateOrderItem(OrderItemRequest newStatus, Long id) {
        Optional<OrderItem> updatedOrderItem = orderItemRepository.findById(id)
                .map(orderItem -> {
                    orderItem.setStatus(newStatus.getStatus());
                    return orderItemRepository.save(orderItem);
                });

        // Update the status of the order if necessary
        if (updatedOrderItem.isPresent()) {
            Order order = updatedOrderItem.get().getOrder();
            order.updateStatus();
            orderRepository.save(order);
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Update order successfully", updatedOrderItem, 1)
        );
    }


    //Delete a Product => DELETE method
    public ResponseEntity<ResponseResult> deleteOrderItem(Long id) {
        boolean exists = orderItemRepository.existsById(id);
        if(exists) {
            orderItemRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Delete order item successfully", "",1)
            );
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseResult("failed", "Cannot find order item to delete", "",1)
        );
    }

    //Let's return an object with: data, message, status
    public ResponseEntity<ResponseResult> findById( Long id) {
        Optional<OrderItem> foundOrderItem = orderItemRepository.findById(id);
        return foundOrderItem.isPresent() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query order item successfully", foundOrderItem,1)
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseResult("failed", "Cannot find order item with id = "+id, "",1)
                );
    }

    public ResponseEntity<ResponseResult> findOrderItemsByOrderId(Long id) {
        List<OrderItem> foundOrderItem = orderItemRepository.findByOrderId(id);
        return !foundOrderItem.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query order item successfully", foundOrderItem, foundOrderItem.size())
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseResult("failed", "Cannot find order item with id = "+id, "",foundOrderItem.size())
                );
    }

    public Long countTotal() {
        return orderItemRepository.countByTotal();
    }



    public ResponseEntity<ResponseResult> findOrderItemsByShopId(Long id) {
        List<OrderItem> foundOrderItem = orderItemRepository.findByShopIdOrderByOrderDateDesc(id);
        return !foundOrderItem.isEmpty() ?
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("ok", "Query order item in shop successfully", foundOrderItem, foundOrderItem.size())
                        //you can replace "ok" with your defined "error code"
                ):
                ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseResult("failed", "Cannot find order item in shop with id = "+id, "",foundOrderItem.size())
                );
    }

    public ResponseEntity<ResponseResult> findPayment(Long id) {
        List<OrderItem> foundOrderItems = orderItemRepository.findByShopIdAndPaymentStatus(id, "Đã thanh toán");

        if (!foundOrderItems.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("ok", "Query order items in shop successfully", foundOrderItems, foundOrderItems.size())
            );
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseResult("failed", "Cannot find order items in shop with id = " + id, "", 0)
            );
        }
    }


    public Map<Date, Double> getTotalSalesByDay(Long shopId) {
        List<Object[]> results = orderItemRepository.getTotalSalesByDay(shopId,"Đã thanh toán");
        Map<Date, Double> totalSalesByDay = new HashMap<>();

        for (Object[] result : results) {
            Date orderDate = (Date) result[0];
            Double totalSales = (Double) result[1];
            totalSalesByDay.put(orderDate, totalSales);
        }

        return totalSalesByDay;
    }

    public ResponseEntity<ResponseResult> insertOrderItemFromCart(OrderFromCart orderFromCart) {
        User user = userRepository.findById(orderFromCart.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID"));
        List<CartItem> cartItemList = cartItemRepository.findByUser(user);
        if (cartItemList == null || cartItemList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseResult("error", "Cart is empty", null, 0));
        }
        Order order = new Order();
        order.setUser(user);
        order.setStatus("Đang xử lý");
        order.setPaymentStatus("Chưa thanh toán");


        orderRepository.save(order);
        List<OrderItem> orderItemList = new ArrayList<>();
        double total = 0;
        double invest = 0;
//        double taxRate = 0.05;

        for (CartItem item : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setShop(item.getProduct().getShop());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getPrice());
            orderItem.setStatus("Đang xử lý");
            orderItem.setPaymentStatus("Chưa thanh toán");
            orderItem.setSize(item.getSize());
            Product product = item.getProduct();

            //when user order successful then create new customer
            Customer customer = new Customer();
            customer.setCustomerName(item.getUser().getName());
            customer.setPhone(item.getUser().getPhone());
            customer.setEmail(item.getUser().getEmail());
            customer.setAddress(item.getUser().getAddress());
            customer.setShop(item.getProduct().getShop());

            customerRepository.save(customer);
//            int quantity = item.getQuantity();

            double subtotal = product.getOutputPrice() * item.getQuantity();

            double subinvest = product.getInputPrice() * item.getQuantity();

            total += subtotal;
//            invest += subinvest;

            orderItem.setTotal(subtotal);
            orderItem.setInvest(subinvest);

            orderItemList.add(orderItem);

            // Check if the product already exists in the order
            Optional<OrderItem> existingOrderItem = orderItemList.stream()
                    .filter(oi -> oi.getProduct().getId().equals(product.getId()))
                    .findFirst();

            if (existingOrderItem.isPresent()) {
                // If the product already exists, update the quantity and the sold quantity
                OrderItem oi = existingOrderItem.get();
                oi.setQuantity(oi.getQuantity());

                int soldQuantity = product.getSold() + item.getQuantity();
                product.setSold(soldQuantity);
            } else {
                // If the product does not exist, add a new OrderItem and update the sold quantity
                product.setSold(item.getQuantity());
            }

            productRepository.save(product);
        }

        // Calculate the order prices
        double totalOrderPrice = total;
//        double investOrderPrice = invest;
//        double taxOrder = totalOrderPrice * taxRate;
//        double invoiceOrder = totalOrderPrice + taxOrder;

        // Set the total order price and other attributes
        order.setTotal(totalOrderPrice);
//        order.setInvest(investOrderPrice);
//        order.setTax(taxOrder);
//        order.setBillInvoice(invoiceOrder);


        // Save the order items and the OrderFromCart object
        orderItemRepository.saveAll(orderItemList);
        cartItemRepository.deleteAll(cartItemList);
        orderRepository.save(order);


        // Return the response
        return ResponseEntity.ok(new ResponseResult("ok", "Insert Order successfully", order, orderItemList.size()));
    }

    public ResponseEntity<ResponseResult> RefundPayment(String paymentStatus, Long id) {
        Optional<OrderItem> optionalOrder = orderItemRepository.findById(id);

        if (optionalOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseResult("failed", "Order not found", "", 1)
            );
        }

        OrderItem orderItem = optionalOrder.get();
        String currentStatus = orderItem.getStatus();

        if (currentStatus.equals("Huỷ đơn hàng") && orderItem.getPaymentStatus().equals("Đã thanh toán")) {
            Double orderAmount = orderItem.getTotal();
            Double walletAmount = orderItem.getOrder().getUser().getWallet().getBalance();
            Double totalAmount = orderAmount + walletAmount;

            Payment payment = paymentRepository.findByOrderId(orderItem.getOrder().getId());
            payment.setAmount(payment.getAmount() - orderAmount);
            paymentRepository.save(payment);

            Wallet wallet = walletRepository.findByUserId(orderItem.getOrder().getUser().getId());
            wallet.setBalance(totalAmount);
            walletRepository.save(wallet);


            orderItem.setPaymentStatus(paymentStatus);
            orderItemRepository.save(orderItem);

            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get().getOrder();
                order.setPaymentStatus("Đang xử lý");
                order.updateStatusPayment();

                orderRepository.save(order);
            }

        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseResult("ok", "Change status successfully", orderItem, 1)
        );
    }

    public List<OrderItem> getOrderItemToday(Long shopId) {
        Date startDate = new Date();
        startDate.setHours(0);
        startDate.setMinutes(0);
        startDate.setSeconds(0);

        Date endDate = new Date();
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);

        return orderItemRepository.findOrderItemToday(startDate, endDate, shopId);
    }

    public List<OrderItem> findOrderItemByDate(Date date, Long shopId) {
        return orderItemRepository.findOrderItemByDate(date, shopId);
    }

    public List<OrderItem> findOrderItemByDateRange(Date startDate, Date endDate, Long shopId) {
        return orderItemRepository.findOOrderItemByDateRange(startDate, endDate, shopId);
    }

    public List<OrderItem> findOrderItemForCurrentWeek(Long shopId) {
        LocalDate currentDate = LocalDate.now();
        LocalDate startOfWeek = currentDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = currentDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        return orderItemRepository.findOrderItemForCurrentWeek(startDate, endDate, shopId);
    }

    public List<OrderItem> findOrderItemForCurrentMonth(Long shopId) {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        return orderItemRepository.findOrderItemForCurrentMonth(year, month, shopId);
    }

    public List<OrderItem> findOrderItemFromYesterday(Long shopId) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDay = yesterday.atStartOfDay();
        ZonedDateTime startOfDayUtc = startOfDay.atZone(ZoneOffset.UTC);
        Instant startOfDayInstant = startOfDayUtc.toInstant();
        Date startOfDayDate = Date.from(startOfDayInstant);

        return orderItemRepository.findOrderItemFromYesterday(startOfDayDate, shopId);
    }


    public List<OrderItem> findOrderItemForPreviousMonth(Long shopId) {
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        LocalDate previousMonth = currentDate.minusMonths(1);
        int previousYear = previousMonth.getYear();
        int previousMonthValue = previousMonth.getMonthValue();
        return orderItemRepository.findOrderItemForPreviousMonth(previousYear, previousMonthValue, shopId);
    }

    public List<OrderItem> findOrderItemForLastWeek(Long shopId) {
        LocalDate currentDate = LocalDate.now();
        LocalDate lastWeekStartDate = currentDate.minusWeeks(1).with(DayOfWeek.MONDAY);
        LocalDate lastWeekEndDate = lastWeekStartDate.plusDays(6);
        Date startDate = Date.from(lastWeekStartDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(lastWeekEndDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        return orderItemRepository.findOrderItemForLastWeek(startDate, endDate, shopId);
    }

}
