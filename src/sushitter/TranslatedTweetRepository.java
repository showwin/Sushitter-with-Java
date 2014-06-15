package sushitter;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import morphologicalAnalysis.MorphologicalAnalysis;
import plugin.Translation;

public class TranslatedTweetRepository
implements TranslatedTweetRepositoryService{
	
	@Override
	public String tweet(String name, String emotion, String content) {
		String count;
		String translated;
		String JSONData = "";
		try{
			// get today's date
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/HH:mm:ss");
			String date = sdf.format(cal.getTime());
			// get tweet number
			try(Statement s = connection.createStatement()){
				try(ResultSet rs = s.executeQuery("select count(*)+1 from tweets")){
					rs.next();
					count = rs.getString(1);
				}
			}
			// insert into DB
			try(PreparedStatement ps = connection.prepareStatement("insert into tweets values (?, ?, ?, ?, ?, ?)")){
				ps.setString(1, name);
				ps.setString(2, emotion);
				ps.setString(3, content);
				translated = Translation.execute(content);
				ps.setString(4, translated);
				ps.setString(5, date);
				ps.setString(6, count);
				ps.executeUpdate();
			}
			// make JSON
			JSONData = "{";
			JSONData += "\"name\": \""+name+"\", ";
			JSONData += "\"emotion\": \""+emotion+"\", ";
			JSONData += "\"content_jp\": \""+content+"\", ";
			JSONData += "\"content_en\": \""+translated+"\", ";
			JSONData += "\"date\": \""+date+"\", ";
			JSONData += "\"count\": \""+count+"\"";
			JSONData += "}";
		} catch(SQLException e){
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return JSONData;
	}
	
	/*@Override
	public String tweet(String name, String emotion, String content) {
		String count;
		String translated;
		String JSONData = "";
		try{
			// get today's date
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/HH:mm:ss");
			String date = sdf.format(cal.getTime());
			// get tweet number
			try(Statement s = connection.createStatement()){
				try(ResultSet rs = s.executeQuery("select count(*)+1 from tweets")){
					rs.next();
					count = rs.getString(1);
				}
			}
			// insert into DB
			try(PreparedStatement ps = connection.prepareStatement("insert into tweets values (?, ?, ?, ?, ?, ?)")){
				ps.setString(1, name);
				ps.setString(2, emotion);
				ps.setString(3, content);
				translated = Translation.execute(content);
				ps.setString(4, translated);
				ps.setString(5, date);
				ps.setString(6, count);
				ps.executeUpdate();
			}
			// make JSON
			JSONData = "{";
			JSONData += "\"name\": \""+name+"\", ";
			JSONData += "\"emotion\": \""+emotion+"\", ";
			JSONData += "\"content_jp\": \""+content+"\", ";
			JSONData += "\"content_en\": \""+translated+"\", ";
			JSONData += "\"date\": \""+date+"\", ";
			JSONData += "\"count\": \""+count+"\"";
			JSONData += "}";
		} catch(SQLException e){
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return JSONData;
	}*/
	
	@Override
	public String getTweets(String startNm, String getNm){
		int sNm = Integer.valueOf(startNm);
		int gNm = Integer.valueOf(getNm);
		String name;
		String emotion;
		String content_jp;
		String content_en;
		String date;
		String count;
		String JSONData = "";
		try{
			try(Statement s = connection.createStatement()){
				// get all tweets
				try(ResultSet rs = s.executeQuery("select * from tweets")){
					int i;
					for(i=0; i<sNm; i++){
						rs.next();
					}
					JSONData = "[";
					// get tweets from startNum to startNum+getNUm(=fromNum)
					for(int start=i; start<sNm+gNm; start++){
						name = rs.getString(1);
						emotion = rs.getString(2);
						content_jp = rs.getString(3);
						content_en = rs.getString(4);
						date = rs.getString(5);
						count = rs.getString(6);
						//make JSON
						JSONData += "{";
						JSONData += "\"name\": \""+name+"\", ";
						JSONData += "\"emotion\": \""+emotion+"\", ";
						JSONData += "\"content_jp\": \""+content_jp+"\", ";
						JSONData += "\"content_en\": \""+content_en+"\", ";
						JSONData += "\"date\": \""+date+"\", ";
						JSONData += "\"count\": \""+count+"\"";
						JSONData += "},";
						rs.next();
					}
					// format JSON
					JSONData = JSONData.substring(0, JSONData.length()-1);
					JSONData += "]";
				}
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		System.out.println(JSONData);
		return JSONData;
	}
	
	@Override
	public void testTweet(){
		String content = "中国に行きたいなぁ";
		List<String> wordsList = MorphologicalAnalysis.execute(content);
		String wordsSQL = "";
		for(String word : wordsList){
			wordsSQL += "word LIKE '"+word+"' or ";
		}
		System.out.println("======形態素解析終了======");
		wordsSQL = wordsSQL.substring(0, wordsSQL.length()-4);
		List<Integer> nearTweets = new ArrayList<Integer>();
		try(Statement s = connection.createStatement()){
			try(ResultSet rsTid = s.executeQuery("select tid from examples where "+wordsSQL)){
				List<Integer> searchIDs = new ArrayList<Integer>();
				while(rsTid.next()){
					int tid = rsTid.getInt(1);
					searchIDs.add(tid);
					System.out.println("======tid:"+tid+"======");
				}
				for(int tid : searchIDs){
					try(ResultSet rsW = s.executeQuery("select word from examples where tid = "+tid)){
						List<String> rsWList = new ArrayList<String>();
						List<String> searchList = new ArrayList<String>();
						for(String word : wordsList){
							searchList.add(word);
						}
						while(rsW.next()){
							String setW = rsW.getString(1);
							rsWList.add(setW);
							searchList.add(setW);
						}
						int match = 0;
						int mismatch = 0;
						System.out.println("======searchList:"+searchList+" tid:"+tid+"======");
						System.out.println("======wordsList:"+wordsList+" tid:"+tid+"======");
						for(String testWord : searchList){
							if (wordsList.contains(testWord) && rsWList.contains(testWord)){
								match++;
							}else{
								mismatch++;
							}
						}
						System.out.println("======match:"+match+" mismatch:"+mismatch+" tid:"+tid+"======");
						//評価関数を改善する必要がある
						if(match >= 2){
							nearTweets.add(tid);
						}
						System.out.println("======search finish tid:"+tid+"======");
					}
				}
			}
			int positive = 0;
			int negative = 0;
			for(int id : nearTweets){
				System.out.println("======get output tid:"+id+"======");
				try(ResultSet rsOut = s.executeQuery("select distinct emotion from examples where tid ="+id)){
					rsOut.next();
					if(rsOut.getInt(1) == 1){
						positive++;
					}else{
						negative++;
					}
				}
			}
			int emotion = 0;
			if(positive>=negative){
				emotion = 1;
			}
			System.out.println(emotion+":: p:"+positive+" n:"+negative);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void learn(){
		try{
			String[] lines = sets.split(",");
			int tweetID = 1;
			connection.createStatement().executeUpdate("delete from examples");
			for(String line : lines){
				System.out.println(line);
				String[] str = line.split(":");
				String sentence = str[0];
				int emotion = Integer.valueOf(str[1]);
				//形態素解析をして、結果をDBに保存
				List<String> words = MorphologicalAnalysis.execute(sentence);
				for(String word : words){
					try(PreparedStatement ps = connection.prepareStatement("insert into examples values (?, ?, ?)")){
						ps.setInt(1, tweetID);
						ps.setString(2, word);
						ps.setInt(3, emotion);
						ps.executeUpdate();
					}
				}
				tweetID++;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		try(Statement s = connection.createStatement()){
			try(ResultSet rs = s.executeQuery("select word, count(word) from examples group by word order by count(word)")){
				while(rs.next()){
					int tid = rs.getInt(2);
					String word = rs.getString(1);
					System.out.println(String.valueOf(tid)+":"+word);
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

	private static Connection connection;

	static{
		try{
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			connection = DriverManager.getConnection("jdbc:derby:/Users/showwin/Develop/Java/SushiDB;create=true");
			connection.createStatement().executeUpdate(
					"create table examples (tid integer, word varchar(256), emotion integer)");
			connection.createStatement().executeUpdate(
					"create table tweets (name varchar(256), emotion varchar(1), original varchar(256), translated varchar(256), date varchar(32), count varchar(8))");
		} catch(SQLException e){
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private String sets = "今日の夕飯は親と食べに行く。何を食べられるか楽しみだな！:1,"+
						"明日新しいパソコンを買いに行くから、テンションが高いぞ！！:1,"+
						"昨日変なものを食べたらしくて、かなりお腹痛かった…:0,"+
						"この訓練集合書くのめちゃくちゃ面倒くさい:0,"+
						"かなり辛みがあるけど、やるしかないからな:0,"+
						"今日の日本vsコートジボワール戦は残念だったね:0,"+
						"今日の試合の内容を見ても、明らかにコートジボワールの方が良かったから、あのスコアは妥当な気もするけど。:0,"+
						"これからの試合はギリシャとコロンビアだし、一勝も出来ずにW杯終わってしまうのでは。:0,"+
						"W杯出場アジア3チームは弱すぎるからな。FIFAランキングから見ても、なんでW杯出場してるのってレベルだし。:0,"+
						"8月の中旬に香港に旅行に行くことになった。:1,"+
						"香港のディズニーランドも行く予定だし、かなり楽しみ。:1,"+
						"でも予算10万円ぐらいで、結構お金が飛んでいきそうな感じ。:0,"+
						"去年マカオでカジノに行った時には、元金2000円から始めて、最終的に9000円ぐらいになったから、7000円儲けた。:1,"+
						"マカオには期待値1のギャンブルがあって、そこばっかりやってたから勝てた。:1,"+
						"ギャンブルで期待値1以下のものやるとか損する見込みしかないし、やる気起きない。:0,"+
						"パチンコで10万円勝った！とか言ってる人Twitterでたまに見るけど、おまえそれまでに何万円捨ててきたんだって感じする。:0,"+
						"まだこれで17個目の訓練例かよ。マジで辛い。そんなにたくさん例書けないって。:0,"+
						"中国の南の方はだいたい旅行したし、今度は大連とかハルピンとか北の方の都市に行ってみたい。:1,"+
						"香港のディズニーランドについて調べてたら、東京のディズニーランドめちゃくちゃ行きたくなってきた。:1,"+
						"7月20日にUSJ行く。:1,"+
						"7月15日にUSJにハリーポッターのやつができるから、20日とかすごく混んでそうだけど、行く。:1,"+
						"テーマパークって最初作るときにすでに拡張案とか決めてるのかな？:1,"+
						"なんでも一人でやるタイプで、USJとかも一人で行ってシングルライダーで乗りまくるタイプ:1,"+
						"中国から日本に帰ってくると、空気がすごくきれいな感じがしてかなりいい:1,"+
						"逆に、日本から中国に行くとしばらくガスってるのが気になるよね。:0,"+
						"昼間は曇りかなーと思うから、あんまり空気汚いこと気にならないんだけど、夜になって街灯が光りだすと、空気の悪さが目立つね:0,"+
						"あんまり話題広めすぎてもうまく学習できないだろうから、同じ話題についてたくさんツイートしないとな:0,"+
						"W杯はどこが優勝するのかな。予想はスペインかブラジル。:1,"+
						"ブラジルはやっぱりホームってのが強いんじゃないかな。ホーム+あの気候に慣れてるからね。:1,"+
						"地元は在日ブラジル人が日本一多い都市で、友達のブラジル人も何人かいるから、ブラジルに頑張ってほしいわ:1,"+
						"先週AppleStore表参道が開店したね。先週の木、金曜日あたりは時間あったから東京に行ってくればよかった。:0,"+
						"AppleStore表参道で先着5000人に限定Tシャツ配ってたし、貰いたかったなぁ。:0,"+
						"そういえば2年前ぐらいに国内のAppleStore全部制覇する！って行って、レシート集め始めたんだけど、心斎橋と栄しか行ってないわ。:0,"+
						"MacBookProRetinaが明日届く！！30万円するラップトップは始めてだから緊張するわ！笑:1,"+
						"CPUは今使ってるMacminiと同等だし、GPUは明らかにMacBookProの方が上だし、デスクトップよりも性能高いラップトップ。。:1,"+
						"Apple大好き！！:1,"+
						"今年の秋はAppleがハードウェアたくさん発表するだろうし楽しみだなぁ:1,"+
						"普段Twitterやってる時は、ガンガンツイートできるのに、今回ここで書くのはかなりしんどいわ:0,"+
						"最近一人で旅行してないから、久しぶりにバックパック背負って旅行行きたい:1,"+
						"2年前ぐらいにW杯の時にブラジルに一緒に旅行に行こう！って友達と言ってたけど、結局その話どっかいっちゃったな:1,"+
						"香港の夜景は確かに綺麗だけど、最近は上海とかも夜景キレイになってきて飛び抜けて綺麗だなーとは感じなかった:1,"+
						"上海杭州あたりの料理は日本料理の味とかなり近いから普通に美味しい:1,"+
						"四川料理とか辛くて全然食べれないよね。:0,"+
						"辛くしないで、って頼んでも辛いのでてくるからね。あれは本当に食べられなかったわ。ただでさえ辛いの苦手なのに:0,"+
						"これ書いてて思ったけど、やっぱり単語数多すぎちゃって、近いツイートとか調べるのにかなり時間かかるんじゃないかって気がしてきた。:0,"+
						"正確に分類できるかどうかもかなり怪しいし。:0,"+
						"他の方法を使うべきだったかなぁ。でもそんなに時間ないし、今回の方法でやってみよう。:0,"+
						"週末に研究室行くときはラフな格好で行けるからよい。:1,"+
						"さっき隣の部屋からハッピーバースデーの歌声が聞こえてきたような気がする:1,"+
						"よし、これで半分終わったぞ！(予定):1,"+
						"今年度入ってからWebサービス2つ開発してて、お金たくさん入ってくる〜！:1,"+
						"だけど、Macと香港旅行でほとんどなくなるからなぁ:0,"+
						"満たされてる状態だと生産性落ちるし、適度に飢餓状態の方がいいよね:1,"+
						"蚊に刺されて痒い:0,"+
						"この時期になると蚊が出てくるから嫌だなぁ:0,"+
						"あと下宿の近くに山があるから、そこから蛾とか変な虫がたくさんでてきて、廊下の電球の辺りにウヨウヨしてるから最悪:0,"+
						"無視が入ってこないように一瞬でドア開けて、一瞬で閉めるからね。:0,"+
						"去年なんか羽アリみたいなやつのもうちょっと小さな奴が廊下一面に死んでて、マジで気持ち悪かったわ。:0,"+
						"先週久しぶりに2kmぐらいランニングして、汗かいたら気持ちよかった。:1,"+
						"最近ずっと座ってばっかりだったから、たまには体動かすのもいいよね:1,"+
						"S.Jobsはやっぱり神:1,"+
						"TimCookはやっぱり船長:1,"+
						"寿司ゆき！:1,"+
						"結局寿司ゆきのhubot-sushiyukiはGitHubの人に使われることになったのだろうか:1,"+
						"はてな周辺の人たちは寿司が好きらしい(OB含む):1,"+
						"中国で寿司食べると回転すしの2、3倍ぐらいの値段するし、日本にいる間に飽きるぐらい寿司食べたい:1,"+
						"だけど、あんまり京都には(安い)寿司の店がなくて寂しい。:0,"+
						"地元は海面してるから、ひたすら回転寿司ある。:1,"+
						"朝にW杯の負け試合見ると、その日の生産性落ちるから、夜に試合やって欲しい:0,"+
						"さすがに疲れてきた。一旦休憩する。:0,";
}
