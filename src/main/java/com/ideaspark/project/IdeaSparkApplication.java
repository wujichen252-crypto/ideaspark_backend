package com.ideaspark.project;

<<<<<<< HEAD
import io.github.cdimascio.dotenv.Dotenv;
=======
>>>>>>> origin/main
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class IdeaSparkApplication {

    /**
     * 应用入口
     */
    public static void main(String[] args) {
<<<<<<< HEAD
        // 加载 .env 文件到系统属性
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        
=======
>>>>>>> origin/main
        SpringApplication.run(IdeaSparkApplication.class, args);
    }
}
