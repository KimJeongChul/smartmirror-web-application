var FUNCTIONSERVICE = {
	defaultHome : function($scope) {
		console.debug("Ok, going to default view...");
        if(responsiveVoice.voiceSupport()) {
          responsiveVoice.speak("홈으로 이동합니다.","Korean Female");
        }
        $scope.focus = "default";
	},
	whoIsSmartMirror : function($scope) {
		console.log("Who is Smart Mirror");
		if(responsiveVoice.voiceSupport()) {
	          responsiveVoice.speak("저는 음성 인식이 가능한 스마트 미러입니다.","Korean Female");
        }
		$scope.focus = "whoissmartmirror";
	},
	goSleep : function($scope){
		console.debug("Ok, going to sleep...");
        if(responsiveVoice.voiceSupport()) {
          responsiveVoice.speak("자러 갈게요. 다음에 봐요!","Korean Female");
        }
        $scope.focus = "sleep";
	},
	wake : function($scope) {
		console.debug("Wake up...");
		if(responsiveVoice.voiceSupport()) {
            responsiveVoice.speak("안녕하세요. 미러에요!","Korean Female");
          }
    	$scope.focus = "default";
	},
	whatCanISay : function($scope){
		console.debug("Here is a list of commands...");
        if(responsiveVoice.voiceSupport()) {
          responsiveVoice.speak("다음은 이용 가능한 메뉴입니다.","Korean Female");
        }
        $scope.focus = "commands";
	},
	map : function($scope,GeolocationService,MapService) {
		console.debug("Home map ...");
        GeolocationService.getLocation({enableHighAccuracy: true}).then(function(geoposition){
            console.log("Geoposition", geoposition);
            $scope.map = MapService.generateMap(geoposition.coords.latitude+','+geoposition.coords.longitude);

            $scope.focus = "map";
        });
        if(responsiveVoice.voiceSupport()) {
          responsiveVoice.speak("현재 위치 입니다.","Korean Female");
        }
	},
	location : function(location,$scope,GeolocationService,MapService) {
		console.debug("Getting map of", location);
        $scope.map = MapService.generateMap(location);
        if(responsiveVoice.voiceSupport()) {
          responsiveVoice.speak(location + "의 지도입니다.","Korean Female");
        }
        $scope.focus = "map";
	},
	news: function($scope) {
		console.debug("News..");
		
		if(responsiveVoice.voiceSupport()) {
            responsiveVoice.speak("실시간 뉴스입니다.","Korean Female");
          }
		
		$('#news-div').load('https://news.google.co.kr/news?pz=1&zx=muklwsp2gkt0 .section-toptop .esc-lead-article-title .titletext',function(){
  			console.log('news loaded.');
  		});
		$scope.focus = "news";
	},
	playYoutube : function(term,$scope,$sce,YoutubeService) {
		console.log("Play Youtube");
		
		if(responsiveVoice.voiceSupport()) {
        	responsiveVoice.speak("유튜브를 동영상을 재생합니다.","Korean Female");
        }
		YoutubeService.getYoutube(term,'video').then(function(){
			if(term){
	            var videoId = YoutubeService.getVideoId()
	            $scope.focus = "youtube";
	            $scope.youtubeurl = "http://www.youtube.com/embed/" + videoId + "?autoplay=1&enablejsapi=1&version=3&playerapiid=ytplayer"
	            $scope.currentYoutubeUrl = $sce.trustAsResourceUrl($scope.youtubeurl);
            }
        });
	},
	stopYoutube : function($scope) {
		console.debug("Stop Youtube");
		
		if(responsiveVoice.voiceSupport()) {
        	responsiveVoice.speak("유튜브를 동영상을 정지합니다.","Korean Female");
        }
		
		var iframe = document.getElementsByTagName("iframe")[0].contentWindow;
        iframe.postMessage('{"event":"command","func":"' + 'stopVideo' +   '","args":""}', '*');
        $scope.focus = "default";
	},
	subway : function(station,linenumber,updown,$scope,SubwayService) {
		console.debug("subway");
		SubwayService.init(station).then(function(){
            SubwayService.getArriveTime(linenumber,updown).then(function(data){
              if(data != null){
                $scope.subwayinfo1 = data[1].ARRIVETIME + "에 " + data[1].SUBWAYNAME + "행 열차";
                $scope.subwayinfo2 = data[2].ARRIVETIME + "에 " + data[2].SUBWAYNAME + "행 열차";
                $scope.subwayinfo3 = data[3].ARRIVETIME + "에 " + data[3].SUBWAYNAME + "행 열차";
                $scope.subwayinfo4 = data[4].ARRIVETIME + "에 " + data[4].SUBWAYNAME + "행 열차";
         
                if(responsiveVoice.voiceSupport()) {
                	responsiveVoice.speak(data[1].ARRIVETIME + "에 " + data[1].SUBWAYNAME + "행 열차가 있습니다. 이어서,"+data[2].ARRIVETIME + "에 " + data[2].SUBWAYNAME + "행 열차가 있습니다.","Korean Female");
                }
              }else{
                $scope.subwayinfo = "운행하는 열차가 없습니다.";
                if(responsiveVoice.voiceSupport()) {
                	responsiveVoice.speak("운행하는 열차가 없습니다.","Korean Female");
                }
              }
              $scope.focus = "subway";
            });
          });
	},
	photo : function(PHOTO_INDEX) {
		console.debug("Take a Photo ...");
		
		if(responsiveVoice.voiceSupport()) {
			responsiveVoice.speak("사진 촬영을 시작합니다.","Korean Female");
		}
		
		/* 카메라 프로세스 */
		var exec_photo = require('child_process').exec;
	
		/* 카메라 저장될 위치 설정 */
		var photo_path = __dirname+"/public/photo/"+"photo"+PHOTO_INDEX+'.jpg';
		
		/* 라즈베리 카메라 촬영 명령*/
		var cmd_photo = 'raspistill -o '+photo_path;
		exec_photo(cmd_photo, function(error, stdout, stderr){
			console.log('Photo Saved : ',photo_path);
			require('./js/mailer').sendEmail(photo_path);
		});
		
		// 4초 후 음성 합성 출력
		setTimeout(function() {
			if(responsiveVoice.voiceSupport()) {
				responsiveVoice.speak("사진 촬영이 끝났습니다.","Korean Female");
			}
		}, 4000);
		
		

	},
	video : function(VIDEO_INDEX) {
		console.debug("Take a Video ...");
		
		if(responsiveVoice.voiceSupport()) {
            responsiveVoice.speak("비디오 촬영을 시작합니다.","Korean Female");
        }
		
		/* 비디오 프로세스*/
		var exec_video = require('child_process').exec;
		/* 비디오 저장될 위치 설정*/
		var video_path = __dirname+"/public/video/"+"video"+VIDEO_INDEX+'.h264';
		/* 라즈베리 카메라 비디오 명령*/
		var cmd_video = 'raspivid -o '+video_path+' -t 4000';
		
		/* 라즈베리 카메라 비디오 촬영 및 이메일 전송*/
		exec_video(cmd_video, function(errror, stdout, stderr) {
			console.log('Video Saved : ',video_path);
			require('./js/mailer').sendEmail(video_path);
		});
		
		// 4초 후 음성 합성 출력
		setTimeout(function() {
			if(responsiveVoice.voiceSupport()) {
				responsiveVoice.speak("비디오 촬영이 끝났습니다.","Korean Female");
			}
		}, 4000);
		
	},
	lightOn : function() {
		console.debug("led on...");
		
    	if(responsiveVoice.voiceSupport()) {
            responsiveVoice.speak("등을 켭니다.","Korean Female");
        }
		
		/* Light on 프로세스*/
		var exec_lighton = require('child_process').exec;
		/* 커맨드 실행할 lightOn.js의 위치  */
		var cmd_path = "/home/pi/RelaySwitch/lightOn.js";
		/* 커맨드 명령*/
		var cmd_lighton = 'node '+cmd_path;
		
		exec_lighton(cmd_lighton, function(errror, stdout, stderr) {
			console.log('Start node lightOn.js');
		});
	},
	lightOff : function() {
		console.debug("led off...");
		
		if(responsiveVoice.voiceSupport()) {
            responsiveVoice.speak("등을 끕니다.","Korean Female");
        }
		
		/* Light off 프로세스*/
		var exec_lightoff = require('child_process').exec;
		/* 커맨드 실행할 lightOff.js의 위치  */
		var cmd_path = "/home/pi/RelaySwitch/lightOff.js";
		/* 커맨드 명령*/
		var cmd_lightoff = 'node '+cmd_path;
		
		exec_lightoff(cmd_lightoff, function(errror, stdout, stderr) {
			console.log('Start node lightOff.js');
		});
	}
};