$(function(){
	$("#hello").click(function(){
		$.ajax({
			type: "POST",
			url: "jsServices/TranslatedTweetRepository",
			data: JSON.stringify({method: "hello", params: [$("#name").val()]}),
			success: function(ret){
				alert(ret.result);
			}
		});
	});
	
	$("#learn").click(function(){
		$.ajax({
			type: "POST",
			url: "jsServices/TranslatedTweetRepository",
			data: JSON.stringify({method: "learn"}),
			success: function(ret){
				alert("success!");
			}
		});
	});
	$("#test").click(function(){
		$.ajax({
			type: "POST",
			url: "jsServices/TranslatedTweetRepository",
			data: JSON.stringify({method: "testTweet"}),
			success: function(ret){
				alert("success!");
			}
		});
	});
	
	$("#tweet").click(function(){
		$.ajax({
			type: "POST",
			url: "jsServices/TranslatedTweetRepository",
			data: JSON.stringify({method: "tweet", params: [$("#tw_name").val(), $("#tw_emotion").val(), $("#tw_content").val()]}),
			success: function(ret){
				var jsondata = JSON.parse(ret.result);
				var name = jsondata.name;
				var emotion = jsondata.emotion;
				var content_jp = jsondata.content_jp;
				var content_en = jsondata.content_en;
				var date = jsondata.date;
				var count = jsondata.count;
				var sushi_num;
				switch (emotion){
				case "1":
					sushi_num = "31";
					break;
				case "2":
					sushi_num = "09";
					break;
				case "3":
					sushi_num = "36";
					break;
				}
				var img = '<img src="http://www.showwin.asia/contents/sushiyuki_pack2/sushiyuki_'+sushi_num+'.png" alt="User Avatar" class="img-circle" width="50" height="50" />';
				var span = '<span class="chat-img pull-left">'+img+'</span>';
				
				var div_header = '<div class="header"><p id="tl_name"><b>'+name+'</b></p>'+
					'<small class="pull-right text-muted"><p id="tl_time">'+count+'<br>'+date+'</p></small><div>';
				var strong = '<strong class="primary-font" id="tl_content_jp">'+content_jp+'</strong>';
				var p = '<p id="tl_content_en">'+content_en+'</p>';
				var div_body = '<div class="chat-body clearfix">'+div_header+strong+p+'</div>';
				$("#chat").prepend($('<li class="left clearfix">').append(span).append(div_body));
			}
		});
	});
	
	$("#getTweets").click(function(){
		$.ajax({
			type: "POST",
			url: "jsServices/TranslatedTweetRepository",
			data: JSON.stringify({method: "getTweets", params: [$("#getTwsfrom").val(), $("#getTwsNum").val()]}),
			success: function(ret){
				var jsondata = JSON.parse(ret.result);
				var len = jsondata.length;
				$("#chat").empty();
				for(var i=0; i<len; i++){
					var name = jsondata[i].name;
					var emotion = jsondata[i].emotion;
					var content_jp = jsondata[i].content_jp;
					var content_en = jsondata[i].content_en;
					var date = jsondata[i].date;
					var count = jsondata[i].count;
					var sushi_num;
					switch (emotion){
					case "1":
						sushi_num = "31";
						break;
					case "2":
						sushi_num = "09";
						break;
					case "3":
						sushi_num = "36";
						break;
					}
					var img = '<img src="http://www.showwin.asia/contents/sushiyuki_pack2/sushiyuki_'+sushi_num+'.png" alt="User Avatar" class="img-circle" width="50" height="50" />';
					var span = '<span class="chat-img pull-left">'+img+'</span>';
					
					var div_header = '<div class="header"><p id="tl_name"><b>'+name+'</b></p>'+
						'<small class="pull-right text-muted"><p id="tl_time">'+count+'<br>'+date+'</p></small><div>';
					var strong = '<strong class="primary-font" id="tl_content_jp">'+content_jp+'</strong>';
					var p = '<p id="tl_content_en">'+content_en+'</p>';
					var div_body = '<div class="chat-body clearfix">'+div_header+strong+p+'</div>';
					$("#chat").prepend($('<li class="left clearfix">').append(span).append(div_body));
				}
				
				
				
				
				/*var l = $("#tweetList").empty();
				var i = 0;
				var jpTweet;
				var enTweet;
				l.append($("<thead></thead>")
					.append($("<tr></tr>")
						.append($("<th></th>").text("日本語"))
						.append($("<th></th>").text("英語"))
					)
				);
				//l.append($("<tbody>"));
				$.each(ret.result, function(index, tweet){
					if (i%2==0){
						jpTweet = tweet;
					}else{
						enTweet = tweet;
						l.append($("<tr></tr>")
							.append($("<td class='col-xs-6'></td>").text(jpTweet))
							.append($("<td class='col-xs-6'></td>").text(enTweet))
						);
					}
					i++;
				});
				*/
			}
		});
	});
	
	$("#getTotalCount").click(function(){
		$.ajax({
			type: "POST",
			url: "jsServices/TranslatedTweetRepository",
			data: JSON.stringify({method: "getTotalCount"}),
			success: function(ret){
				$("#message").text(ret.result+"件のツイートがあります。").change();
				setTimeout(function(){
					$("#message").fadeOut("normal", function() { $(this).remove(); $("#notification").append('<p id="message"><br></p>'); });
				}, 3000);
			}
		});
	});
});