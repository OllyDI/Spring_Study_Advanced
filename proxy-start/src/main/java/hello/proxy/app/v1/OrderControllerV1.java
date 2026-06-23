package hello.proxy.app.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 스프링은 @Controller or @RequestMapping 이 있으면 스프링 컨트롤러로 인식
 */
@RequestMapping
@ResponseBody
public interface OrderControllerV1 {

    @GetMapping("/v1/request")
    String request(@RequestParam("itemId") String itemId);  // 인터페이스는 @RequestParam 을 넣어줘야 인식 잘됨

    @GetMapping("/v1/no-log")
    String noLog();
}
