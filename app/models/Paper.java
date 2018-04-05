package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Paper extends com.avaje.ebean.Model {

    @Id
    @Column(columnDefinition = "INT(11) NOT NULL")
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    @JsonProperty("email")
    private String emailOfContactAuthor;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<User> authors;

    @Column(columnDefinition = "VARCHAR(255)")
    private String otherAuthors;

    @Column(columnDefinition = "VARCHAR(16) NOT NULL")
    private String awardCandidate;

    @Column(columnDefinition = "VARCHAR(11) NOT NULL")
    @JsonProperty("studentVolunteer")
    private String volunteer;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String abstractText;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    private Conference conference;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String topic;

    /**
     * Accept, Reject, Moved, Transferred
     */
    @Column(columnDefinition = "VARCHAR(255)")
    private String status;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private Date submissionDate;

    @Column(columnDefinition = "VARCHAR(255)")
    private String fileUrl;

    @Column(columnDefinition = "VARCHAR(255)")
    private String fileSize;

    @Column(columnDefinition = "VARCHAR(255)")
    private String fileName;

    @ManyToMany(mappedBy = "assignedPapers", cascade = CascadeType.ALL)
    @JoinTable(name = "reviewer_paper")
    @JsonIgnore
    private List<User> reviewers;

    @ManyToMany(mappedBy = "reviewedPapers", cascade = CascadeType.ALL)
    @JoinTable(name = "reviewed_reviewer_paper")
    @JsonIgnore
    private List<User> reviewedReviewers;

    @OneToMany(mappedBy = "paper")
    @JsonIgnore
    private List<Review> reviews;

    /**
     * Getter method for property <tt>id</tt>.
     *
     * @return property value of id
     */
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEmailOfContactAuthor() {
        return emailOfContactAuthor;
    }

    public void setEmailOfContactAuthor(String emailOfContactAuthor) {
        this.emailOfContactAuthor = emailOfContactAuthor;
    }

    public List<User> getAuthors() {
        return authors;
    }

    public void setAuthors(List<User> authors) {
        this.authors = authors;
    }

    public String getOtherAuthors() {
        return otherAuthors;
    }

    public void setOtherAuthors(String otherAuthors) {
        this.otherAuthors = otherAuthors;
    }

    /**
     * Getter method for property <tt>awardCandidate</tt>.
     *
     * @return property value of awardCandidate
     */
    public String getAwardCandidate() {
        return awardCandidate;
    }

    /**
     * Setter method for property <tt>awardCandidate</tt>.
     *
     * @param awardCandidate value to be assigned to property awardCandidate
     */
    public void setAwardCandidate(String awardCandidate) {
        this.awardCandidate = awardCandidate;
    }

    /**
     * Getter method for property <tt>volunteer</tt>.
     *
     * @return property value of volunteer
     */
    public String getVolunteer() {
        return volunteer;
    }

    /**
     * Setter method for property <tt>volunteer</tt>.
     *
     * @param volunteer value to be assigned to property volunteer
     */
    public void setVolunteer(String volunteer) {
        this.volunteer = volunteer;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    public Conference getConference() {
        return conference;
    }

    public void setConference(Conference conference) {
        this.conference = conference;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Date getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Date submissionDate) {
        this.submissionDate = submissionDate;
    }

    /**
     * Getter method for property <tt>fileUrl</tt>.
     *
     * @return property value of fileUrl
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * Setter method for property <tt>fileUrl</tt>.
     *
     * @param fileUrl value to be assigned to property fileUrl
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * Getter method for property <tt>fileSize</tt>.
     *
     * @return property value of fileSize
     */
    public String getFileSize() {
        return fileSize;
    }

    /**
     * Setter method for property <tt>fileSize</tt>.
     *
     * @param fileSize value to be assigned to property fileSize
     */
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Getter method for property <tt>fileName</tt>.
     *
     * @return property value of fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Setter method for property <tt>fileName</tt>.
     *
     * @param fileName value to be assigned to property fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Getter method for property <tt>status</tt>.
     *
     * @return property value of status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Setter method for property <tt>status</tt>.
     *
     * @param status value to be assigned to property status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public List<User> getReviewers() {
        return reviewers;
    }

    public List<User> getReviewedReviewers() {
        return reviewedReviewers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    /**
     * Setter method for property <tt>reviewers</tt>.
     *
     * @param reviewers value to be assigned to property reviewers
     */
    public void setReviewers(List<User> reviewers) {
        this.reviewers = reviewers;
    }

    /**
     * Setter method for property <tt>reviewedReviewers</tt>.
     *
     * @param reviewedReviewers value to be assigned to property reviewedReviewers
     */
    public void setReviewedReviewers(List<User> reviewedReviewers) {
        this.reviewedReviewers = reviewedReviewers;
    }

    /**
     * Setter method for property <tt>reviews</tt>.
     *
     * @param reviews value to be assigned to property reviews
     */
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public void addReviewer(User reviewer) {
        reviewers.add(reviewer);
    }

    public void addReviewedReviewer(User reviewer) {
        reviewedReviewers.add(reviewer);
    }

    public void addReview(Review review) {
        reviews.add(review);
    }
}
