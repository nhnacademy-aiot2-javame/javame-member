    package com.nhnacademy.common.config;

    import com.querydsl.jpa.impl.JPAQueryFactory;
    import jakarta.persistence.EntityManager;
    import lombok.RequiredArgsConstructor;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;

    @Configuration
    @RequiredArgsConstructor
    public class QueryDslConfig {

        @Bean
        public JPAQueryFactory jpaQueryFactory(EntityManager entityManager) {
            return new JPAQueryFactory(entityManager);
        }

    }
