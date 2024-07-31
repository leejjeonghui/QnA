package qna.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.DialectOverride;
import org.hibernate.annotations.Where;
import qna.exception.CannotDeleteException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100,nullable = false)
    private String title;
    @Lob
    private String contents;
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;
    //직접참조 (오브젝트의 주소가 이곳에 복사된다)
    //아이디만 선언해주면 간접참조
    //Answer가 Question에 의존한다 = 참조한다
    //Q가 변경되면 A도 변경된다
    @Column(nullable = false)
    private boolean deleted = false;

    @Column(columnDefinition = "TIMESTAMP",nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt =LocalDateTime.now();

    @OneToMany(mappedBy = "question",
            cascade = {CascadeType.PERSIST,CascadeType.REMOVE},
            orphanRemoval = true)
    @Where(clause = "deleted=false")
    private List<Answer> answers = new ArrayList<>();
     //원투매니 쓰고 앤서 리포지토리 안쓰겠음


    public List<Answer> getAnswers() {
        return answers;
    }

    protected Question() {
    }

    public Question(String title, String contents) {
        this(null, title, contents);
    }

    public Question(Long id, String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public Question writeBy(User writer) {
        this.writer = writer;
        return this;
    }

    public boolean isOwner(User writer) {
        return this.writer.getId().equals(writer.getId());
    }

    public void validateUser(User user){
        if(!isOwner(user)){
            throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
        }
    }
    public void validateOwner(User user){
        for(Answer answer : answers){
            if(!answer.isOwner(user)){
                throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
            }
        }

    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
        answers.add(answer);
    }

    public Long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public String getContents() {
        return contents;
    }


    public Long getWriterId() {
        return writer.getId();
    }
    public User getWriter(){return writer;}


    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public DeleteHistory delete(){
        this.deleted =true;
        return new DeleteHistory(ContentType.QUESTION, id,getWriter(), LocalDateTime.now());
    }

    public void deleteAnswers(){
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for(Answer answer: answers){
            answer.setDeleted(true);
            deleteHistories.add(new DeleteHistory(ContentType.ANSWER,answer.getId(),answer.getWriter(), LocalDateTime.now()));
        }
    }


    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writerId=" + writer.getId() +
                ", deleted=" + deleted +
                '}';
    }
}
