package com.sungbin.autoreply.bot.three.fixtures

import java.security.SecureRandom
import java.util.*

open class FixturesData {
    companion object {
        var rnd = SecureRandom()
        var avatars  =
            object : ArrayList<String>() {
                init {
                    add("http://i.imgur.com/pv1tBmT.png")
                    add("http://i.imgur.com/R3Jm1CL.png")
                    add("http://i.imgur.com/ROz4Jgh.png")
                    add("http://i.imgur.com/Qn9UesZ.png")
                }
            }
        val groupChatImages =
            object : ArrayList<String>() {
                init {
                    add("http://i.imgur.com/hRShCT3.png")
                    add("http://i.imgur.com/zgTUcL3.png")
                    add("http://i.imgur.com/mRqh5w1.png")
                }
            }
        val groupChatTitles =
            object : ArrayList<String>() {
                init {
                    add("Samuel, Michelle")
                    add("Jordan, Jordan, Zoe")
                    add("Julia, Angel, Kyle, Jordan")
                }
            }
        val names =
            object : ArrayList<String>() {
                init {
                    add("Samuel Reynolds")
                    add("Kyle Hardman")
                    add("Zoe Milton")
                    add("Angel Ogden")
                    add("Zoe Milton")
                    add("Angelina Mackenzie")
                    add("Kyle Oswald")
                    add("Abigail Stevenson")
                    add("Julia Goldman")
                    add("Jordan Gill")
                    add("Michelle Macey")
                }
            }
        val messages: ArrayList<String> =
            object : ArrayList<String>() {
                init {
                    add("Hello!")
                    add("This is my phone number - +1 (234) 567-89-01")
                    add("Here is my e-mail - myemail@example.com")
                    add("Hey! Check out this awesome link! www.github.com")
                    add("Hello! No problem. I can today at 2 pm. And after we can go to the office.")
                    add("At first, for some time, I was not able to answer him one word")
                    add("At length one of them called out in a clear, polite, smooth dialect, not unlike in sound to the Italian")
                    add("By the bye, Bob, said Hopkins")
                    add("He made his passenger captain of one, with four of the men; and himself, his mate, and five more, went in the other; and they contrived their business very well, for they came up to the ship about midnight.")
                    add("So saying he unbuckled his baldric with the bugle")
                    add("Just then her head struck against the roof of the hall: in fact she was now more than nine feet high, and she at once took up the little golden key and hurried off to the garden door.")
                }
            }
        val images =
            object : ArrayList<String>() {
                init {
                    add("https://habrastorage.org/getpro/habr/post_images/e4b/067/b17/e4b067b17a3e414083f7420351db272b.jpg")
                    add("https://cdn.pixabay.com/photo/2017/12/25/17/48/waters-3038803_1280.jpg")
                }
            }

        val randomId: String
            get() = UUID.randomUUID().leastSignificantBits.toString()

        val randomAvatar: String
            get() = avatars[rnd.nextInt(
                avatars.size
            )]!!

        val randomGroupChatImage: String
            get() = groupChatImages[rnd.nextInt(
                groupChatImages.size
            )]!!

        val randomGroupChatTitle: String
            get() = groupChatTitles[rnd.nextInt(
                groupChatTitles.size
            )]!!

        val randomName: String
            get() = names[rnd.nextInt(names.size)]!!

        val randomMessage: String
            get() = messages[rnd.nextInt(
                messages.size
            )]

        val randomImage: String
            get() = images[rnd.nextInt(images.size)]!!

        val randomBoolean: Boolean
            get() = rnd.nextBoolean()
    }
}