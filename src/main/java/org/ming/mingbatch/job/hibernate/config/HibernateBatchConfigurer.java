package org.ming.mingbatch.job.hibernate.config;

import org.hibernate.SessionFactory;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * 스프링 배치는 기본으로 사용하는 TransactionManager 로 DataSourceTransactionManager 를 제공한다.
 * 그러나 하이버네이트를 사용한다면, 일반적인 DataSource 커넥션과 하이버네이트 세션을 아우르는 아우르는 TransactionManager 가 필요하다.
 * 스프링은 바로 이런 목적으로 사용할 수 있는 HibernateTransactionManager 를 제공한다.
 *
 * BatchConfigurer 의 커스텀 구현체를 사용해서 HibernateBatchConfigurer 를 구성한다.
 * DefaultBatchConfigurer.getTransactionManager() 메서드를 오버라이드하기만 하면 된다!
 */
@Component
public class HibernateBatchConfigurer extends DefaultBatchConfigurer {

    private DataSource dataSource;
    private SessionFactory sessionFactory;
    private PlatformTransactionManager transactionManager;

    public HibernateBatchConfigurer(DataSource dataSource, EntityManagerFactory entityManagerFactory) {
        super(dataSource);
        this.dataSource = dataSource;
        this.sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        this.transactionManager = new HibernateTransactionManager(this.sessionFactory);
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }
}
