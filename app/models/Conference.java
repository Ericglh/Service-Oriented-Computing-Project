package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import play.data.format.Formats;

import javax.persistence.*;
import java.util.List;

@Entity
public class Conference extends com.avaje.ebean.Model {

    @Id
    @Column(columnDefinition = "INT(11) NOT NULL")
    private int id;

    @Column(columnDefinition = "VARCHAR(255)")
    private String acronym;

    @Column(columnDefinition = "VARCHAR(255)")
    private String location;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private String date;

    @Formats.DateTime(pattern = "yyyy-MM-dd")
    private String submissionDeadline;

    @Column(columnDefinition = "VARCHAR(255)")
    private String status;

    @OneToMany(mappedBy = "conference", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Paper> papers;

    @ManyToMany(mappedBy = "reviewConfs", cascade = CascadeType.ALL)
    @JoinTable(name = "reviewer_conference")
    @JsonIgnore
    private List<User> reviewers;

    @ManyToMany(mappedBy = "pendingReviewConfs", cascade = CascadeType.ALL)
    @JoinTable(name = "pending_reviewer_conference")
    @JsonIgnore
    private List<User> pendingReviewers;

    @ManyToMany(mappedBy = "adminConfs", cascade = CascadeType.ALL)
    @JoinTable(name = "admin_conference")
    @JsonIgnore
    private List<User> admins;

    /**
     * Stored as json string
     */
    @JsonRawValue
    private String researchTopics;

    @JsonRawValue
    @Column(columnDefinition = "TEXT")
    private String criteria;

    @JsonRawValue
    @Column(columnDefinition = "TEXT")
    private String reviewQuestions;

    @JsonRawValue
    @Column(columnDefinition = "TEXT")
    private String statusCodes;

    @Column(columnDefinition = "TEXT")
    private String logoUrl;

    private String submissionEmailTemplate = "Your paper {title} has been submitted. The paper ID is {paperId}";

    private String reminderEmailTemplate = "Dear {username},\n\nThis is to remind you that you still have {numPapersNotReviewed} papers to review.";

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(columnDefinition = "INT(11) NULL")
    @JsonIgnore
    private User pcChair;

    public static Finder<Integer, Conference> find = new Finder<Integer, Conference>(Conference.class);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Getter method for property <tt>date</tt>.
     *
     * @return property value of date
     */
    public String getDate() {
        return date;
    }

    /**
     * Setter method for property <tt>date</tt>.
     *
     * @param date value to be assigned to property date
     */
    public void setDate(String date) {
        this.date = date;
    }

    public String getSubmissionDeadline() {
        return submissionDeadline;
    }

    public void setSubmissionDeadline(String submissionDeadline) {
        this.submissionDeadline = submissionDeadline;
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

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }

    public List<User> getReviewers() {
        return reviewers;
    }

    public List<User> getAdmins() {
        return admins;
    }

    /**
     * Setter method for property <tt>reviewers</tt>.
     *
     * @param reviewers value to be assigned to property reviewers
     */
    public void setReviewers(List<User> reviewers) {
        this.reviewers = reviewers;
    }

    public List<User> getPendingReviewers() {
        return pendingReviewers;
    }

    public void setPendingReviewers(List<User> pendingReviewers) {
        this.pendingReviewers = pendingReviewers;
    }

    /**
     * Setter method for property <tt>admins</tt>.
     *
     * @param admins value to be assigned to property admins
     */
    public void setAdmins(List<User> admins) {
        this.admins = admins;
    }

    public void addReviewer(User reviewer) {
        reviewers.add(reviewer);
    }

    public void addAdmin(User admin) {
        admins.add(admin);
    }


    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getSubmissionEmailTemplate() {
        return submissionEmailTemplate;
    }

    public void setSubmissionEmailTemplate(String submissionEmailTemplate) {
        this.submissionEmailTemplate = submissionEmailTemplate;
    }

    public String getReminderEmailTemplate() {
        return reminderEmailTemplate;
    }

    public void setReminderEmailTemplate(String reminderEmailTemplate) {
        this.reminderEmailTemplate = reminderEmailTemplate;
    }

    public User getPcChair() {
        return pcChair;
    }

    public void setPcChair(User pcChair) {
        this.pcChair = pcChair;
    }

    public String getResearchTopics() {
        return researchTopics;
    }

    public void setResearchTopics(String researchTopics) {
        this.researchTopics = researchTopics;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public String getReviewQuestions() {
        return reviewQuestions;
    }

    public void setReviewQuestions(String reviewQuestions) {
        this.reviewQuestions = reviewQuestions;
    }

    public String getStatusCodes() {
        return statusCodes;
    }

    public void setStatusCodes(String statusCodes) {
        this.statusCodes = statusCodes;
    }
}
