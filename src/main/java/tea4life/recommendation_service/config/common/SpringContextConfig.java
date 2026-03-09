package tea4life.recommendation_service.config.common;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Admin 1/26/2026
 */
@Configuration
public class SpringContextConfig {

    @Bean
    public static SpringUtil springUtil() {
        return new SpringUtil();
    }
}
