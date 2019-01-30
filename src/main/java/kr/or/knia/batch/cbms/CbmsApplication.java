package kr.or.knia.batch.cbms;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class CbmsApplication {

  public static void main(String[] args) {
    SpringApplication.run(CbmsApplication.class, args);
  }

}

