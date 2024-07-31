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
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.*;

import static qna.domain.UserTest.*;


@DataJpaTest
@Sql("/truncate.sql")
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerRepository answerRepository;

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


    @Test
    void getAnswersTest() {
        // given
        /*
         * DB에 질문과 그 질문에 대한 2개의 답변들이 저장되어 있는 상황에서
         * */
        User 작성자 = userRepository.save(new User("", "", "", ""));
        Question 질문 = new Question("제목", "내용");
        질문.writeBy(작성자);
        questionRepository.save(질문); // 캐싱됨
//        Answer 답변1 = new Answer(); // writer와 question이 null
//        답변1.writer = 작성자; // private이라서 불가능
//        답변1.question = 질문; // private이라서 불가능
        answerRepository.save(new Answer(null, 작성자, 질문, ""));
        answerRepository.save(new Answer(null, 작성자, 질문, ""));
        em.clear(); // 캐시 삭제

        // when
        /*
         * QuestionRepository로 질문만 조회했을 때,
         * 그 Question 오브젝트의 getAnswers() 메서드를 호출하면
         * */
        Question 찾은_질문 = questionRepository.findById(질문.getId())
                .orElse(null);

        // then
        /*
         * return된 Answer 리스트에 답변 2개가 들어 있다.
         * */
        assertThat(찾은_질문.getAnswers()).hasSize(2);
    }


    @DisplayName("Question을 저장할 때, Question의 answer리스트에 포함 된 answer들까지 함께 저장한다")
    @Test
    void 질문과_함께저장(){
        // given
        User 작성자 = userRepository.save(new User("", "", "", ""));

        Question 질문 = new Question("제목", "내용");
        질문.addAnswer(new Answer(작성자,질문, "Answers Contents1"));
        질문.addAnswer(new Answer(SPONGEBOB,질문, "Answers Contents2"));

        //when
        questionRepository.save(질문);
        em.clear();

        // then
        assertThat(질문.getAnswers().size()).isEqualTo(2);
        assertThat(질문.getAnswers().get(0).getContents()).isEqualTo("Answers Contents1");

    }


    @Test
    void remove_Answer(){
        // given
        User 작성자 = userRepository.save(new User("", "", "", ""));
        Question 질문 = new Question("제목", "내용");
        질문.addAnswer(new Answer(작성자,질문, "Answers Contents1"));
        질문.addAnswer(new Answer(SPONGEBOB,질문, "Answers Contents2"));

        //when
        questionRepository.save(질문);

        질문.getAnswers().remove(0);

        em.clear();

        // then
        assertThat(질문.getAnswers().size()).isEqualTo(1);
        assertThat(질문.getAnswers().get(0).getContents()).isEqualTo("Answers Contents2");

    }

    // 현실적인 시나리오
    @Test
    @DisplayName("조회한 Question의 answers(Answer 리스트)에 Answer를 추가하기만 하면 Answer가 저장된다")
    void 질문과함께답변까지저장_좀더_현실적() {
        // given
        // 질문이 저장되어 있고
        User 유저 = userRepository.save(new User("", "", "", ""));
        Question 질문 = new Question("", "");
        질문.writeBy(유저);
        questionRepository.save(질문);
        em.clear();

        // (여기까지가 질문자가 질문을 작성해 둔 상황)
        // (그리고 답변 생성 요청이 들어와서 질문에 답변을 추가하는 상황이 이어진다)

        // when: 찾은 질문의 답변 목록에 답변을 추가하기만 하면
        Question 찾은_질문 = questionRepository.findById(질문.getId())
                .orElse(null);
        Answer 답변 = new Answer(유저, 질문, "답변");
        찾은_질문.addAnswer(답변);

        // then
        // 답변이 저장됨을 확인할 수 있다
        List<Answer> answers = answerRepository.findByQuestion_IdAndDeletedFalse(찾은_질문.getId());
        assertThat(answers).hasSize(1);
    }

}