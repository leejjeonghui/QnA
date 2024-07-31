package qna.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;
@Entity
public class DeleteHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    @ManyToOne
    @JoinColumn(nullable = false)
    private User deletedBy;

    public User getDeletedBy() {
        return deletedBy;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public Long getContentId() {
        return contentId;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    @Enumerated(EnumType.STRING)
    private ContentType contentType;
    private Long contentId;
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createDate = LocalDateTime.now();

    protected DeleteHistory() {
    }

    public DeleteHistory(ContentType contentType, Long contentId, User deletedBy, LocalDateTime createDate) {
        this.contentType = contentType;
        this.contentId = contentId;
        this.deletedBy = deletedBy;
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteHistory that = (DeleteHistory) o;
        return Objects.equals(id, that.id) &&
                contentType == that.contentType &&
                Objects.equals(contentId, that.contentId) &&
                Objects.equals(deletedBy.getId(), that.deletedBy.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contentType, contentId, deletedBy.getId());
    }

    @Override
    public String toString() {
        return "DeleteHistory{" +
                "id=" + id +
                ", contentType=" + contentType +
                ", contentId=" + contentId +
                ", deletedById=" + deletedBy.getId() +
                ", createDate=" + createDate +
                '}';
    }
}
