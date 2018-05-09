package recommender.hackathon.mahout;

import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;

public interface BasicRecommender {
	
	UserBasedRecommender getRecommender(DataModel dataModel);
	
}
