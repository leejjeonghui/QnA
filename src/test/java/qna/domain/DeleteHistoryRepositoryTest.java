package qna.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static qna.domain.UserTest.*;



@DataJpaTest
@Sql("/truncate.sql")
public class DeleteHistoryRepositoryTest {

    @Autowired
    private DeleteHistoryRepository deleteHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void setUp() {
        userRepository.save(DORAEMON);
        userRepository.save(SPONGEBOB);
        userRepository.save(CONAN);
    }
    //외래키인애는 미리 저장을 해주지 않으면 오류난다
    @Test
    public void testSaveDeleteHistory() {
        // Given
        // DeleteHistory 객체를 생성한다. (ContentType, contentId, deletedById, createDate 설정)
        DeleteHistory deleteHistory = new DeleteHistory(ContentType.ANSWER, 1L,DORAEMON, LocalDateTime.now());

        // When
        // 생성한 DeleteHistory 객체를 저장한다.
        DeleteHistory saved = deleteHistoryRepository.save(deleteHistory);

        // Then
        // 저장된 DeleteHistory 객체가 null이 아닌지 확인한다.
        // 저장된 DeleteHistory 객체의 ID가 null이 아닌지 확인한다.
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();

    }

    @Test
    public void testFindDeleteHistoryById() {

        User 삭제한유저=SPONGEBOB;
        // Given
        // DeleteHistory 객체를 생성하고 저장한다.
        DeleteHistory saved = deleteHistoryRepository.save(new DeleteHistory(ContentType.ANSWER, 1L,삭제한유저, LocalDateTime.now()));
        // Referential integrity constraint violation : User에서 참조할 수 없는 외래키 값을 가지고 있다?
        // When
        // 얘는 id 2
        // 저장된 DeleteHistory의 ID로 조회한다.
        DeleteHistory deleteHistory = deleteHistoryRepository.findById(saved.getId())
                .orElse(null);
            //얘는 id - 1
        // Then
        //저희는 팩토리 메서드를 통해 매번 새롭게 인스턴스를 생성하는 방식을 활용 했어요!
        // 조회된 DeleteHistory 객체가 null이 아닌지 확인한다.
        // 조회된 DeleteHistory 객체의 ContentType이 기대한 값과 일치하는지 확인한다.
        // 조회된 DeleteHistory 객체의 contentId가 기대한 값과 일치하는지 확인한다.
        assertThat(deleteHistory).isNotNull();
        assertThat(deleteHistory.getContentType()).isEqualTo(saved.getContentType());
        assertThat(deleteHistory.getContentId()).isEqualTo(saved.getContentId());

    }

    @Test
    public void testFindAllDeleteHistories() {
        // Given
        // 여러 개의 DeleteHistory 객체를 생성하고 저장한다.
        DeleteHistory saved1 = deleteHistoryRepository.save(new DeleteHistory(ContentType.ANSWER, 1L,DORAEMON, LocalDateTime.now()));
        DeleteHistory saved2 = deleteHistoryRepository.save(new DeleteHistory(ContentType.ANSWER, 2L,SPONGEBOB , LocalDateTime.now()));
        DeleteHistory saved3 = deleteHistoryRepository.save(new DeleteHistory(ContentType.ANSWER, 3L,CONAN, LocalDateTime.now()));

        // When
        // 모든 DeleteHistory 객체를 조회한다.
        List<DeleteHistory> deleteHistories = deleteHistoryRepository.findAll();

        // Then
        // 조회된 DeleteHistory 리스트가 비어있지 않은지 확인한다.
        // 조회된 DeleteHistory 리스트의 크기가 기대한 값과 일치하는지 확인한다.
        assertThat(deleteHistories).isNotEmpty();
        assertThat(deleteHistories).hasSize(3);
    }





}
