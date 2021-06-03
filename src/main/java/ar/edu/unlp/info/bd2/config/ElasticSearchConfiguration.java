package ar.edu.unlp.info.bd2.config;

import ar.edu.unlp.info.bd2.repositories.HibernateBithubRepository;
import ar.edu.unlp.info.bd2.services.BithubService;
import ar.edu.unlp.info.bd2.services.HibernateBithubServiceImpl;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class ElasticSearchConfiguration {

  @Bean
  public BithubService hibernateService() {
    HibernateBithubRepository repository = this.createRepository();
    return new HibernateBithubServiceImpl(repository);
  }

  @Bean
  public HibernateBithubRepository createRepository() {
    return new HibernateBithubRepository();
  }

  @Bean
  public LocalSessionFactoryBean sessionFactory() {
    LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
    sessionFactory.setDataSource(dataSource());
    sessionFactory.setPackagesToScan(new String[] {"ar.edu.unlp.info.bd2.model"});
    sessionFactory.setHibernateProperties(hibernateProperties());

    return sessionFactory;
  }

  @Bean
  public DataSource dataSource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/bd2_grupo" + this.getGroupNumber()+"?useSSL=false");
    dataSource.setUsername("root"); //dataSource.setUsername("grupo13");
    dataSource.setPassword(""); //dataSource.setPassword("SeCuRePaSsWoRd");

    return dataSource;
  }

  @Bean
  public PlatformTransactionManager hibernateTransactionManager() {
    HibernateTransactionManager transactionManager = new HibernateTransactionManager();
    transactionManager.setSessionFactory(sessionFactory().getObject());
    return transactionManager;
  }

  @SuppressWarnings("Duplicates")
  private final Properties hibernateProperties() {
    Properties hibernateProperties = new Properties();

    hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create");
    hibernateProperties.setProperty(
        "hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
    hibernateProperties.setProperty("hibernate.show_sql", "true");
    hibernateProperties.setProperty("hibernate.format_sql", "true");
    hibernateProperties.setProperty("hibernate.use_sql_comments", "false");

    return hibernateProperties;
  }

  private Integer getGroupNumber() {
    return 13;
  }


  @Bean
  public MongoDatabase mongoDatabase() {
    try (Client client = new PreBuiltTransportClient(
            Settings.builder().put("client.transport.sniff", true)
                    .put("cluster.name","elasticsearch").build())
            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300))
    ) {
      return client.getDatabase("bd2");
    }
  }

}
