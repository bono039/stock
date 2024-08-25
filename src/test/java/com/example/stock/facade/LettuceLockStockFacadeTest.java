package com.example.stock.facade;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LettuceLockStockFacadeTest {

    @Autowired
    private LettuceLockStockFacade lettuceLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    // 테스트 실행 전 데이터 생성
    @BeforeEach
    public void before() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    // TC가 종료되면, 모든 아이템 삭제하기
    @AfterEach
    public void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        int threadCnt = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 비동기 실행 작업을 단순화해 사용할 수 있게 하는 API
        CountDownLatch latch = new CountDownLatch(threadCnt);   // 다른 스레드에서 수행 중인 작업이 완료될 때까지 대기하는 걸 돕는 클래스

        for(int i = 0 ; i < threadCnt ; i++) {
            executorService.submit(() -> {
                try {
                    lettuceLockStockFacade.decrease(1L, 1L);
                }catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();
        // 100 - (1*100) = 0
        assertEquals(0, stock.getQuantity());
    }
}