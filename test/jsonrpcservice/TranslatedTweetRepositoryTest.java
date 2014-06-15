package jsonrpcservice;

import java.net.URL;

import jp.go.nict.langrid.client.jsonrpc.JsonRpcClientFactory;

import org.junit.Assert;
import org.junit.Test;

import sushitter.TranslatedTweetRepositoryService;

public class TranslatedTweetRepositoryTest {
	
	@Test
	public void tweetTest() throws Exception{
		TranslatedTweetRepositoryService service = new JsonRpcClientFactory().create(
				TranslatedTweetRepositoryService.class,
				new URL("http://127.0.0.1:8080/JSONRPCService/jsServices/TranslatedTweetRepository")
				);
		//Assert.assertEquals("Good evening.", service.tweet("Ito Shogo", "2", "こんばんは"));
	}
	
	@Test
	public void getTweetsTest() throws Exception{
		TranslatedTweetRepositoryService service = new JsonRpcClientFactory().create(
				TranslatedTweetRepositoryService.class,
				new URL("http://127.0.0.1:8080/JSONRPCService/jsServices/TranslatedTweetRepository")
				);
		service.tweet("Tom", "1", "猫が好きです");
		service.tweet("Cat", "1", "トムが好きです");
		//Assert.assertEquals("[{\"name\": \"Tom\", \"emotion\": \"1\", \"content_jp\": \"猫が好きです\", \"content_en\": \"I like a cat.\", \"date\": \"06/06/17:05:36\", \"count\": \"1\"},{\"name\": \"Cat\", \"emotion\": \"1\", \"content_jp\": \"トムが好きです\", \"content_en\": \"I like Tom.\", \"date\": \"06/06/17:06:08\", \"count\": \"2\"}]", service.getTweets("1","2"));
	}
}
