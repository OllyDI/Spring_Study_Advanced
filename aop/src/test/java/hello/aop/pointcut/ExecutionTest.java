package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.*;

@Slf4j
public class ExecutionTest {

    AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class);
    }

    @Test
    void printMethod() {
        /*
         execution 문법 -> 아래 출력과 같음
         execution(접근제어자? 반환타입 선언타입?메서드이름(파라미터) 예외?)
         public java.lang.String hello.aop.member.MemberServiceImpl.hello(java.lang.String)
         접근제어자?: public
         반환타입: String
         선언타입?: hello.aop.member.MemberServiceImpl
         메서드이름: hello
         파라미터: (java.lang.String)
        */
        log.info("helloMethod={}", helloMethod);
    }

    @Test
    void exactMath() {
        /*
         정확한 매치
         접근제어자?: public
         반환타입: String
         선언타입?: hello.aop.member.MemberServiceImpl
         메서드이름: hello
         파라미터: (String)
        */
        pointcut.setExpression("execution(public String hello.aop.member.MemberServiceImpl.hello(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void allMath() {
        /*
         전체 매치
         접근제어자?: 생략
         반환타입: *
         선언타입?: 생략
         메서드이름: *
         파라미터: (..)
        */
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatch() {
        /*
         이름 매치
         메서드이름: hello
        */
        pointcut.setExpression("execution(* hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar1() {
        /*
         이름 패턴 매치1
         메서드이름: hel*
        */
        pointcut.setExpression("execution(* hel*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchStar2() {
        /*
         이름 패턴 매치2
         메서드이름: *el*
        */
        pointcut.setExpression("execution(* *el*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void nameMatchFalse() {
        /*
         매치 실패
         메서드이름: null
        */
        pointcut.setExpression("execution(* null(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageExactMatch1() {
        /*
         패키지 매치1
         선언타입?: hello.aop.member.MemberServiceImpl
        */
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.hello(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageMatch2() {
        /*
         패키지 매치2
         선언타입?: hello.aop.member.*
         메서드이름: *
        */
        pointcut.setExpression("execution(* hello.aop.member.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageMatchFalse() {
        /*
         패키지 매치 실패
         선언타입?: hello.aop.*.*
         실패 이유: hello.aop.*.* 사이에 점이 하나 더 들어가야 하위 패키지 포함 -> hello.aop..*.*
         .: 정확하게 해당 위치의 패키지
         ..: 해당위치의 패키지와 그 하위 패키지
        */
        pointcut.setExpression("execution(* hello.aop.*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void packageMatchSubPackage1() {
        /*
         서브 패키지 포함 매치1
         선언타입?: hello.aop.member..*
        */
        pointcut.setExpression("execution(* hello.aop.member..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void packageMatchSubPackage2() {
        /*
         서브 패키지 포함 매치2
         선언타입?: hello.aop..*
        */
        pointcut.setExpression("execution(* hello.aop..*.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeExactMatch() {
        /*
         정확한 타입 매치
         선언타입?: hello.aop.member.MemberServiceImpl
        */
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchSuperType() {
        /*
         슈퍼 타입 매치
         선언타입?: hello.aop.member.MemberService -> 부모타입을 매칭해도 성공
        */
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void typeMatchInternal1() throws NoSuchMethodException {
        /*
         슈퍼 타입 매치
         선언타입?: hello.aop.member.MemberService -> 부모타입
         부모타입에 선언된 메서드까지만 가능 -> 자식타입의 internal 내부 메서드는 매칭 불가
        */
        pointcut.setExpression("execution(* hello.aop.member.MemberService.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void typeMatchInternal2() throws NoSuchMethodException {
        /*
         슈퍼 타입 매치
         선언타입?: hello.aop.member.MemberServiceImpl -> 자식타입
         선언타입이 자식타입이기 때문에 internal 메서드 매칭 가능
        */
        pointcut.setExpression("execution(* hello.aop.member.MemberServiceImpl.*(..))");
        Method internalMethod = MemberServiceImpl.class.getMethod("internal", String.class);
        assertThat(pointcut.matches(internalMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void argsMatch() {
        /*
         String 타입 파라미터 허용
         파라미터: (String)
        */
        pointcut.setExpression("execution(* *(String))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void argsMatchNoArgs() {
        /*
         파라미터 매치: 파라미터가 없어야 함
         파라미터: ()
        */
        pointcut.setExpression("execution(* *())");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }

    @Test
    void argsMatchStar() {
        /*
         파라미터 매치: 정확히 하나의 파라미터만 허용, 모든 타입 허용
         파라미터: (*)
        */
        pointcut.setExpression("execution(* *(*))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void argsMatchAll() {
        /*
         파라미터 매치: 파라미터 개수 타입과 무관하게 모두 허용
         파라미터: (..)
        */
        pointcut.setExpression("execution(* *(..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    @Test
    void argsMatchComplex() {
        /*
         파라미터 매치: String 타입으로 시작하면 파라미터 개수 타입과 무관하게 모두 허용
         파라미터: (String, ..)
        */
        pointcut.setExpression("execution(* *(String, ..))");
        assertThat(pointcut.matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }
}
