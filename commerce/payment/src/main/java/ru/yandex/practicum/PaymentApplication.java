package ru.yandex.practicum;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}