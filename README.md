# Popular Movies

Second project of Udacity's Android Nanodegree.

Popular Movies is an app that helps users discover popular and recent movies. The user can see either the list of the most popular movies at the moment, or the list of the best rated movies on TMDb. It can then obtain details about these films: poster, synopsys, trailers, users' comments, release date and users' rating. He can finally add movies to his list of favorite movies, so he can find them later.

## Preview

![gif preview](https://github.com/maphdev/GDND_Popular_Movies/blob/master/preview.gif)

## Concepts

- Layout design
- Menu
- ProgressBar / FloatingActionButton
- RecyclerView / ViewHolder / GridLayoutManager / Adapter
- Intents (navigate between activities, launch another app) / Parcelable interface / Pass data between activities
- TMDb API
- JSon parsing
- Picasso library
- AsyncTask / AsyncTaskLoader
- Error handling (no internet connection, wrong request results, try/catch)
- NetworkUtils : build URL & URI / requests from URL / check network connection
- Data persistence
- SQLite / Contract / DbHelper / ContentResolver / Cursors

## Regarding the TMDb API

This product uses the TMDb API but is not endorsed or certified by TMDb.

Request your own API key on [TMDb](https://www.themoviedb.org/) website.

You should put your API key in utils/NetworkUtils.java, in "YOUR_API_KEY_HERE".
