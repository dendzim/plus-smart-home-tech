package ru.yandex.practicum;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}