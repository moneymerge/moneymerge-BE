package example.com.moneymergebe.global.webMVC;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
            .allowedOrigins("http://cdpn.io", "http://localhost:3000", "http://43.203.66.36:3000")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
