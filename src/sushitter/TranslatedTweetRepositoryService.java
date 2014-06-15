package sushitter;

import jp.go.nict.langrid.commons.rpc.intf.Description;
import jp.go.nict.langrid.commons.rpc.intf.Parameter;

public interface TranslatedTweetRepositoryService {
	@Description("挨拶を返すメソッド")
	String tweet(@Parameter(name="content") String name, String emotion, String content);
	String getTweets(@Parameter(name="content") String startNm, String getNm);
	void learn();
	void testTweet();
}
