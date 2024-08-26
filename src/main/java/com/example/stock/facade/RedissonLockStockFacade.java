package com.example.stock.facade;

import com.example.stock.service.StockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockStockFacade {

    private RedissonClient redissonClient;  // Lock 획득용 (Redisson 라이브러리 덕에 Repository 따로 안 만들어도 됨)
    private StockService stockService;  // 재고 감소용

    public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    // 재고 감소 로직
    public void decrease(Long id, Long quantity) {
        RLock lock = redissonClient.getLock(id.toString());   // Lock 객체 가져오기

        try {
            boolean available = lock.tryLock(15, 1, TimeUnit.SECONDS);

            // 몇 초 동안 lock 획득하고 점유할 것인지 설정하기
            if(!available) {
                System.out.println("lock 획득 실패");
                return;
            }

            // 정상적으로 Lock 획득한 경우, 재고 감소시키기
            stockService.decrease(id, quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 로직 모두 정상적으로 종료되면, Lock 해제하기
            lock.unlock();
        }
    }
}
