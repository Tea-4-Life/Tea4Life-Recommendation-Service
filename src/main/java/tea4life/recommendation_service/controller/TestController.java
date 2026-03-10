package tea4life.recommendation_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin 3/10/2026
 *
 **/
@RestController
@RequestMapping("/public")
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Hello World";
    }
}
