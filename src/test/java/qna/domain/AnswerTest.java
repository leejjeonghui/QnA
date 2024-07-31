package qna.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.CannotCreateTransactionException;
import qna.exception.CannotDeleteException;

@DataJpaTest
public class AnswerTest {
    public static final Answer A1 = new Answer(UserTest.DORAEMON, QuestionTest.Q1, "Answers Contents1");
    public static final Answer A2 = new Answer(UserTest.SPONGEBOB, QuestionTest.Q1, "Answers Contents2");


    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnswerRepository answerRepository;

    }


