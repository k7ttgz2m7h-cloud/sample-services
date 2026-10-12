package com.stepflow.sample.order;
import com.stepflow.sample.order.dto.OrderRequests.*; import com.stepflow.sample.order.repository.OrderRepository; import com.stepflow.sample.order.service.OrderService; import org.junit.jupiter.api.Test; import org.springframework.beans.factory.annotation.Autowired; import org.springframework.boot.test.context.SpringBootTest; import java.math.BigDecimal; import java.util.*; import static org.assertj.core.api.Assertions.*;
@SpringBootTest
class OrderTransactionRollbackTest {
 @Autowired OrderService service; @Autowired OrderRepository orders;
 @Test void rollsBackOrderWhenAuditPersistenceFails(){long before=orders.count();CreateOrderRequest request=new CreateOrderRequest(UUID.randomUUID(),"USD",List.of(new CreateOrderItemRequest(null,"SKU","Product",1,BigDecimal.TEN,BigDecimal.ZERO,BigDecimal.ZERO)),new CreateOrderAddressRequest(null,"Street",null,"City","State","12345","US"),null,BigDecimal.ZERO);String changedBy="x".repeat(200);assertThatThrownBy(()->service.create(request,"rollback-key-"+UUID.randomUUID(),changedBy)).isInstanceOf(RuntimeException.class);assertThat(orders.count()).isEqualTo(before);}
}
