package qna.domain;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import qna.exception.CannotDeleteException;

import static org.assertj.core.api.Assertions.*;
import static qna.domain.QuestionTest.Q1;
import static qna.domain.UserTest.*;

@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

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


    @DisplayName("Question 저장 테스트")
    @Test
    void saveQuestionTest() {
        // given
        // - Question 객체를 생성한다. (제목: "제목", 내용: "내용")
        Question question = new Question(1L,"제목","내용");
        // when
        // - 생성한 Question 객체를 저장한다.
        questionRepository.save(question);
        // then
        // - 저장된 Question의 ID가 null이 아닌지 검증한다.
        assertThat(question.getId()).isNotNull();
        // - 저장된 Question의 제목이 "제목"인지 검증한다.
        assertThat(question.getTitle().equals("제목")).isTrue();
        // - 저장된 Question의 내용이 "내용"인지 검증한다.
        assertThat(question.getContents().equals("내용")).isTrue();


    }

    @DisplayName("Question 목록 조회 테스트")
    @Test
    void findAllQuestionTest() {

        questionRepository.deleteAll();
        // given
        // - 2개의 Question을 생성하고 저장한다. (제목1: "제목1", 내용1: "내용1"), (제목2: "제목2", 내용2: "내용2")
        Question question = new Question(1L,"제목1","내용1");
        Question question2 = new Question(2L,"제목2","내용2");
        questionRepository.save(question);
        questionRepository.save(question2);
        // when
        // - 모든 Question의 목록을 조회한다.
        List<Question> questionList = questionRepository.findAll();
        // then
        // - 조회된 Question의 목록 크기가 2인지 검증한다.
        assertThat(questionList.size()).isEqualTo(2);
    }

    @DisplayName("삭제되지 않은 Question 목록 조회 테스트")
    @Test
    void findByDeletedFalseTest() {
        questionRepository.deleteAll();
        // given
        // - 2개의 Question을 생성한다. (제목1: "제목1", 내용1: "내용1"), (제목2: "제목2", 내용2: "내용2")
        // - 두 번째 Question의 deleted를 true로 설정하고 저장한다.
        Question question = new Question("제목1","내용1");
        Question question2 = new Question("제목2","내용2");
        question2.setDeleted(true);
        questionRepository.save(question);
        questionRepository.save(question2);

        // when
        // - deleted가 false인 Question의 목록을 조회한다.
        List<Question> questionList = questionRepository.findByDeletedFalse();
        // then
        // - 조회된 Question의 목록 크기가 1인지 검증한다.
        assertThat(questionList.size()).isEqualTo(1);
        // - 조회된 Question의 제목이 "제목1"인지 검증한다.
        assertThat(questionList.get(0).getTitle()).isEqualTo("제목1");
    }

    @DisplayName("삭제되지 않은 Question 단건 조회 테스트")
    @Test
    void findByIdAndDeletedFalseTest() {
        // given
        // - Question을 생성하고 저장한다. (제목: "제목", 내용: "내용")
        Question question = new Question(1L,"제목","내용");
        questionRepository.save(question);
        // when
        // - 저장된 Question의 ID와 deleted가 false인 조건으로 단건 조회한다.
        Question 조회된애 = questionRepository.findByIdAndDeletedFalse(question.getId()).orElse(null);
        // then
        assertThat(조회된애).isNotNull();
        // - 조회 결과가 존재하는지 검증한다.
        assertThat(조회된애.getTitle()).isEqualTo("제목");
        // - 조회된 Question의 제목이 "제목"인지 검증한다.
    }

    @DisplayName("제목이 없는 Question 저장 시 예외 발생 테스트")
    @Test
    void saveQuestionWithoutTitleTest() {
        // given
        // - 제목이 null인 Question 객체를 생성한다.
        Question question = new Question(null,"내용");
        // when&then
        // - 제목이 null인 Question 객체를 저장한다.
        // - 데이터베이스 제약 조건 위반 예외(DataIntegrityViolationException)가 발생하는지 검증한다.
        assertThatThrownBy(()->
                questionRepository.save(question))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @DisplayName("제목 길이가 100자를 초과하는 Question 저장 시 예외 발생 테스트")
    @Test
    void saveQuestionWithTooLongTitleTest() {
        // given
        // - 101자의 제목을 가진 Question 객체를 생성한다.
        String longTitle = "a".repeat(101);
        Question question = new Question(longTitle, "내용");

        // when&then
        // - 101자의 제목을 가진 Question 객체를 저장한다.
        //- 데이터베이스 제약 조건 위반 예외(DataIntegrityViolationException)가 발생하는지 검증한다.
        assertThatThrownBy(()->questionRepository.save(question))
                .isInstanceOf(DataIntegrityViolationException.class);

    }




}