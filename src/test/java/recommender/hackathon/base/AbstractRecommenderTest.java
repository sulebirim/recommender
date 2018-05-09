package recommender.hackathon.base;

import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import recommender.hackathon.RecommenderApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes={RecommenderApplication.class})
public abstract class AbstractRecommenderTest {

}
