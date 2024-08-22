package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository sr;

    public StockService(StockRepository sr) {
        this.sr = sr;
    }

    // 재고 감소 메소드
    //@Transactional
    public synchronized void decrease(Long id, Long quantity) {
        Stock stock = sr.findById(id).orElseThrow();    // Stock 조회하고
        stock.decrease(quantity);   // 재고 감소시킨 뒤
        sr.saveAndFlush(stock); // 갱신된 값 저장하기
    }

}
