package com.example.stock.facade;

import com.example.stock.domain.Stock;
import com.example.stock.repository.LockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Service;

@Service
public class NamedLockStockFacade {

    private LockRepository lockRepository;  // Lock 획득용

    private final StockService stockService;    // 재고 감소용

    public NamedLockStockFacade(LockRepository lockRepository, StockService stockService) {
        this.lockRepository = lockRepository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());  // lock 획득하기
            stockService.decrease(id, quantity);    // 재고 감소하기
        } finally {
            // 모든 로직 종료 시 Lock 해제시키기
            lockRepository.releaseLock(id.toString());
        }

    }
}
