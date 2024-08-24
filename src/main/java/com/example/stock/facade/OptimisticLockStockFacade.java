package com.example.stock.facade;

import com.example.stock.service.OptimisticLockStockService;
import org.springframework.stereotype.Service;

@Service
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    public OptimisticLockStockFacade(OptimisticLockStockService optimisticLockStockService) {
        this.optimisticLockStockService = optimisticLockStockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        // 업데이트 실패 시 재시도해야 하므로 while문
        while(true) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                break;
            } catch(Exception e) {
                // 실패 시, 50ms 이후 재시도
                Thread.sleep(50);
            }
        }


    }
}
