package hello.proxy.app.v3;

public class OrderServiceV2 {

    private final OrderRepositoryV3 orderRepository;

    public OrderServiceV2(OrderRepositoryV3 orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void orderItem(String itemId) {
        orderRepository.save(itemId);
    }
}
