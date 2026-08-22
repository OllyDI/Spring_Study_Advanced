package hello.aop.proxyvs;

import hello.aop.member.MemberService;
import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactory;

import static org.junit.jupiter.api.Assertions.*;


/**
 * 프록시 캐스팅은 의존관계 주입 시 문제 발생 -> ProxyDITest 참고
 * JDK 동적 프록시 -> 구현 클래스 캐스팅 불가
 * CGLIB 프록시 -> 구현 클래스, 인터페이스 캐스팅 가능
 */
@Slf4j
public class ProxyCastingTest {

    @Test
    void jdkProxy() {
        MemberServiceImpl target = new MemberServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(false);    // JDK 동적 프록시

        // 프록시 MemberService 인터페이스로 캐스팅 성공
        MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();

        // 문제1. JDK 동적 프록시는 구현 클래스(인터페이스가 아닌)로 캐스팅 시도 -> 실패 ClassCastException
        // MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy;
        assertThrows(ClassCastException.class, () -> {
            MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy;
        });
    }

    @Test
    void cglibProxy() {
        MemberServiceImpl target = new MemberServiceImpl();
        ProxyFactory proxyFactory = new ProxyFactory(target);
        proxyFactory.setProxyTargetClass(true);    // CGLIB 프록시

        // 프록시 MemberService 인터페이스로 캐스팅 성공
        MemberService memberServiceProxy = (MemberService) proxyFactory.getProxy();

        // 문제1. CGLIB 동적 프록시는 구현 클래스(인터페이스가 아닌)로 캐스팅 시도 -> 성공, 인터페이스도 가능
        MemberServiceImpl castingMemberService = (MemberServiceImpl) memberServiceProxy;
    }
}
