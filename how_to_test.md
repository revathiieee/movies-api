#Testing using Swagger
After running hte application and browse the swagger api url
Swagger API URL:
http://localhost:8080/swagger-ui/index.html

1. To know the oscar winning movie
    GET Endpoint: http://localhost:8080/api/movies/v1/bestPicture
    Request : Header : X-APIKEY , movieName : Titanic 
    Response:
      {
      "boxOfficeValue": "string",
      "isWonOscar": "string",
      "movieName": "string",
      "rating": 0,
      "releaseDate": "string"
      }
    
2. To give rating
   PUT Endpoint: http://localhost:8080/api/movies/v1/rating
   Request : Header : X-APIKEY , movieName : Titanic, rating: 8.6
   Response:
   {
   "boxOfficeValue": "string",
   "movieName": "string",
   "rating": 0,
   "ratingId": 0
   }

3. To know Top 10 boxoffice movies
   GET Endpoint: http://localhost:8080/api/movies/v1/boxOffice
   Response:
   {
   "boxOfficeValue": "string",
   "movieName": "string",
   "rating": 0,
   "ratingId": 0
   }