package com.example.stock.facade;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Service;

@Service
public class LettuceLockStockFacade {

    private final RedisLockRepository redisLockRepository;    // Redis 사용해 Lock 설정용
    private final StockService stockService;  // 재고 감소용

    // 생성자
    public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
        this.redisLockRepository = redisLockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (!redisLockRepository.lock(id)) { // Lock 획득 시도
            Thread.sleep(100);
        }

        try {
            // Lock 획득 성공 시
            stockService.decrease(id, quantity);
        } finally {
            // 로직 모두 종료 시, unlock 메소드 활용해 Lock 해제
            redisLockRepository.unlock(id);
        }
    }
}
