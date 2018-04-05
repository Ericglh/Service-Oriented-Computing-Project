package models;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by htyleo on 4/12/17.
 */
@Entity
public class Review extends Model {

    @Id
    @Column(columnDefinition = "INT(11) NOT NULL")
    private int id;

    @ManyToOne
    @JsonIgnore
    private Paper paper;

    @ManyToOne
    @JsonIgnore
    private User reviewer;

    @Column(columnDefinition = "VARCHAR(4096)")
    private String content;

    public static Finder<Integer, Review> finder = new Finder<Integer, Review>(Review.class);

    /**
     * Getter method for property <tt>id</tt>.
     *
     * @return property value of id
     */
    public int getId() {
        return id;
    }

    /**
     * Getter method for property <tt>paper</tt>.
     *
     * @return property value of paper
     */
    public Paper getPaper() {
        return paper;
    }

    /**
     * Setter method for property <tt>paper</tt>.
     *
     * @param paper value to be assigned to property paper
     */
    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    /**
     * Getter method for property <tt>reviewer</tt>.
     *
     * @return property value of reviewer
     */
    public User getReviewer() {
        return reviewer;
    }

    /**
     * Setter method for property <tt>reviewer</tt>.
     *
     * @param reviewer value to be assigned to property reviewer
     */
    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    /**
     * Getter method for property <tt>content</tt>.
     *
     * @return property value of content
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter method for property <tt>content</tt>.
     *
     * @param content value to be assigned to property content
     */
    public void setContent(String content) {
        this.content = content;
    }
}
