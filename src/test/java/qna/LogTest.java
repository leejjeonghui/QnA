package qna;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogTest {

    private static final Logger logger = LoggerFactory.getLogger(LogTest.class);

    @BeforeAll
    static void setUp() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Level logLevel = Level.TRACE; //실제로는 설정파일에다가 로그레벨을 세팅해줌
//        Level logLevel = Level.WARN;
        loggerContext.getLogger("ROOT")
                .setLevel(logLevel);
    }

    @Test
    void logByLevel() {
        logger.trace("[TRACE] 함수 xxx() 호출됨");
        logger.debug("[DEBUG] 쿼리 실행됨: SELECT * FROM products WHERE id = 23");
        logger.info("[INFO] 애플리케이션 시작됨");
        logger.warn("[WARN] 연결 시간 초과됨");
        logger.error("[ERROR] 주문을 읽어 오는 중에 상품 정보를 찾을 수 없는 문제 발생");
    }
}