package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;
import config.Global;
import models.Paper;
import models.Review;
import models.User;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import util.JsonUtil;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by alviss_reimu on 4/12/17.
 */
public class ReviewController extends Controller {
    private FormFactory formFactory;

    @Inject
    public ReviewController(FormFactory formFactory) {
        this.formFactory = formFactory;
    }

    public Result submitReview() {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: submitReview");

        JsonNode params = request().body().asJson();

        if (Global.showRequestParam)
            System.out.println("Req Param: " + params.toString());

        String paperId = JsonUtil.getAsString(params, "paperId");
        String username = JsonUtil.getAsString(params, "username");
        String content = JsonUtil.getAsString(params, "content");

        Paper paper = Ebean.find(Paper.class).where().eq("id", paperId).findUnique();
        if (paper == null) {
            return notFound();
        }

        User reviewer = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (reviewer == null) {
            return notFound();
        }

        Review review = setReviewData(new Review(), params);

        for (int i = 0; i < paper.getReviews().size(); i++) {
            Review r = paper.getReviews().get(i);
            if (r.getReviewer().getUsername().equals(username)) {
                r.setContent(content);
                r.update();
                return ok();
            }
        }

        review.save();
        //as long as a review is created, this paper would be added to this reviewer's reviewedPapers
        reviewer.addReviewedPaper(paper);
        reviewer.update();

        return created();
    }

    public Result getReviewByPaperIdAndUsername(String paperId, String username) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getReviewByPaperIdAndUsername");

        if (Global.showRequestParam)
            System.out.println("Req Param: paperId = " + paperId + " username = " + username);

        User user = Ebean.find(User.class).where().eq("username", username).findUnique();
        if (user == null) {
            return notFound();
        }

        Paper paper = Ebean.find(Paper.class).where().eq("id", paperId).findUnique();
        if (paper == null) {
            return notFound();
        }

        JsonNode result = null;

        for (Review review : paper.getReviews()) {
            if (review.getReviewer().getUsername().equals(username)) {
                result = Json.toJson(review);
                break;
            }
        }

        if (result == null)
            return notFound();

        if (Global.showResponse)
            System.out.println("Res: " + result);

        return ok(result);
    }

    public Result getReviewsByPaperId(String paperId) {
        if (Global.showEnterControllerFunction)
            System.out.println("Func: getReviewsByPaperId");

        if (Global.showRequestParam)
            System.out.println("Req Param: paperId = " + paperId);

        Paper paper = Ebean.find(Paper.class).where().eq("id", paperId).findUnique();
        if (paper == null) {
            return notFound();
        }


        List<Review> reviews = paper.getReviews();

        if (Global.showResponse)
            System.out.println("Res: " + reviews);

        return ok(Json.toJson(reviews));
    }

    private Review setReviewData(Review review, JsonNode params) {
        String paperId = JsonUtil.getAsString(params, "paperId");
        String username = JsonUtil.getAsString(params, "username");
        String content = JsonUtil.getAsString(params, "content");

        Paper paper = Ebean.find(Paper.class).where().eq("id", paperId).findUnique();
        review.setPaper(paper);

        User reviewer = Ebean.find(User.class).where().eq("username", username).findUnique();
        review.setReviewer(reviewer);

        review.setContent(content);

        return review;
    }

}
