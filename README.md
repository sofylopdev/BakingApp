# BakingApp

Fourth Project for the Android Nanodegree

## Project Overview

In this project, you will create an app to view video recipes. You will handle media loading, verify your user interfaces with UI tests, and integrate third party libraries. You'll also provide a complete user experience with a home screen widget.
You will productionize an app, taking it from a functional state to a production-ready state. This will involve finding and handling error cases, adding accessibility features, allowing for localization, adding a widget, and adding a library.

Each Android ND project will be reviewed against the [Common Project Requirements](http://udacity.github.io/android-nanodegree-guidelines/core.html), in addition to its project-specific rubric.

## Why this Project?

As a working Android developer, you often have to create and implement apps where you are responsible for designing and planning the steps you need to take to create a production-ready app. Unlike Popular Movies where we gave you an implementation guide, it will be up to you to figure things out for the Baking App.

## What Will I Learn?

In this project you will:

 - Use MediaPlayer/Exoplayer to display videos.
 - Handle error cases in Android.
 - Add a widget to your app experience.
 - Leverage a third-party library in your app.
 - Use Fragments to create a responsive design that works on phones and tablets.
 
## App Description

Your task is to create a Android Baking App that will allow Udacity’s resident baker-in-chief, Miriam, to share her recipes with the world. You will create an app that will allow a user to select a recipe and see video-guided steps for how to complete it.

[The recipe listing is located here](https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json).

The JSON file contains the recipes' instructions, ingredients, videos and images you will need to complete this project. Don’t assume that all steps of the recipe have a video. Some may have a video, an image, or no visual media at all.

One of the skills you will demonstrate in this project is how to handle unexpected input in your data -- professional developers often cannot expect polished JSON data when building an app.

## Sample Mocks
Here are some sample mocks that you can refer to as you build your app. Make sure your app functions properly on tablet devices as well as phones. ([PDF version](https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58dee986_bakingapp-mocks/bakingapp-mocks.pdf))


## Languages, libraries and tools used

 - Java
 - Android Support Libraries
 - [Retrofit](https://github.com/square/retrofit)
 - [Gson](https://github.com/google/gson) (as a converter in Retrofit)
 - [Picasso](https://github.com/square/picasso)
 - [Butterknife](https://github.com/JakeWharton/butterknife)
 - [Timber](https://github.com/JakeWharton/timber)
 - [ExoPlayer](https://developer.android.com/guide/topics/media/exoplayer)
 - [Espresso](https://developer.android.com/training/testing/espresso/)
 
 ## Special funtionality implemented
 
 - Used a ViewPager instead of using "Next"/"Previous" to display the recipe steps details (Phone).
 
 ## ScreenShots
 
 |Main List| Recipe Details | Step Details |
| --- | --- | --- |
| ![alt text](https://github.com/sofylopdev/BakingApp/blob/master/MainList.png) | ![alt text](https://github.com/sofylopdev/BakingApp/blob/master/RecipeDetails.png) | ![alt text](https://github.com/sofylopdev/BakingApp/blob/master/StepDetails.png) | 

|Landscape: Step Details| Widget Configuration Page | Widget |
| --- | --- | --- |
| ![alt text](https://github.com/sofylopdev/BakingApp/blob/master/StepLandscape.png) | ![alt text](https://github.com/sofylopdev/BakingApp/blob/master/WidgetConfig.png) | ![alt text](https://github.com/sofylopdev/BakingApp/blob/master/Widget.png) | 

|Tablet: Main List| Tablet: Recipe and Step Details | No video example |
| --- | --- | --- |
| ![alt text](https://github.com/sofylopdev/BakingApp/blob/master/MainListTablet.png) | ![alt text](https://github.com/sofylopdev/BakingApp/blob/master/DetailsTablet.png) | ![alt text](https://github.com/sofylopdev/BakingApp/blob/master/NoVideoTablet.png) | 
 
 ## Icon
 
 Icon made by [Freepik](https://www.freepik.com/) from www.flaticon.com 
 
 ## License
 
http://www.apache.org/licenses/LICENSE-2.0
 
 
