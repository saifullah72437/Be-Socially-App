package com.saif.besocially.Models;

import java.util.ArrayList;

public class Story {
   private String storyBy;
   private Long storyAt;
   ArrayList<UserStories> stories;

   public Story() {
   }

   public String getStoryBy() {
      return storyBy;
   }

   public void setStoryBy(String storyBy) {
      this.storyBy = storyBy;
   }

   public Long getStoryAt() {
      return storyAt;
   }

   public void setStoryAt(Long storyAt) {
      this.storyAt = storyAt;
   }

   public ArrayList<UserStories> getStories() {
      return stories;
   }

   public void setStories(ArrayList<UserStories> stories) {
      this.stories = stories;
   }
}
