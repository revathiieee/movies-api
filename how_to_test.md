## Testing using Swagger  

After running hte application and browse the swagger api url

**Swagger API URL:** 

**http://localhost:8090/swagger-ui/index.html**

### API KEY for testing: **130feada**

1. To know the oscar winning movie

    GET Endpoint: http://localhost:8090/api/movies/v1/bestPicture
    
    Request : Header : X-APIKEY , movieName : Titanic 
    
    Response:
      {
      "boxOfficeValue": "string",
      "isWonOscar": "string",
      "movieName": "string",
      "rating": 0,
      "releaseDate": "string"
      }

    Status Code: 200

    ---------------------------------------------------
 
    Request : Header : InValid API Key , movieName : Titanic 

    Response:

    **Supplied API Key : invalid-key is invalid**

    Status Code: 400

    ---------------------------------------------------

   Request : Header : X-API Key , movieName : invalidMovie

   Response:

   **MovieName : invalidMovie does not exist.**

   Status Code: 404
   
-------------------------------------------------------
    
2. To give rating

   PUT Endpoint: http://localhost:8090/api/movies/v1/rating
   
   Request : Header : X-APIKEY , movieName : Titanic, rating: 8.6
   
   Response:
   {
   "boxOfficeValue": "string",
   "movieName": "string",
   "rating": 0,
   "ratingId": 0
   }
   
   Status Code: 200

-------------------------------------------------------------

3. To know Top 10 boxoffice movies

   GET Endpoint: http://localhost:8090/api/movies/v1/boxOffice
   
   Response:
   {
   "boxOfficeValue": "string",
   "movieName": "string",
   "rating": 0,
   "ratingId": 0
   }
