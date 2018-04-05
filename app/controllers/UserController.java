package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.Global;
import models.Conference;
import models.Paper;
import models.Profile;
import models.User;
import org.apache.commons.lang3.RandomStringUtils;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.GmailService;
import util.JsonUtil;

import javax.inject.Inject;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserController extends Controller {

    private FormFactory formFactory;

    private GmailService gmailService;

    @Inject
    public UserController(FormFactory formFactory, GmailService gmailService) {
        this.formFactory = formFactory;
        this.gmailService = gmailService;
    }

    public Result getCurrentUsername() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getCurrentUsername");

        String username = session("username");
        if (username == null) {
            return unauthorized();
        }
        ObjectNode result = Json.newObject().put("username", username);

        if (Global.showResponse)
            System.out.println("Res: " + result.toString());

        return ok(result);
    }

    public Result register() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: register");

        JsonNode params = request().body().asJson();
        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        String username = JsonUtil.getAsString(params, "username");
        String password = JsonUtil.getAsString(params, "password");
        String securityQuestion = JsonUtil.getAsString(params, "securityQuestion");
        String answer = JsonUtil.getAsString(params, "answer");
        String backupEmail = JsonUtil.getAsString(params, "backupEmail");

        String title = JsonUtil.getAsString(params, "title");
        String researchAreas = JsonUtil.getAsString(params, "researchAreas");
        String firstName = JsonUtil.getAsString(params, "firstName");
        String lastName = JsonUtil.getAsString(params, "lastName");
        String position = JsonUtil.getAsString(params, "position");
        String affiliation = JsonUtil.getAsString(params, "affiliation");
        String email = JsonUtil.getAsString(params, "email");
        String phone = JsonUtil.getAsString(params, "phone");
        String fax = JsonUtil.getAsString(params, "fax");
        String address = JsonUtil.getAsString(params, "address");
        String city = JsonUtil.getAsString(params, "city");
        String country = JsonUtil.getAsString(params, "country");
        String zipCode = JsonUtil.getAsString(params, "zipCode");
        String comments = JsonUtil.getAsString(params, "comments");

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setSecurityQuestion(securityQuestion);
        user.setAnswer(answer);
        user.setBackupEmail(backupEmail);

        Profile profile = new Profile();
        profile.setTitle(title);
        profile.setResearchAreas(researchAreas);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setPosition(position);
        profile.setAffiliation(affiliation);
        profile.setEmail(email);
        profile.setPhone(phone);
        profile.setFax(fax);
        profile.setAddress(address);
        profile.setCity(city);
        profile.setCountry(country);
        profile.setZipCode(zipCode);
        profile.setComments(comments);

        user.setProfile(profile);
        profile.setUser(user);
        user.save();
        profile.save();

        session("username", username);

        String res = "[User] " + username + " registered.";

        if (Global.showResponse)
            System.out.println("Res: " + res);

        return created(res);
    }

    public Result getProfileByUsername(String username) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getProfileByUsername");

        if (Global.showRequestParam)
            System.out.println("Req Param: username = " + username);

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (user == null) {
            return notFound();
        }

        Profile profile = user.getProfile();

        ObjectNode result = Json.newObject();
        result.put("securityQuestion", user.getSecurityQuestion());
        result.setAll((ObjectNode) Json.toJson(profile));

        if (Global.showResponse)
            System.out.println("Res: " + result.toString());

        return ok(result);
    }

    public Result updateProfile() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: updateProfile");

        JsonNode params = request().body().asJson();
        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        String username = JsonUtil.getAsString(params, "username");
        String title = JsonUtil.getAsString(params, "title");
        String researchAreas = JsonUtil.getAsString(params, "researchAreas");
        String firstName = JsonUtil.getAsString(params, "firstName");
        String lastName = JsonUtil.getAsString(params, "lastName");
        String position = JsonUtil.getAsString(params, "position");
        String affiliation = JsonUtil.getAsString(params, "affiliation");
        String email = JsonUtil.getAsString(params, "email");
        String phone = JsonUtil.getAsString(params, "phone");
        String fax = JsonUtil.getAsString(params, "fax");
        String address = JsonUtil.getAsString(params, "address");
        String city = JsonUtil.getAsString(params, "city");
        String country = JsonUtil.getAsString(params, "country");
        String zipCode = JsonUtil.getAsString(params, "zipCode");
        String comments = JsonUtil.getAsString(params, "comments");

        if (!session("username").equals(username)) {
            return unauthorized();
        }

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        Profile profile = user.getProfile();
        profile.setTitle(title);
        profile.setResearchAreas(researchAreas);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setPosition(position);
        profile.setAffiliation(affiliation);
        profile.setEmail(email);
        profile.setPhone(phone);
        profile.setFax(fax);
        profile.setAddress(address);
        profile.setCity(city);
        profile.setCountry(country);
        profile.setZipCode(zipCode);
        profile.setComments(comments);

        profile.update();
        user.update();

        String res = "[User] " + username + " profile updated.";

        if (Global.showResponse)
            System.out.println("Res: " + res);

        return ok(res);
    }

    public Result login() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: login");

        JsonNode params = request().body().asJson();
        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        String username = JsonUtil.getAsString(params, "username");
        String password = JsonUtil.getAsString(params, "password");

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (user == null) {
            return notFound();
        }

        String userPassword = user.getPassword();
        if (userPassword.equals(password)) {
            session("username", username);
            String res = "[User] " + username + " logged in.";

            if (Global.showResponse)
                System.out.println("Res: " + res);

            return ok(res);
        } else {
            return unauthorized();
        }
    }

    public Result logout() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: logout");

        String username = session("username");
        session().clear();

        String res = "[User] " + username + " logged out.";

        if (Global.showResponse)
            System.out.println("Res: " + res);

        return ok(res);
    }

    public Result resetPassword() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: resetPassword");

        JsonNode params = request().body().asJson();
        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        String username = JsonUtil.getAsString(params, "username");
        String answer = JsonUtil.getAsString(params, "answer");
        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (user == null) {
            return notFound();
        }

        String securityAnswer = user.getAnswer();
        if (securityAnswer.equals(answer)) {
            try {
                // Generate random password
                String newPassword = RandomStringUtils.randomAlphanumeric(6);
                byte[] btInput = newPassword.getBytes("utf-8");
                MessageDigest mdInst = MessageDigest.getInstance("MD5");
                mdInst.update(btInput);
                byte[] md = mdInst.digest();
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < md.length; i++) {
                    int val = ((int) md[i]) & 0xff;
                    if (val < 16) {
                        sb.append("0");
                    }
                    sb.append(Integer.toHexString(val));
                }
                String md5NewPassword = sb.toString();
                user.setPassword(md5NewPassword);
                user.save();
                System.out.println("[User] " + username + "'s password reset to: " + newPassword);

                // Send email
                String to = user.getBackupEmail();
                String subject = "Password Reset";
                String bodyText = "Your password has been reset to: " + newPassword;
                gmailService.sendEmail(to, subject, bodyText);

                String res = "[User] " + username + " password reset";

                if (Global.showResponse)
                    System.out.println("Res: " + res);

                return ok(res);

            } catch (Exception e) {
                return internalServerError(e.toString());
            }
        } else {
            return badRequest();
        }
    }

    public Result getAuthorsByConfId(String confId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getAuthorsByConfId");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId);

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        if (conference == null) {
            return notFound();
        }

        List<Paper> papers = conference.getPapers();
        List<User> authors = new ArrayList<>();
        for (Paper paper : papers) {
            List<User> authorsInPaper = paper.getAuthors();
            for (User author : authorsInPaper) {
                if (!authors.contains(author))
                    authors.add(author);
            }
        }

        ArrayNode result = Json.newArray();

        for (User author : authors) {
            ObjectNode authorNode = Json.newObject();
            authorNode.put("username", author.getUsername());
            authorNode.put("affiliation", author.getProfile().getAffiliation());
            List<Paper> papersInAuthorByConfId = new ArrayList<>();
            for (Paper paper : author.getPapers()) {
                if (paper.getConference().getId() == Integer.parseInt(confId)) {
                    papersInAuthorByConfId.add(paper);
                }
            }
            ArrayNode papersArrayNode = Json.newArray();
            for (Paper paper : papersInAuthorByConfId) {
                ObjectNode paperNode = Json.newObject();
                paperNode.put("id", paper.getId());
                paperNode.put("title", paper.getTitle());
                papersArrayNode.add(paperNode);
            }
            authorNode.put("papers", papersArrayNode);
            result.add(authorNode);
        }

        if (Global.showResponse)
            System.out.println("Res: " + result.toString());

        return ok(result);
    }

    public Result getReviewersByConfId(String confId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getReviewersByConfId");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId);

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        if (conference == null) {
            return notFound();
        }

        List<Paper> papers = conference.getPapers();
        Set<User> reviewers = new HashSet<>();
        for (Paper paper : papers) {
            reviewers.addAll(paper.getReviewers());
        }

        ArrayNode result = Json.newArray();

        for (User reviewer : reviewers) {
            ObjectNode reviewerNode = Json.newObject();
            reviewerNode.put("username", reviewer.getUsername());

            ArrayNode reviewedPapersJa = Json.newArray();
            reviewer.getReviewedPapers().forEach(paper -> {
                reviewedPapersJa.add(paper.getId());
            });
            reviewerNode.set("reviewedPapers", reviewedPapersJa);

            ArrayNode notReviewedPapersJa = Json.newArray();
            List<Paper> notReviewedPapers = new ArrayList<>(reviewer.getAssignedPapers());
            notReviewedPapers.removeAll(reviewer.getReviewedPapers());
            notReviewedPapers.forEach(paper -> {
                notReviewedPapersJa.add(paper.getId());
            });
            reviewerNode.set("notReviewedPapersJa", notReviewedPapersJa);

            result.add(reviewerNode);
        }

        if (Global.showResponse)
            System.out.println("Res: " + result.toString());

        return ok(result);
    }
}
