package recommender.hackathon.recommender;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import recommender.hackathon.base.AbstractRecommenderTest;
import recommender.hackathon.exceptions.FileModelCreationException;
import recommender.hackathon.mahout.BasicRecommender;

public class UserBasedRecommenderTests extends AbstractRecommenderTest {

	private static final String DATA_SOURCE = "/Users/n.kazarian/Projects/hackathon/data/result.csv";
	
	@Autowired
	BasicRecommender builder;

	DataModel model = null;

	@Before
	public void setup() {
		try {
			model = new FileDataModel(new File(DATA_SOURCE));
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileModelCreationException("Could not create model from requested file. Check form and path");
		}
	}

	@Test
	public void testLala() throws Exception {
		UserBasedRecommender recommender = builder.getRecommender(model);

		// users
		for (int i = 5; i >= 0; i--) {
			int userId = ((i * 1000) + 1);
			List<RecommendedItem> recommendations = recommender.recommend(userId, 10);
			System.out.println("==== user " + userId + " ====");
			for (RecommendedItem rec : recommendations) {
				System.out.println(rec);
			}
		}
	}

	@Test
	public void evaluate() throws Exception {
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderBuilder builder = new RecommenderBuilder() {

			@Override
			public Recommender buildRecommender(DataModel arg0) throws TasteException {
				return UserBasedRecommenderTests.this.builder.getRecommender(arg0);
			}
		};
		double result = evaluator.evaluate(builder, null, model, 0.8, 1.0);
		System.out.println(result);
	}
}
