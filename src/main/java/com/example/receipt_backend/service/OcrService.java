package com.example.receipt_backend.service;



import com.example.receipt_backend.entity.Receipt;
import org.springframework.scheduling.annotation.Async;

public interface OcrService {

  @Async("taskExecutor")
  void extractOcrAsync(Receipt receipt);
}
