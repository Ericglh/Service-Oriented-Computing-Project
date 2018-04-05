package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.Constraints;

import javax.persistence.*;
import java.util.List;

@Entity
public class User extends com.avaje.ebean.Model {

    @Id
    @Column(columnDefinition = "INT(11)")
    private int id;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Profile profile;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL UNIQUE")
    @Constraints.Required
    private String username;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    @Constraints.Required
    @JsonIgnore
    private String password;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    @Constraints.Required
    private String securityQuestion;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    @Constraints.Required
    @JsonIgnore
    private String answer;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    @Constraints.Required
    private String backupEmail;

    @ManyToMany(mappedBy = "authors", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Paper> papers;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "reviewer_conference")
    @JsonIgnore
    private List<Conference> reviewConfs;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "pending_reviewer_conference")
    @JsonIgnore
    private List<Conference> pendingReviewConfs;

    /**
     * List of conferences which this user is an administer
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "admin_conference")
    @JsonIgnore
    private List<Conference> adminConfs;

    /**
     * List of conferences which this user is a PC Chair
     */
    @OneToMany(mappedBy = "pcChair", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Conference> pcChairConfs;

    /**
     * List of papers which this user has been assigned to review as a reviewer
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "reviewer_paper")
    @JsonIgnore
    private List<Paper> assignedPapers;

    /**
     * List of papers which this user has already reviewed
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "reviewed_reviewer_paper")
    @JsonIgnore
    private List<Paper> reviewedPapers;

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Review> reviews;

    public static Finder<Integer, User> find = new Finder<Integer, User>(User.class);

    public int getId() {
        return id;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getBackupEmail() {
        return backupEmail;
    }

    public void setBackupEmail(String backupEmail) {
        this.backupEmail = backupEmail;
    }

    public List<Paper> getPapers() {
        return papers;
    }

    public void setPapers(List<Paper> papers) {
        this.papers = papers;
    }

    public List<Conference> getReviewConfs() {
        return reviewConfs;
    }

    public void setReviewConfs(List<Conference> reviewConfs) {
        this.reviewConfs = reviewConfs;
    }

    public void addReviewConf(Conference conference) {
        this.reviewConfs.add(conference);
    }

    public List<Conference> getPendingReviewConfs() {
        return pendingReviewConfs;
    }

    public void setPendingReviewConfs(List<Conference> pendingReviewConfs) {
        this.pendingReviewConfs = pendingReviewConfs;
    }

    public void addPendingReviewConfs(Conference conference) {this.pendingReviewConfs.add(conference);}

    public void deletePendingReviewConfs(Conference conference) {
        for (int i = 0; i < this.pendingReviewConfs.size(); i++) {
            Conference c = this.pendingReviewConfs.get(i);
            if(c.getId() == conference.getId()) {
                this.pendingReviewConfs.remove(i);
                break;
            }
        }
    }

    public List<Conference> getAdminConfs() {
        return adminConfs;
    }

    public void setAdminConfs(List<Conference> adminConfs) {
        this.adminConfs = adminConfs;
    }

    public void addAdminConf(Conference conference) {
        this.adminConfs.add(conference);
    }

    public List<Conference> getPcChairConfs() {
        return pcChairConfs;
    }

    public void setPcChairConfs(List<Conference> pcChairConfs) {
        this.pcChairConfs = pcChairConfs;
    }

    public void addPcChairConf(Conference conference) {
        this.pcChairConfs.add(conference);
    }

    public List<Paper> getAssignedPapers() {
        return assignedPapers;
    }

    public void addAssignedPaper(Paper paper) {
        this.assignedPapers.add(paper);
    }

    public void setAssignedPapers(List<Paper> assignedPapers) {
        this.assignedPapers = assignedPapers;
    }

    public List<Paper> getReviewedPapers() {
        return reviewedPapers;
    }

    public void addReviewedPaper(Paper paper) {
        this.reviewedPapers.add(paper);
    }

    public void setReviewedPapers(List<Paper> reviewedPapers) {
        this.reviewedPapers = reviewedPapers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

}
