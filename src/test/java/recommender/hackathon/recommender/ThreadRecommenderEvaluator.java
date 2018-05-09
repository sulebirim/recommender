package recommender.hackathon.recommender;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.google.common.collect.Lists;

public class ThreadRecommenderEvaluator {
	
	private static final String DATA_SOURCE = "/Users/n.kazarian/Projects/hackathon/data/result.csv";
	
	public static void main(String[] args) throws IOException, TasteException {
		DataModel model = new GenericDataModel(GenericDataModel.toDataMap(new FileDataModel(new File(DATA_SOURCE))));

		List<UserSimilarity> similarities = Lists.newArrayList(new LogLikelihoodSimilarity(model),
				new PearsonCorrelationSimilarity(model, Weighting.UNWEIGHTED),
				new PearsonCorrelationSimilarity(model, Weighting.WEIGHTED),
				new EuclideanDistanceSimilarity(model, Weighting.UNWEIGHTED),
				new EuclideanDistanceSimilarity(model, Weighting.WEIGHTED), 
				new TanimotoCoefficientSimilarity(model),
				new SpearmanCorrelationSimilarity(model), new CityBlockSimilarity(model));

		for (final UserSimilarity similarity : similarities) {
			RecommenderBuilder recommenderBuilder = new RecommenderBuilder() {
				public Recommender buildRecommender(DataModel model) throws TasteException {
					// UserNeighborhood neighborhood = new
					// NearestNUserNeighborhood(50, similarity, model);
					// similarity.setPreferenceInferrer(new
					// AveragingPreferenceInferrer(model));
					UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.8, similarity, model);
					return new GenericUserBasedRecommender(model, neighborhood, similarity);
				}
			};

			evaluate(similarity, recommenderBuilder, model);
		}
	}

	public static void evaluate(UserSimilarity similarity, RecommenderBuilder builder, DataModel model)
			throws TasteException {
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		RecommenderIRStatsEvaluator evaluator1 = new GenericRecommenderIRStatsEvaluator();

		double score = evaluator.evaluate(builder, null, model, .8, .95);
		System.out.println(similarity.getClass().getName());
		System.out.println(score);
	}
}