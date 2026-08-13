package hello.aop.exam;

import hello.aop.exam.annotation.Trace;
import hello.aop.exam.aop.RetryAspect;
import hello.aop.exam.aop.TraceAspect;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * 5번째에 일부러 예외발생 -> AOP 를 이용하여 재시도
 */
@Slf4j
@Import({TraceAspect.class, RetryAspect.class})
@SpringBootTest
public class ExamTest {

    @Autowired
    ExamService examService;

    @Test
    void test() {
        for (int i = 0; i < 5; i++) {
            log.info("request={}", i);
            examService.request("data" + i);
        }
    }
}
