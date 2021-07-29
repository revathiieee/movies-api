## Assumptions

1. To fetch top 10 movies based box office value, I need box office value for the movies
. I fetched the actual movie data from omdb api. Use imdb rating and box office value and load into tb_movie_ratings table.
   Hence I can use for box office and rating operations.
2. Based on the assumption that User can give a rating to a movie after searching for a movie. Once User searched the movie, details will be retrieved from omdb api and box office value is loaded into mysql db
3. Only 'Best Picture' movies from excel are loaded into db.
