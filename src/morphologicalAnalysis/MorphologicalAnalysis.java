package morphologicalAnalysis;

import java.util.List;
import java.util.ArrayList;

import org.atilika.kuromoji.Token;
import org.atilika.kuromoji.Tokenizer;

public class MorphologicalAnalysis {
	public static List<String> execute(String sentence){
		Tokenizer tokenizer = Tokenizer.builder().build();
		List<Token> result = tokenizer.tokenize(sentence);
		List<String> words = new ArrayList<String>();
		for (Token token : result) {
			String[] features = token.getAllFeaturesArray();
			String parts = features[0];
			String origin = features[6];
			if((parts.equals("形容詞") || parts.equals("名詞") || parts.equals("動詞")) && !origin.equals("*")){
				words.add(origin);
			}
		}
		return words;
	}
}
