<p align="center">
  <img src="https://raw.githubusercontent.com/sungbin5304/KakaoBotSourceHub/master/IMAGE/icon.png" width="300" height="300">
</p>

-----

## 설명
새자봇(새로운 자동응답 봇)은 메신저 자동 답장 봇 입니다.<br>
반응할 채팅과 대답할 말을 설정하기 위해 코딩이 필요합니다.<br>
지원하는 언어는 총 3가지로 자바스크립트, 커피스크립트, 루아스크립트가 있습니다.<br>
추가로 코딩이 필요 없는 단순 자동 응답 키워드 설정도 지원 합니다.


## 미리보기 이미지
<div>
<img src="https://raw.githubusercontent.com/sungbin5304/NewAutoReplyBot-Helper/master/IMAGE/screener_1562496049538.png" width="300" height="500">
<img src="https://raw.githubusercontent.com/sungbin5304/NewAutoReplyBot-Helper/master/IMAGE/screener_1568363700374.png" width="300" height="500">
<img src="https://raw.githubusercontent.com/sungbin5304/NewAutoReplyBot-Helper/master/IMAGE/screener_1562496149897.png" width="300" height="500">
<img src="https://raw.githubusercontent.com/sungbin5304/NewAutoReplyBot-Helper/master/IMAGE/screener_1562496170558.png" width="300" height="500">
</div>

## 개발
![Image](https://raw.githubusercontent.com/sungbin5304/NewAutoReplyBot-Helper/master/IMAGE/sungbin.png)

## 메인 함수
``` JavaScript
function response(room, msg, sender, isGroupChat, replier, ImageDB, package) {
  /*
  @String room : 메세지를 받은 방 이름 리턴
  @String sender : 메세지를 보낸 상대의 이름 리턴
  @Boolean isGroupChat : 메세지를 받은 방이 그룹채팅방(오픈채팅방은 그룹채팅방 취급) 인지 리턴
  @Object replier : 메세지를 받은 방의 Action를 담은 Object 리턴
  @Object ImageDB : 이미지 관련 데이터를 담은 Object 리턴
  @String package : 메세지를 받은 어플의 패키지명 리턴
  */
}
```

## Main Function Argument Description
```js
@replier
replier.reply(msg: String) //Sends msg.
replier.reply (room: String, msg: String) //Send msg to the room named room.
replier.replyShowAll(msg1: String, msg2: String) //msg1 is visible and msg2 is sent visible only when the full view button is pressed.
replier.replyShowAll(room: String, msg1: String, msg2: String) //In a room called room, msg1 is just visible, and msg2 is sent to be visible only when the full view button is pressed.

@ImageDB
ImageDB.getProfileImage() //Encodes the profile picture of the other person who received the message in KakaoTalk as Base64 and returns it.
ImageDB.getPicture() //Encodes the last photo received in KakaoTalk as Base64 and returns. (Default : null)

/*
If you use replier.reply(ImageDB.getPicture()), the error data parcel size ~~~ bytes may occur.
In this case, the base64 value of the picture is too long and it is an error in the process of sending it to KakaoTalk, so if you receive Base64 as a small photo and send it, it will work.
*/
```

## JavaScript Example
```js
function response(room, msg, sender, isGroupChat, replier, ImageDB, package) {
  switch(package){
    case "com.kakao.talk" :
      replier.reply("Received a message from KakaoTalk");
      break;
    default :
      replier.reply("Receive message from" + package);
  }
    
  if(msg == "My Profile Image") {
    replier.reply("This is the result of Base64 encoding " + sender + "`s profile picture.\n\n" + ImageDB.getProfileImage());
  }
}
```

-----

## API List
[[API List]](https://github.com/sungbin5304/NewAutoReplyBot-Helper/blob/master/API/API.md)

## Release notes
[[Relase]](https://github.com/sungbin5304/NewAutoReplyBot-Helper/releases)

## Google PlayStore Link
[[Playstore]](https://play.google.com/store/apps/details?id=com.sungbin.autoreply.bot.three)

## Supported Messenger
#### List of confirmed working messengers as of 2019.07.03
1. KakaoTalk
2. Telegram
3. Message (text)
4. Line
5. Facebook Message

-----

## Official Cafe
[[Cafe]](https://cafe.naver.com/nameyee)

## Official Discord
[[Discord]](https://discord.gg/2measTZ)

## Functions (not implemented yet)
1. Upload / edit / Delete Script (Posts)
2. Write / edit / delete comments
3. JavaScript question / answer
4. Point system (reflected by level)
5. Level ranking system
6. New post notification service
7. Etc...

## LICENSE
[[GPL3 LICENSE]](https://github.com/sungbin5304/KakaoBotSourceHub/blob/master/LICENSE)

## Special Thanks
PartWorm
