package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.Global;
import models.Conference;
import models.Paper;
import models.User;
import org.apache.commons.io.FileUtils;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.GmailService;
import util.JsonUtil;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by chuck on 30/3/17.
 */
public class ConferenceController extends Controller {
    private FormFactory formFactory;

    private GmailService gmailService;

    @Inject
    public ConferenceController(FormFactory formFactory, GmailService gmailService) {
        this.formFactory = formFactory;
        this.gmailService = gmailService;
    }

    /**
     * Get all conferences, associated with user privileges
     *
     * @param username
     * @return
     */
    public Result getAllConfs(String username) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getAllConfs");

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (user == null) {
            return notFound();
        }
        List<Conference> reviewedConfs = user.getReviewConfs();
        List<Conference> adminConfs = user.getAdminConfs();
        List<Conference> pcChairConfs = user.getPcChairConfs();

        List<Conference> conferences = Conference.find.all();
        JsonNode result = Json.toJson(conferences);
        ArrayNode resultJa = (ArrayNode) result;

        for (int i = 0; i < resultJa.size(); i++) {
            Conference conference = conferences.get(i);
            String reviewAccess = "no";
            String adminAccess = "no";
            String pcChairAccess = "no";
            if (reviewedConfs.contains(conference)) {
                reviewAccess = "yes";
            }
            if (adminConfs.contains(conference)) {
                adminAccess = "yes";
            }
            if (pcChairConfs.contains(conference)) {
                pcChairAccess = "yes";
            }
            ObjectNode conferenceJo = (ObjectNode) resultJa.get(i);
            conferenceJo.put("reviewAccess", reviewAccess);
            conferenceJo.put("adminAccess", adminAccess);
            conferenceJo.put("pcChairAccess", pcChairAccess);
        }

        if (Global.showResponse)
            System.out.println("Res: " + result);

        return ok(result);
    }

    /**
     * Get conferences submitted by a user
     *
     * @param username
     * @return
     */
    public Result getConferencesSubmitted(String username) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getConferencesSubmitted");

        if (Global.showRequestParam)
            System.out.println("Req Param: username = " + username);

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (user == null) {
            return notFound();
        } else {
            List<Paper> papers = user.getPapers();
            List<Conference> conferences = new ArrayList<>();

            for (int i = 0; i < papers.size(); i++) {
                if (!conferences.contains(papers.get(i).getConference()))
                    conferences.add(papers.get(i).getConference());
            }

            JsonNode result = Json.toJson(conferences);

            if (Global.showResponse)
                System.out.println("Res: " + result.toString());

            return ok(result);
        }
    }

    public Result assignReviewer() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: assignReviewer");

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        int confId = Integer.parseInt(JsonUtil.getAsString(params, "confId"));
        String username = JsonUtil.getAsString(params, "username");
        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        if (user == null || conference == null)
            return notFound();

        user.addReviewConf(conference);
        user.update();

        return ok("Conference " + confId + " assigned to Reviewer " + username);
    }

    public Result removeReviewer(String confId, String username) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: removeReviewer");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId + " username = " + username);

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        if (user == null || conference == null)
            return notFound();

        // remove review conference
        user.getReviewConfs().remove(conference);
        // remove review papers
        user.getAssignedPapers().removeIf(paper -> paper.getConference().getId() == conference.getId());
        user.getReviewedPapers().removeIf(paper -> paper.getConference().getId() == conference.getId());
        // remove reviews
        user.getReviews().forEach(review -> {
            if (review.getPaper().getConference().getId() == conference.getId()) {
                review.delete();
            }
        });
        user.update();

        return ok("Remove reviewer " + username + " from conference " + confId);
    }

    public Result assignPendingReviewer() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: assignPendingReviewer");

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        int confId = Integer.parseInt(JsonUtil.getAsString(params, "confId"));
        String username = JsonUtil.getAsString(params, "username");
        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        if (user == null || conference == null)
            return notFound();

        user.addPendingReviewConfs(conference);
        user.update();

        return ok("Conference " + confId + " assigned to pending reviewer " + username);
    }

    public Result deletePendingReviewer(String confIdStr, String username) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: assignPendingReviewer");

        /*JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());*/

        int confId = Integer.parseInt(confIdStr);
        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        if (user == null || conference == null)
            return notFound();

        user.deletePendingReviewConfs(conference);
        user.update();

        return ok("Conference " + confId + " delete pending reviewer " + username);
    }

    public Result getPendingReviewers(String confIdStr) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getPendingReviewers");

        int confId = Integer.parseInt(confIdStr);
        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        if (conference == null)
            return notFound();

        JsonNode result = Json.toJson(conference.getPendingReviewers());

        return ok(result);

    }

    public Result assignAdmin() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: assignAdmin");

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        int confId = Integer.parseInt(JsonUtil.getAsString(params, "confId"));
        String username = JsonUtil.getAsString(params, "username");
        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        if (user == null || conference == null)
            return notFound();

        user.addAdminConf(conference);
        user.update();

        return ok("Conference " + confId + " assigned to Admin " + username);
    }

    public Result assignPcChair() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: assignPcChair");

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        int confId = Integer.parseInt(JsonUtil.getAsString(params, "confId"));
        String username = JsonUtil.getAsString(params, "username");
        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        if (user == null || conference == null)
            return notFound();

        conference.setPcChair(user);
        conference.save();

        return ok("Conference " + confId + " assigned to PC chair " + username);
    }

    public Result getConferencesByReviewer(String username) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getConferencesByReviewer");

        if (Global.showRequestParam)
            System.out.println("Req Param: username = " + username);

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (user == null) {
            return notFound();
        }

        List<Conference> confs = Conference.find.all();
        confs.retainAll(user.getReviewConfs());

        ArrayNode result = Json.newArray();
        confs.forEach(conf -> {
            ObjectNode confJo = (ObjectNode) Json.toJson(conf);

            // calculate how many papers are assigned to this reviewer in this conference
            long numPapersAssigned = user.getAssignedPapers().stream().filter(paper -> paper.getConference().getId() == conf.getId()).count();
            confJo.put("numPapersAssigned", numPapersAssigned);

            // calculate how many papers are reviewed by this reviewer in this conference
            long numPapersReviewed = user.getReviewedPapers().stream().filter(paper -> paper.getConference().getId() == conf.getId()).count();
            confJo.put("numPapersReviewed", numPapersReviewed);

            // calculate how many papers are to be reviewed by this reviewer in this conference
            long numPapersLeft = numPapersAssigned - numPapersReviewed;
            confJo.put("numPapersLeft", numPapersLeft);

            result.add(confJo);
        });

        return ok(result);
    }

    /**
     * Add research topic
     *
     * @return
     */
    public Result putResearchTopics() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: postResearchTopic");

        JsonNode params = request().body().asJson();

        int confId = Integer.parseInt(JsonUtil.getAsString(params, "confId"));

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        // Get jsonArray string
        String researchTopics = conference.getResearchTopics() == null ? "[]" : conference.getResearchTopics();

        ObjectMapper mapper = new ObjectMapper();
        int lastId;
        try {
            // Turn string to jsonArray
            JsonNode researchTopicsNode = mapper.readTree(researchTopics);
            ArrayNode researchTopicsArray = (ArrayNode) researchTopicsNode;

            // Generate id
            if (researchTopicsArray.size() == 0) {
                lastId = 0;
            } else {
                // Get the id of latest researchTopic
                String lastIdStr = researchTopicsArray.get(researchTopicsArray.size() - 1).get("id").asText();
                lastId = Integer.parseInt(lastIdStr);
            }

            // Get new json object
            ObjectNode researchTopic = (ObjectNode) params.get("researchTopic");
            researchTopic.put("id", lastId + 1);

            researchTopicsArray.add(researchTopic);
            conference.setResearchTopics(researchTopicsArray.toString());
            conference.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok();
    }

    public Result putCriteria() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: postCriteria");

        JsonNode params = request().body().asJson();

        int confId = Integer.parseInt(JsonUtil.getAsString(params, "confId"));

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        // Get jsonArray string
        String criteria = conference.getCriteria() == null ? "[]" : conference.getCriteria();

        ObjectMapper mapper = new ObjectMapper();
        int lastId;
        try {
            // Turn string to jsonArray
            JsonNode criteriaNode = mapper.readTree(criteria);
            ArrayNode criteriaArray = (ArrayNode) criteriaNode;

            // Generate id
            if (criteriaArray.size() == 0) {
                lastId = 0;
            } else {
                String lastIdStr = criteriaArray.get(criteriaArray.size() - 1).get("id").asText();
                lastId = Integer.parseInt(lastIdStr);
            }

            // Get new json object
            ObjectNode criterion = (ObjectNode) params.get("criterion");
            criterion.put("id", lastId + 1);

            criteriaArray.add(criterion);
            conference.setCriteria(criteriaArray.toString());
            conference.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok();
    }

    public Result putReviewQuestions() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: putReviewQuestion");

        JsonNode params = request().body().asJson();

        int confId = Integer.parseInt(JsonUtil.getAsString(params, "confId"));

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        // Get jsonArray string
        String reviewQuestions = conference.getReviewQuestions() == null ? "[]" : conference.getReviewQuestions();

        ObjectMapper mapper = new ObjectMapper();
        int lastId;
        try {
            // Turn string to jsonArray
            JsonNode reviewQuestionsNode = mapper.readTree(reviewQuestions);
            ArrayNode reviewQuestionsArray = (ArrayNode) reviewQuestionsNode;

            // Generate id
            if (reviewQuestionsArray.size() == 0) {
                lastId = 0;
            } else {
                String lastIdStr = reviewQuestionsArray.get(reviewQuestionsArray.size() - 1).get("id").asText();
                lastId = Integer.parseInt(lastIdStr);
            }

            // Get new json object
            ObjectNode reviewQuestion = (ObjectNode) params.get("reviewQuestion");
            reviewQuestion.put("id", lastId + 1);

            reviewQuestionsArray.add(reviewQuestion);
            conference.setReviewQuestions(reviewQuestionsArray.toString());
            conference.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok();
    }

    public Result putStatusCodes() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: putStatusCode");

        JsonNode params = request().body().asJson();

        int confId = Integer.parseInt(JsonUtil.getAsString(params, "confId"));

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        // Get jsonArray string
        String statusCodes = conference.getStatusCodes() == null ? "[]" : conference.getStatusCodes();

        ObjectMapper mapper = new ObjectMapper();
        int lastId;
        try {
            // Turn string to jsonArray
            JsonNode statusCodesNode = mapper.readTree(statusCodes);
            ArrayNode statusCodesArray = (ArrayNode) statusCodesNode;

            // Generate id
            if (statusCodesArray.size() == 0) {
                lastId = 0;
            } else {
                String lastIdStr = statusCodesArray.get(statusCodesArray.size() - 1).get("id").asText();
                lastId = Integer.parseInt(lastIdStr);
            }

            // Get new json object
            ObjectNode statusCode = (ObjectNode) params.get("statusCode");
            statusCode.put("id", lastId + 1);

            statusCodesArray.add(statusCode);
            conference.setStatusCodes(statusCodesArray.toString());
            conference.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok();
    }

    public Result deleteResearchTopics(String confId, String researchTopicId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: deleteResearchTopics");

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        // Get jsonArray string
        String researchTopics = conference.getResearchTopics() == null ? "[]" : conference.getResearchTopics();

        ObjectMapper mapper = new ObjectMapper();
        try {
            // Turn string to jsonArray
            JsonNode researchTopicsNode = mapper.readTree(researchTopics);
            ArrayNode researchTopicsArray = (ArrayNode) researchTopicsNode;

            int len = researchTopicsArray.size();
            for (int i = 0; i < len; i++) {
                // Remove researchTopic which id is researchTopicId
                if (researchTopicsArray.get(i).get("id").toString().equals(researchTopicId)) {
                    researchTopicsArray.remove(i);
                    break;
                }
            }
            conference.setResearchTopics(researchTopicsArray.toString());
            conference.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok();
    }

    public Result deleteCriteria(String confId, String criterionId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: deleteResearchTopics");

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        // Get jsonArray string
        String criteria = conference.getCriteria() == null ? "[]" : conference.getCriteria();

        ObjectMapper mapper = new ObjectMapper();
        try {
            // Turn string to jsonArray
            JsonNode criteriaNode = mapper.readTree(criteria);
            ArrayNode criteriaArray = (ArrayNode) criteriaNode;

            int len = criteriaArray.size();
            for (int i = 0; i < len; i++) {
                // Remove criterion which id is criterionId
                if (criteriaArray.get(i).get("id").toString().equals(criterionId)) {
                    criteriaArray.remove(i);
                    break;
                }
            }
            conference.setCriteria(criteriaArray.toString());
            conference.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok();
    }

    public Result deleteReviewQuestions(String confId, String reviewQuestionId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: deleteResearchTopics");

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        // Get jsonArray string
        String reviewQuestions = conference.getReviewQuestions() == null ? "[]" : conference.getReviewQuestions();

        ObjectMapper mapper = new ObjectMapper();
        try {
            // Turn string to jsonArray
            JsonNode reviewQuestionsNode = mapper.readTree(reviewQuestions);
            ArrayNode reviewQuestionsArray = (ArrayNode) reviewQuestionsNode;

            int len = reviewQuestionsArray.size();
            for (int i = 0; i < len; i++) {
                // Remove researchTopic which id is researchTopicId
                if (reviewQuestionsArray.get(i).get("id").toString().equals(reviewQuestionId)) {
                    reviewQuestionsArray.remove(i);
                    break;
                }
            }
            conference.setReviewQuestions(reviewQuestionsArray.toString());
            conference.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok();
    }

    public Result deleteStatusCodes(String confId, String statusCodeId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: deleteStatusCodes");

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();

        // Get jsonArray string
        String statusCodes = conference.getStatusCodes() == null ? "[]" : conference.getStatusCodes();

        ObjectMapper mapper = new ObjectMapper();
        try {
            // Turn string to jsonArray
            JsonNode statusCodesNode = mapper.readTree(statusCodes);
            ArrayNode statusCodesArray = (ArrayNode) statusCodesNode;

            int len = statusCodesArray.size();
            for (int i = 0; i < len; i++) {
                // Remove researchTopic which id is researchTopicId
                if (statusCodesArray.get(i).get("id").toString().equals(statusCodeId)) {
                    statusCodesArray.remove(i);
                    break;
                }
            }
            conference.setStatusCodes(statusCodesArray.toString());
            conference.update();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok();
    }

    public Result updateStatus() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: updateStatus");

        JsonNode params = request().body().asJson();
        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        String confId = JsonUtil.getAsString(params, "confId");
        String status = JsonUtil.getAsString(params, "status");

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        conference.setStatus(status);
        conference.update();

        String res = "[Conference] " + confId + " status updated to " + status + ".";

        if (Global.showResponse)
            System.out.println("Res: " + res);

        return ok(res);
    }

    public Result updateSubmissionEmailTemplate() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: updateSubmissionEmailTemplate");

        JsonNode params = request().body().asJson();
        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        String confId = JsonUtil.getAsString(params, "confId");
        String template = JsonUtil.getAsString(params, "template");

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        conference.setSubmissionEmailTemplate(template);
        conference.update();

        String res = "[Conference] " + confId + " submissionEmailTemplate updated.";

        if (Global.showResponse)
            System.out.println("Res: " + res);

        return ok(res);
    }

    public Result updateReminderEmailTemplate() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: updateReminderEmailTemplate");

        JsonNode params = request().body().asJson();
        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        String confId = JsonUtil.getAsString(params, "confId");
        String template = JsonUtil.getAsString(params, "template");

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        conference.setReminderEmailTemplate(template);
        conference.update();

        String res = "[Conference] " + confId + " reminderEmailTemplate updated.";

        if (Global.showResponse)
            System.out.println("Res: " + res);

        return ok(res);
    }

    /**
     * Get conference by ID
     *
     * @param confId
     * @return
     */
    public Result getConfById(String confId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getConfById");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId);

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        if (conference == null) {
            return notFound("Conference not found. confId = " + confId);
        }

        JsonNode result = Json.toJson(conference);

        if (Global.showResponse)
            System.out.println("Res: " + result);

        return ok(result);
    }

    /**
     * Configure conference
     *
     * @return
     */
    public Result configureConf() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getConfById");

        JsonNode params = request().body().asJson();
        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        String confId = JsonUtil.getAsString(params, "confId");
        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        if (conference == null) {
            return notFound("Conference not found. confId = " + confId);
        }

        String acronym = JsonUtil.getAsString(params, "acronym");
        String location = JsonUtil.getAsString(params, "location");
        String date = JsonUtil.getAsString(params, "date");
        String submissionDeadline = JsonUtil.getAsString(params, "submissionDeadline");

        conference.setAcronym(acronym);
        conference.setLocation(location);
        conference.setDate(date);
        conference.setSubmissionDeadline(submissionDeadline);
        conference.update();

        return ok("Conference configured");
    }

    @BodyParser.Of(BodyParser.MultipartFormData.class)
    public Result uploadLogo(String confId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: uploadLogo");

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> doc = body.getFile("logo");
        if (doc == null) {
            return badRequest("Missing file");
        }

        try {
            Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
            if (conference == null) {
                return notFound("Conference not found. confId = " + confId);
            }

            File file = doc.getFile();
            File localFile = new File("public/logo/" + conference.getId());
            FileUtils.deleteQuietly(localFile);
            FileUtils.moveFile(file, localFile);

            conference.setLogoUrl(localFile.getPath());

            conference.update();

            String res = "Logo " + file.getName() + " uploaded";

            if (Global.showResponse)
                System.out.println("Res: " + res);

            return redirect("/landingPage");

        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError(e.toString());
        }
    }

    /**
     * Send emails to authors
     *
     * @return
     */
    public Result sendEmailToAuthors() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: sendEmailToAuthors");

        JsonNode params = request().body().asJson();
        String confId = JsonUtil.getAsString(params, "confId");
        String title = JsonUtil.getAsString(params, "title");
        String content = JsonUtil.getAsString(params, "content");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId + " title = " + title + " content = " + content);

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        if (conference == null) {
            return notFound("Conference not found. confId = " + confId);
        }

        List<Paper> papers = conference.getPapers();
        Set<User> authors = new HashSet<>();
        for (Paper paper : papers) {
            authors.addAll(paper.getAuthors());
        }

        for (User author : authors) {
            String email = author.getProfile().getEmail();
            try {
                gmailService.sendEmail(email, title, content);
            } catch (Exception e) {
                e.printStackTrace();
                return internalServerError(e.toString());
            }
        }

        return ok("Email sent to authors of conference " + confId);
    }

    /**
     * Send reminder to reviewers of this conference
     *
     * @return
     */
    public Result sendReminderToReviewers() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: sendReminderToReviewers");

        JsonNode params = request().body().asJson();
        String confId = JsonUtil.getAsString(params, "confId");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId);

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        if (conference == null) {
            return notFound("Conference not found. confId = " + confId);
        }

        String emailTemplate = conference.getReminderEmailTemplate();

        List<User> reviewers = conference.getReviewers();
        for (User reviewer : reviewers) {
            String username = reviewer.getUsername();
            String email = reviewer.getProfile().getEmail();
            int numPapersNotReviewed = reviewer.getAssignedPapers().size() - reviewer.getReviewedPapers().size();
            String body = emailTemplate
                    .replace("{username}", username)
                    .replace("{numPapersNotReviewed}", numPapersNotReviewed + "");

            try {
                gmailService.sendEmail(email, "Reviewer Reminder", body);
            } catch (Exception e) {
                e.printStackTrace();
                return internalServerError(e.toString());
            }
        }

        return ok("Reviewer reminder sent");
    }

    /**
     * Send reminder to reviewers of this conference who has unreviewed papers
     *
     * @return
     */
    public Result sendReminderToReviewersWithUnreviewedPapers() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: sendReminderToReviewersWithUnreviewedPapers");

        JsonNode params = request().body().asJson();
        String confId = JsonUtil.getAsString(params, "confId");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId);

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        if (conference == null) {
            return notFound("Conference not found. confId = " + confId);
        }

        String emailTemplate = conference.getReminderEmailTemplate();

        List<User> reviewers = conference.getReviewers();
        for (User reviewer : reviewers) {
            int numAssignedPapers = reviewer.getAssignedPapers().size();
            int numReviewedPapers = reviewer.getReviewedPapers().size();
            if (numAssignedPapers == numReviewedPapers) {
                continue;
            }

            String username = reviewer.getUsername();
            String email = reviewer.getProfile().getEmail();
            int numPapersNotReviewed = reviewer.getAssignedPapers().size() - reviewer.getReviewedPapers().size();
            String body = emailTemplate
                    .replace("{username}", username)
                    .replace("{numPapersNotReviewed}", numPapersNotReviewed + "");

            try {
                gmailService.sendEmail(email, "Reviewer Reminder", body);
            } catch (Exception e) {
                e.printStackTrace();
                return internalServerError(e.toString());
            }
        }

        return ok("Reviewer reminder sent");
    }

}
