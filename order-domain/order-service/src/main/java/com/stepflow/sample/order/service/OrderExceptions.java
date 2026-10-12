package com.stepflow.sample.order.service;
public final class OrderExceptions {
 private OrderExceptions(){}
 public static class OrderNotFoundException extends RuntimeException{public OrderNotFoundException(Object id){super("Order not found: "+id);}}
 public static class OrderItemNotFoundException extends RuntimeException{public OrderItemNotFoundException(Object id){super("Order item not found: "+id);}}
 public static class InvalidOrderStatusTransitionException extends RuntimeException{public InvalidOrderStatusTransitionException(String message){super(message);}}
 public static class IdempotencyConflictException extends RuntimeException{public IdempotencyConflictException(){super("Idempotency key was already used with a different request");}}
 public static class OrderModificationNotAllowedException extends RuntimeException{public OrderModificationNotAllowedException(String status){super("Order cannot be modified in status: "+status);}}
}
