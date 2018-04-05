package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import config.Global;
import models.*;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by chuck on 30/3/17.
 */

public class PaperController extends Controller {

    private FormFactory formFactory;

    private GmailService gmailService;

    private final String UPLOAD_BASE_DIR = "upload";

    @Inject
    public PaperController(FormFactory formFactory, GmailService gmailService) {
        this.formFactory = formFactory;
        this.gmailService = gmailService;
    }

    public Result submitPaper() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: submitPaper");

        if (session("username") == null) {
            return unauthorized();
        }

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        Paper paper = setPaperData(new Paper(), params);
        Integer confId = JsonUtil.getAsInt(params, "confId");
        Conference conf = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        paper.setConference(conf);
        paper.setSubmissionDate(new Date());
        paper.setStatus("Transferred");
        paper.save();

        // Send and Email containing paperId
        String subject = "Paper Submitted Successfully";
        String template = conf.getSubmissionEmailTemplate();
        String bodyText = template.replace("{title}", paper.getTitle()).replace("{paperId}", paper.getId() + "");
        try {
            gmailService.sendEmail(paper.getEmailOfContactAuthor(), subject, bodyText);
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError(e.toString());
        }

        if (Global.showResponse)
            System.out.println("Res: " + bodyText);

        return ok(bodyText);
    }

    public Result updatePaper() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: updatePaper");

        if (session("username") == null) {
            return unauthorized();
        }

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        int paperId = JsonUtil.getAsInt(params, "paperId");
        Paper paper = Ebean.find(Paper.class).where().eq("id", paperId).findUnique();
        if (paper == null) {
            return notFound();
        }
        setPaperData(paper, params);
        paper.update();

        String res = "Paper Updated Successfully";

        if (Global.showResponse)
            System.out.println("Res: " + res);

        return ok(res);
    }

    private Paper setPaperData(Paper paper, JsonNode params) {
        String title = JsonUtil.getAsString(params, "title");
        String email = JsonUtil.getAsString(params, "email");
        String awardCandidate = JsonUtil.getAsString(params, "awardCandidate");
        String studentVolunteer = JsonUtil.getAsString(params, "studentVolunteer");
        String abstractText = JsonUtil.getAsString(params, "abstractText");
        String topic = JsonUtil.getAsString(params, "topic");

        ArrayNode authorsJa = (ArrayNode) params.get("authors");
        Set<String> emails = new HashSet<>(authorsJa.size());
        for (int i = 0; i < authorsJa.size(); i++) {
            ObjectNode authorJo = (ObjectNode) authorsJa.get(i);
            String authorEmail = JsonUtil.getAsString(authorJo, "authorEmail");
            emails.add(authorEmail);
        }

        String otherAuthors = JsonUtil.getAsString(params, "otherAuthors");

        paper.setTitle(title);
        paper.setEmailOfContactAuthor(email);
        paper.setAbstractText(abstractText);
        paper.setOtherAuthors(otherAuthors);
        paper.setTopic(topic);
        paper.setAwardCandidate(awardCandidate);
        paper.setVolunteer(studentVolunteer);

        List<User> authors = new ArrayList<>();
        for (Profile profile : Profile.find.where().in("email", emails).findList()) {
            authors.add(profile.getUser());
        }
        paper.setAuthors(authors);

        return paper;
    }

    public Result getPapersByConfIdAndUsername(String confId, String username) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getPapersByConfIdAndUsername");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId + ", username = " + username);

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (user == null) {
            return notFound();
        }

        List<Paper> papers = new ArrayList<>();
        for (Paper paper : user.getPapers()) {
            if (paper.getConference().getId() == Integer.parseInt(confId)) {
                papers.add(paper);
            }
        }

        // format result JSON
        JsonNode result = Json.toJson(papers);
        ArrayNode resultJa = (ArrayNode) result;
        for (int i = 0; i < resultJa.size(); i++) {
            ArrayNode authorsJa = resultJa.arrayNode();
            Paper paper = papers.get(i);
            for (User author : paper.getAuthors()) {
                Profile authorProfile = author.getProfile();
                ObjectNode authorJo = resultJa.objectNode();
                authorJo.put("username", author.getUsername());
                authorJo.put("authorFirstName", authorProfile.getFirstName());
                authorJo.put("authorLastName", authorProfile.getLastName());
                authorJo.put("authorAffiliation", authorProfile.getAffiliation());
                authorJo.put("authorEmail", authorProfile.getEmail());
                authorsJa.add(authorJo);
            }

            ObjectNode paperJo = (ObjectNode) resultJa.get(i);
            paperJo.set("authors", authorsJa);
        }

        if (Global.showResponse)
            System.out.println("Res: " + result);

        return ok(result);
    }

    public Result getPapersByConfId(String confId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getPapersByConfId");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId);

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        if (conference == null) {
            return notFound();
        }

        List<Paper> papers = conference.getPapers();

        // format result JSON
        JsonNode result = Json.toJson(papers);
        ArrayNode resultJa = (ArrayNode) result;
        for (int i = 0; i < resultJa.size(); i++) {
            ArrayNode authorsJa = resultJa.arrayNode();
            ArrayNode reviewersJa = resultJa.arrayNode();

            Paper paper = papers.get(i);
            for (User author : paper.getAuthors()) {
                Profile authorProfile = author.getProfile();
                ObjectNode authorJo = resultJa.objectNode();
                authorJo.put("username", author.getUsername());
                authorJo.put("authorFirstName", authorProfile.getFirstName());
                authorJo.put("authorLastName", authorProfile.getLastName());
                authorJo.put("authorAffiliation", authorProfile.getAffiliation());
                authorJo.put("authorEmail", authorProfile.getEmail());
                authorsJa.add(authorJo);
            }
            for (User reviewer : paper.getReviewers()) {
                reviewersJa.add(reviewer.getUsername());
            }

            ObjectNode paperJo = (ObjectNode) resultJa.get(i);
            paperJo.set("authors", authorsJa);

            paperJo.set("reviewers", reviewersJa);
        }

        if (Global.showResponse)
            System.out.println("Res: " + result);

        return ok(result);
    }

    public Result getPaperById(String paperId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getPaperById");

        if (Global.showRequestParam)
            System.out.println("Req Param: PaperId = " + paperId);

        Paper paper = Ebean.find(Paper.class).where().eq("id", Integer.parseInt(paperId)).findUnique();
        if (paper == null) {
            return notFound();
        }

        List<User> reviewers = paper.getReviewers();
        List<String> reviewersNames = new ArrayList<>();

        for(User reviewer: reviewers) {
            reviewersNames.add(reviewer.getUsername());
        }




        // format result JSON
        JsonNode result = Json.toJson(paper);
        ObjectNode resultJo = (ObjectNode) result;
        ArrayNode authorsJa = resultJo.arrayNode();
        for (User author : paper.getAuthors()) {
            Profile authorProfile = author.getProfile();
            ObjectNode authorJo = resultJo.objectNode();
            authorJo.put("username", author.getUsername());
            authorJo.put("authorFirstName", authorProfile.getFirstName());
            authorJo.put("authorLastName", authorProfile.getLastName());
            authorJo.put("authorAffiliation", authorProfile.getAffiliation());
            authorJo.put("authorEmail", authorProfile.getEmail());
            authorsJa.add(authorJo);
        }
        resultJo.set("authors", authorsJa);

        ArrayNode reviewersJa = resultJo.arrayNode();
        for (String name: reviewersNames) {
//            ObjectNode reviewerJo = resultJo.objectNode();
//            reviewerJo.put("username", name);
            reviewersJa.add(name);
        }
        resultJo.set("reviewers", reviewersJa);


        if (Global.showResponse)
            System.out.println("Res: " + result);

        return ok(result);
    }

    @BodyParser.Of(BodyParser.MultipartFormData.class)
    public Result uploadPaper(String paperId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: uploadPaper");

        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> doc = body.getFile("doc");
        if (doc == null) {
            return badRequest("Missing file");
        }

        try {
            Paper paper = Ebean.find(Paper.class).where().eq("id", Integer.valueOf(paperId)).findUnique();
            if (paper == null) {
                return notFound("Paper not found. paperId = " + paperId);
            }

            File file = doc.getFile();
            File localFile = new File(UPLOAD_BASE_DIR, paperId);
            FileUtils.deleteQuietly(localFile);
            FileUtils.moveFile(file, localFile);

            paper.setFileUrl(localFile.getAbsolutePath());
            paper.setFileSize(String.format("%.1f(KB)", ((double) localFile.length()) / 1000));
            paper.setFileName(doc.getFilename());
            paper.save();

            String res = "[Paper] " + paperId + " file uploaded";

            if (Global.showResponse)
                System.out.println("Res: " + res);

            return redirect("/landingPage");

        } catch (IOException e) {
            e.printStackTrace();
            return internalServerError(e.toString());
        }
    }

    public Result assignReviewer() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: assignPaper");

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        int paperId = Integer.parseInt(JsonUtil.getAsString(params, "paperId"));
        String username = JsonUtil.getAsString(params, "username");

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        Paper paper = Ebean.find(Paper.class).where().eq("id", paperId).findUnique();

        if (paper.getStatus().equals("Transferred"))
            paper.setStatus("Moved");
        paper.update();

        user.addAssignedPaper(paper);
        user.update();

        return ok("Paper " + paperId + " assigned to User " + username);
    }

    public Result removeReviewer() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: removeReviewer");

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        int paperId = Integer.parseInt(JsonUtil.getAsString(params, "paperId"));
        String username = JsonUtil.getAsString(params, "username");

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        Paper paper = Ebean.find(Paper.class).where().eq("id", paperId).findUnique();

        // remove paper
        user.getAssignedPapers().remove(paper);
        user.getReviewedPapers().remove(paper);
        // remove review
        user.getReviews().forEach(review -> {
            if (review.getPaper().getId() == paper.getId()) {
                review.delete();
            }
        });
        user.update();

        return ok("Paper " + paperId + " removed reviewer: " + username);
    }

    public Result download(String paperId) {

        if (Global.showEnterControllerFunction)
            System.out.println("Func: download");

        if (Global.showRequestParam)
            System.out.println("Req Param: paperId = " + paperId);

        Paper paper = Ebean.find(Paper.class).where().eq("id", Integer.parseInt(paperId)).findUnique();
        if (paper == null) {
            return notFound();
        }

        if (Global.showResponse)
            System.out.println("Res: " + paper.getFileUrl());

        if (paper.getFileUrl() == null) {
            return ok("hakan");
        }
        File resultFile = new File(paper.getFileUrl());
        if (!resultFile.exists()) {
            return notFound(resultFile);
        }

        return ok(resultFile, paper.getFileName()).withHeader("Content-Disposition", "attachment");
    }

    public Result display(String paperId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: display");

        if (Global.showRequestParam)
            System.out.println("Req Param: paperId = " + paperId);

        Paper paper = Ebean.find(Paper.class).where().eq("id", Integer.parseInt(paperId)).findUnique();
        if (paper == null) {
            return notFound();
        }

        if (Global.showResponse)
            System.out.println("Res: " + paper.getFileUrl());

        if (paper.getFileUrl() == null) {
            return notFound("Paper not uploaded");
        }
        File resultFile = new File(paper.getFileUrl());
        if (!resultFile.exists()) {
            return notFound(resultFile);
        }

        return ok(resultFile, paper.getFileName());
    }

    public Result getReviewingPapers(String confId, String username) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getReviewingPapers");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId + " username = " + username);

        int id = Integer.parseInt(confId);
        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        List<Paper> papers = user.getAssignedPapers();
        List<Paper> reviewingPapers = new ArrayList<>();
        for (Paper p : papers) {
            if (p.getConference().getId() == id) {
                reviewingPapers.add(p);
            }
        }
        JsonNode result = Json.toJson(reviewingPapers);

        if (Global.showResponse)
            System.out.println("Res: " + result.toString());

        return ok(result);
    }

    public Result genExcel(String confId, String type) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: genExcel");

        if (Global.showRequestParam)
            System.out.println("Req Param: confId = " + confId + " type = " + type);

        Conference conference = Ebean.find(Conference.class).where().eq("id", confId).findUnique();
        if (conference == null) {
            return notFound();
        }
        List<Paper> allPapers = conference.getPapers();
        List<Paper> requiredPapers = new ArrayList<>();
        if (type.equals("submitted")) {
            requiredPapers = allPapers;
        } else {
            for (Paper paper : allPapers) {
                if (paper.getStatus().equals("Accept"))
                    requiredPapers.add(paper);
            }
        }

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Papers");
        int rowNum = 0;
        HSSFRow row = null;
        HSSFCell cell = null;
        for (int i = 0; i < requiredPapers.size(); i++) {
            Paper paper = requiredPapers.get(i);

            row = sheet.createRow(rowNum);
            cell = row.createCell(0);
            cell.setCellValue("Paper " + (i + 1));
            rowNum++;

            row = sheet.createRow(rowNum);
            cell = row.createCell(0);
            cell.setCellValue("Title:");
            cell = row.createCell(1);
            cell.setCellValue(paper.getTitle());
            rowNum++;

            row = sheet.createRow(rowNum);
            cell = row.createCell(0);
            cell.setCellValue("Status:");
            cell = row.createCell(1);
            cell.setCellValue(paper.getStatus());
            rowNum++;

            row = sheet.createRow(rowNum);
            cell = row.createCell(0);
            cell.setCellValue("Reviews");
            cell = row.createCell(1);
            cell.setCellValue("Reviewer");
            cell = row.createCell(2);
            cell.setCellValue("Content");
            rowNum++;

            for (int j = 0; j < paper.getReviews().size(); j++) {
                Review review = paper.getReviews().get(j);
                row = sheet.createRow(rowNum);
                cell = row.createCell(0);
                cell.setCellValue(j + 1);
                cell = row.createCell(1);
                cell.setCellValue(review.getReviewer().getUsername());
                cell = row.createCell(2);
                cell.setCellValue(review.getContent());
                rowNum++;
            }
            rowNum++;
        }

        String filePath = "excel/";
        String fileName = "Papers.xls";
        File file = new File(filePath + fileName);
        try {
            if (file.exists())
                file.delete();
            FileOutputStream fileOut = new FileOutputStream(file);
            wb.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(file, filePath + fileName).withHeader("Content-Disposition", "attachment;filename=" + fileName);
    }

    public Result setStatus() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: setStatus");

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        int paperId = Integer.parseInt(JsonUtil.getAsString(params, "paperId"));
        String status = JsonUtil.getAsString(params, "status");

        Paper paper = Ebean.find(Paper.class).where().eq("id", paperId).findUnique();
        if(paper == null) {
            return notFound();
        }

        paper.setStatus(status);
        paper.update();
        return ok("Paper status updated");

    }

}
