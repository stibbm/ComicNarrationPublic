# Comic Narration Generator

In Progress: Moving code here from the private repo to this public repo to make sure no api keys get leaked

## Goal

Take manga/manwha and generate AI scripted and AI narrated story video showing the story panels as it narrates

-------

### Example generated video

https://github.com/user-attachments/assets/3f7bd7ac-fda8-4b30-85c7-fefc15bf7e99

-----

## Program Flow

1. PreProcess image files
2. Split up long image strips into panels
3. Save images to dropbox such that send link with OpenAI request and it can be analyzed ( needs to be publically accessible for this to be possible )
4. Send request to GPT to get core information about what is going in in each image. The prompt used to do this is as follows:

### Bones Info Prompt

"Generate a compelling script that remains accurate to the manga

here is some context on the story: 

"Living by selling information about monsters, Maeng Siwoo is considered completely useless in the hunter industry without any standout qualities. However, he fiercely battles to provide for his sick younger sibling. One fateful day, he is gifted a mysterious smartphone by a transcendent being within a dungeon, appointing him as the channel manager for such beings. Upon subscribing to the channel, he unlocks the ability to wield the powers of the gods."

respond in json format with the following fields
I will specify what information to include in each field where the value would normally go:

```json
{  "relevantCharactersInScene": [
  {
      "index": "integer which describes the order which this characters actions would have occurred in this panel",
      "speaker": "name of character make up a clear nickname if you don't know their name Ensure this is unique per unique character",
      "dialogue": "the text the character is speaking in the scene",
      "name": "The characters name populate if recognized from characters provided",
      "genderPronoun": "he or she",
      "speakerUniqueId": "integer id which should be used to uniquely identify this character it should remain the same in future panels",
      "appearance": "less than 10 word summary of the characters appearance",
      "action": "less than 10 word description of characters actions"
  }
]}

```
"
