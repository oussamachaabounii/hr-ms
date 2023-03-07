package com.talenteo.hr;
import com.talenteo.hr.config.HrMsConfiguration;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@Import(HrMsConfiguration.class)
@AllArgsConstructor
public class HrMsApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(HrMsApplication.class);
        springApplication.addListeners(new ApplicationPidFileWriter());
        springApplication.run(args);

    }
}
