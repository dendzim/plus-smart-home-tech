package ru.yandex.practicum;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class DeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryApplication.class, args);
    }
}