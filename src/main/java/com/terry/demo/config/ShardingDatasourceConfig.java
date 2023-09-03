package com.terry.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.terry.demo.config.prop.DataSourceProps;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.AlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.mode.ModeConfiguration;
import org.apache.shardingsphere.infra.config.props.ConfigurationPropertyKey;
import org.apache.shardingsphere.infra.config.rule.RuleConfiguration;
import org.apache.shardingsphere.mode.repository.standalone.StandalonePersistRepositoryConfiguration;
import org.apache.shardingsphere.sharding.api.config.ShardingRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.rule.ShardingTableRuleConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.keygen.KeyGenerateStrategyConfiguration;
import org.apache.shardingsphere.sharding.api.config.strategy.sharding.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.single.api.config.SingleRuleConfiguration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

@Configuration
@MapperScan(value = "com.terry.demo.mapper")
public class ShardingDatasourceConfig {

    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    private Resource[] getResources(String location) {
        try {
            return resourceResolver.getResources(location);
        } catch (IOException e) {
            return new Resource[0];
        }
    }

    @Bean("shardingDatasource")
    @ConditionalOnProperty(name = "sharding.load.type", havingValue = "code", matchIfMissing = true)
    public DataSource shardingDatasource(DataSourceProps dsProps) throws SQLException {
        ModeConfiguration modeConfig = createModeConfiguration(); // Build running mode
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        DataSource ds = druidDataSource(dsProps);
        dataSourceMap.put("db0", ds);

        Collection<RuleConfiguration> ruleConfigs = Arrays.asList(createShardingRuleConfiguration(), createSingleRuleConfiguration()); // Build specific rules
        Properties props =  new Properties();
        props.setProperty(ConfigurationPropertyKey.SQL_SHOW.getKey(), Boolean.TRUE.toString());
        return ShardingSphereDataSourceFactory.createDataSource(modeConfig, dataSourceMap, ruleConfigs, props);
    }

    @Bean("shardingDatasource")
    @ConditionalOnProperty(name = "sharding.load.type", havingValue = "yaml")
    public DataSource shardingDataSourceYml() throws IOException, SQLException {
        return YamlShardingSphereDataSourceFactory.createDataSource(getResources("classpath*:sharding.yml")[0].getFile());
    }

    @Bean
    @ConfigurationProperties(prefix = "jdbc")
    public DataSourceProps rdbProperties() {
        return new DataSourceProps();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("shardingDatasource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        String[] mapperLocations = new String[]{"classpath*:/mappers/**/*.xml"};
        Resource[] resources = Stream.of(mapperLocations)
                .flatMap(location -> Stream.of(getResources(location)))
                .toArray(Resource[]::new);
        bean.setMapperLocations(resources);
        return bean.getObject();
    }

    private DataSource druidDataSource(DataSourceProps props) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(props.getUrl());
        dataSource.setUsername(props.getUsername());
        dataSource.setPassword(props.getPassword());
        dataSource.setFilters("stat");
        dataSource.setMaxActive(20);
        dataSource.setInitialSize(5);
        dataSource.setMaxWait(6000);
        dataSource.setMinIdle(1);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(20);
        return dataSource;
    }

    @Primary
    @Bean("jdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("shardingDatasource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    private ModeConfiguration createModeConfiguration() {
        return new ModeConfiguration("Standalone", new StandalonePersistRepositoryConfiguration("JDBC", new Properties()));
    }

    private SingleRuleConfiguration createSingleRuleConfiguration() {
        return new SingleRuleConfiguration(Collections.singleton("*.*"), "db0");
    }

    private ShardingRuleConfiguration createShardingRuleConfiguration() {
        ShardingRuleConfiguration result = new ShardingRuleConfiguration();
        result.getTables().add(listInfoTableRuleConfiguration());

//        result.getBindingTableGroups().add(new ShardingTableReferenceRuleConfiguration("foo", "t_order, t_order_item"));
//        result.setDefaultDatabaseShardingStrategy(new StandardShardingStrategyConfiguration("user_id", "inline"));
//        result.setDefaultTableShardingStrategy(new StandardShardingStrategyConfiguration("order_id", "standard_test_tbl"));
//        Properties props = new Properties();
//        props.setProperty("algorithm-expression", "demo_ds_${user_id % 2}");
//        result.getShardingAlgorithms().put("inline", new AlgorithmConfiguration("INLINE", props));
//        result.getShardingAlgorithms().put("standard_test_tbl", new AlgorithmConfiguration("STANDARD_TEST_TBL", new Properties()));
        result.getShardingAlgorithms().put("month_interval", createMonthIntervalAlgorithm());
        result.getKeyGenerators().put("snowflake", new AlgorithmConfiguration("SNOWFLAKE", new Properties()));
//        result.getAuditors().put("sharding_key_required_auditor", new AlgorithmConfiguration("DML_SHARDING_CONDITIONS", new Properties()));
        return result;
    }

    private ShardingTableRuleConfiguration listInfoTableRuleConfiguration() {
        ShardingTableRuleConfiguration result = new ShardingTableRuleConfiguration("list_info", "db0.list_info_${202309..203001}");
        result.setTableShardingStrategy(new StandardShardingStrategyConfiguration("create_time", "month_interval"));
        result.setKeyGenerateStrategy(new KeyGenerateStrategyConfiguration("id", "snowflake"));
        return result;
    }


    private AlgorithmConfiguration createMonthIntervalAlgorithm() {
        Properties props = new Properties();
//        props.setProperty("algorithm-expression", "demo_ds_${user_id % 2}");
        props.setProperty("datetime-pattern", "yyyy-MM-dd HH:mm:ss");
        props.setProperty("datetime-lower", "2023-01-01 00:00:00");
        props.setProperty("datetime-upper", "2099-01-01 00:00:00");
        props.setProperty("sharding-suffix-pattern", "yyyyMM");
        props.setProperty("datetime-interval-amount", "1");
        props.setProperty("datetime-interval-unit", "MONTHS");
        return new AlgorithmConfiguration("INTERVAL", props);
    }
}
