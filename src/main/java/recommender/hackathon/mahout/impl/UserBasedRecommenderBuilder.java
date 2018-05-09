package recommender.hackathon.mahout.impl;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import recommender.hackathon.exceptions.FileModelCreationException;
import recommender.hackathon.mahout.BasicRecommender;

@Component
public class UserBasedRecommenderBuilder implements BasicRecommender {

	protected static final Double THRESHOLD = 0.3;

	@Value("${application.data-file}")
	private String dataFile;
	
	private GenericUserBasedRecommender recommender;
	
	private FileDataModel createDataModel(String dataFile) {
		try {
			return new FileDataModel(new File(dataFile == null ? this.dataFile : dataFile));
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileModelCreationException("Could not create model from requested file. Check form and path");
		}
	}
	
	@Override
	public synchronized UserBasedRecommender getRecommender(DataModel model) {
		if (recommender == null) {
			if (model == null) {
				model = createDataModel(dataFile);
			}
			PearsonCorrelationSimilarity similarity;
			try {
				similarity = new PearsonCorrelationSimilarity(model);
			} catch (TasteException e) {
				throw new RuntimeException(e);
			}
			ThresholdUserNeighborhood tun = new ThresholdUserNeighborhood(UserBasedRecommenderBuilder.THRESHOLD, similarity, model);
			this.recommender = new GenericUserBasedRecommender(model, tun, similarity);
		}
		return recommender;
	}

	public synchronized UserBasedRecommender refresh(DataModel model) {
		recommender = null;
		return getRecommender(model);
	}
}
