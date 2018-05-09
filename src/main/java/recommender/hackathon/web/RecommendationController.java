package recommender.hackathon.web;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import recommender.hackathon.mahout.impl.ItemBasedRecommenderBuilder;
import recommender.hackathon.mahout.impl.UserBasedRecommenderBuilder;

@RestController
@RequestMapping(value="/recommend")
public class RecommendationController {

	@Autowired
	UserBasedRecommenderBuilder userRecommenderBuilder;
	
	@Autowired
	ItemBasedRecommenderBuilder itemRecommenderBuilder;
	
	@RequestMapping(value="/receipe",method=RequestMethod.GET)
	public List<Long> recommendReceipe(@RequestParam(required=true,value="userId")Integer userId,@RequestParam(required=true,value="nor")Integer nor){
		
		try {
			List<RecommendedItem> recommendations = userRecommenderBuilder.getRecommender(null).recommend(userId, nor);
			return recommendations.stream().map(c->c.getItemID()).collect(Collectors.toList());
		} catch (TasteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="/similarItems",method=RequestMethod.GET)
	public List<Long> recommendSimilarItems(@RequestParam(required=true,value="itemId")Integer itemId,@RequestParam(required=true,value="nor")Integer nor){
		
		try {
			List<RecommendedItem> recommendations = itemRecommenderBuilder.getRecommender(null).mostSimilarItems(itemId, nor);
			return recommendations.stream().map(c->c.getItemID()).collect(Collectors.toList());
		} catch (TasteException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value="/refresh",method=RequestMethod.GET)
	public void refresh(){
		itemRecommenderBuilder.refresh(null);
		userRecommenderBuilder.refresh(null);
	}
}
