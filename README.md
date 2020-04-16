<p align="center">
  <img src="https://raw.githubusercontent.com/sungbin5304/KakaoTalkBotHub/master/banner.png">
</p>

## Description
KakaotalkBotHub is an application that supports KakaoTalk Bot and Hub(similar to a GitHub) for KakaoTalk Bot.<br>
Bot production methods include programming in JavaScript and setting simple keyword auto-response.

## Preview Images
Preparing...

## Devloper
![Image](https://raw.githubusercontent.com/sungbin5304/NewAutoReplyBot-Helper/master/IMAGE/sungbin.png)

## Main Function
``` JavaScript
function response(room, msg, sender, isGroupChat, replier, ImageDB, package) {
  /*
   @String room: Returns the name of the room that received the message
   @String sender: Returns the name of the person who sent the message
   @Boolean isGroupChat: Returns whether the room that received the message is a group chat room (open chat room is treated as a group chat room)
   @Object replier: Return Object containing the action of the room that received the message
   @Object ImageDB: Return Object containing image related data
   @String package: Return the package name of the application that received the message
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
