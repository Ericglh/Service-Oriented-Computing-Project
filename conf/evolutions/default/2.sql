# --- dataset

DROP DATABASE IF EXISTS `playdbtest`;
CREATE DATABASE `playdbtest`;
USE `playdbtest`;

# --- !Ups

INSERT INTO conference (acronym, location, date, submission_deadline, status, research_topics, criteria, review_questions, status_codes, submission_email_template, reminder_email_template)
VALUES ("conf1", "Mountain View", "2017-04-12", "2017-03-20", "Active", "[]", "[]", "[]", "[]",
                 "Your paper {title} has been submitted. The paper ID is {paperId}",
                 "Dear {username},\n\nThis is to remind you that you still have {numPapersNotReviewed} papers to review.");

INSERT INTO conference (acronym, location, date, submission_deadline, status, research_topics, criteria, review_questions, status_codes, submission_email_template, reminder_email_template)
VALUES ("conf2", "San Jose", "2017-04-18", "2017-01-03", "Archived", "[]", "[]", "[]", "[]",
                 "Your paper {title} has been submitted. The paper ID is {paperId}",
                 "Dear {username},\n\nThis is to remind you that you still have {numPapersNotReviewed} papers to review.");
