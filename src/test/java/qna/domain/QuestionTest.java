package qna.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import qna.exception.CannotDeleteException;
import static qna.domain.UserTest.*;

@DataJpaTest
public class QuestionTest {
    public static final Question Q1 = new Question("title1", "contents1").writeBy(UserTest.DORAEMON);
    public static final Question Q2 = new Question("title2", "contents2").writeBy(UserTest.SPONGEBOB);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerRepository answerRepository;

    private User user;
    private Question question;


    @BeforeEach
    void setUp() {
        user = userRepository.save(new User("user1", "password", "name", "email@test.com"));
        question = questionRepository.save(new Question("제목", "내용"));
        question.writeBy(user);

        userRepository.save(DORAEMON);
        userRepository.save(SPONGEBOB);
        userRepository.save(CONAN);


    }

    @Test
    @DisplayName("작성자 검증 함수에 작성자가 아닌 다른 유저가 전달되면 예외가 발생한다")
    void validateOwnershipTest1() {
        // given
        User 다른_유저 = userRepository.save(new User("b", "", "", ""));
        Question 질문 = new Question("title1", "contents1").writeBy(user);

        // when & then
        Assertions.assertThatThrownBy(
                () -> 질문.validateUser(다른_유저)
        ).isInstanceOf(CannotDeleteException.class);
    }

    @Test
    @DisplayName("작성자 검증 함수에 작성자 유저가 전달되면 예외가 발생하지 않는다")
    void validateOwnershipTest2() {
        // given

        Question 질문 = new Question("title1", "contents1").writeBy(user);

        // when & then
        질문.validateUser(user);

    }

    @Test
    @DisplayName("모든 작성자 검증 함수에 작성자 유저가 전달되면 예외가 발생한다")
    void 모든_유저_확인(){
        //로그인 유저랑 일치하지 않는 답변이 있어야함
        Question 질문 = new Question("title1", "contents1").writeBy(DORAEMON);
        Answer 일치하는_답변 = new Answer(DORAEMON,질문,"");
        Answer 일치하지_않는_답변 = new Answer(CONAN,질문,"");

        질문.addAnswer(일치하는_답변);
        질문.addAnswer(일치하지_않는_답변);

        Assertions.assertThatThrownBy(
                () -> 질문.validateOwner(일치하지_않는_답변.getWriter())
        ).isInstanceOf(CannotDeleteException.class);


    }
    @Test
    @DisplayName("모든 작성자 검증 함수에 작성자 유저가 전달되면 예외가 발생하지않는다")
    void 모든_유저_확인_통과함(){
        //로그인 유저랑 일치하지 않는 답변이 있어야함
        Question 질문 = new Question("title1", "contents1").writeBy(DORAEMON);
        Answer 일치하는_답변 = new Answer(DORAEMON,질문,"");

        질문.addAnswer(일치하는_답변);

        질문.validateUser(일치하는_답변.getWriter());
        Assertions.assertThat(일치하는_답변.getWriter()).isEqualTo(질문.getWriter());

    }

}
