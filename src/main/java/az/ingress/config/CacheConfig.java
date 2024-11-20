package az.ingress.config;

import  lombok.Value;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
    //@Value("${redisson.server.urls}")
    //private String redisServer;

    @Bean
    public RedissonClient redissonSingleClient(){
        Config config = new Config();

        config
                .setCodec(new SerializationCodec())
                .useSingleServer()
                .setAddress("redis://localhost:6379");

        return Redisson.create(config);
    }
}
