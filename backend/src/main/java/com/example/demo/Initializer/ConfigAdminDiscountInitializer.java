package com.example.demo.Initializer;

import com.example.demo.Entity.ConfigAdminDiscountEntity;
import com.example.demo.Repository.ConfigAdminDiscountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConfigAdminDiscountInitializer implements CommandLineRunner {
    @Value("${RUN_INIT_DATA:false}")
    private boolean runInitData;

    private final ConfigAdminDiscountRepository repository;

    public ConfigAdminDiscountInitializer(
            ConfigAdminDiscountRepository repository
    ) {

        this.repository = repository;

    }

    @Override
    public void run(String... args) {

        if (!runInitData) {

            return;

        }

        if(!repository.existsByDiscountType(
                ConfigAdminDiscountEntity.DiscountType.GROUP_DISCOUNT
        )){

            ConfigAdminDiscountEntity groupDiscount =
                    ConfigAdminDiscountEntity.builder()

                            .discountType(
                                    ConfigAdminDiscountEntity.DiscountType.GROUP_DISCOUNT
                            )

                            .percentage(0)

                            .minPassengers(2)

                            .active(false)

                            .build();

            repository.save(groupDiscount);
            System.out.println(
                    ">>> GROUP_DISCOUNT creado."
            );

        }

        if(!repository.existsByDiscountType(
                ConfigAdminDiscountEntity.DiscountType.FREQUENT_CUSTOMER
        )){

            ConfigAdminDiscountEntity frequentCustomer =
                    ConfigAdminDiscountEntity.builder()

                            .discountType(
                                    ConfigAdminDiscountEntity.DiscountType.FREQUENT_CUSTOMER
                            )

                            .percentage(0)

                            .minReservations(1)

                            .active(false)

                            .build();

            repository.save(frequentCustomer);
            System.out.println(
                    ">>> FREQUENT_CUSTOMER creado."
            );

        }

        if(!repository.existsByDiscountType(
                ConfigAdminDiscountEntity.DiscountType.MULTI_PACKAGE
        )){

            ConfigAdminDiscountEntity multiPackage =
                    ConfigAdminDiscountEntity.builder()

                            .discountType(
                                    ConfigAdminDiscountEntity.DiscountType.MULTI_PACKAGE
                            )

                            .percentage(0)

                            .periodDays(30)

                            .active(false)

                            .build();

            repository.save(multiPackage);
            System.out.println(
                    ">>> MULTI_PACKAGE creado."
            );

        }
    }
}
