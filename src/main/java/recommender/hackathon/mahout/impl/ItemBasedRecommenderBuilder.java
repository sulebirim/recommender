package recommender.hackathon.mahout.impl;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import recommender.hackathon.exceptions.FileModelCreationException;

@Component
public class ItemBasedRecommenderBuilder {

	@Value("${application.data-file}")
	private String dataFile;

	protected static final Double THRESHOLD = 1.0;

	private GenericItemBasedRecommender recommender;

	private FileDataModel createDataModel(String dataFile) {
		try {
			return new FileDataModel(new File(dataFile == null ? this.dataFile : dataFile));
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileModelCreationException("Could not create model from requested file. Check form and path");
		}
	}

	public synchronized ItemBasedRecommender getRecommender(DataModel model) {
		if (recommender == null) {
			if (model == null) {
				model = createDataModel(dataFile);
			}
			ItemSimilarity similarity = new LogLikelihoodSimilarity(model);
			this.recommender = new GenericItemBasedRecommender(model, similarity);
		}
		return recommender;
	}

	public synchronized ItemBasedRecommender refresh(DataModel model) {
		recommender = null;
		return getRecommender(model);
	}
}
