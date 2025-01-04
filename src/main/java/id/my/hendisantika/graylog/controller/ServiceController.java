package id.my.hendisantika.graylog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-graylog
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 05/01/25
 * Time: 05.59
 * To change this template use File | Settings | File Templates.
 */
@RestController
public class ServiceController {

    @GetMapping("/app/status")
    public String controllerStatus() {
        return "Service controller is up";
    }
}
