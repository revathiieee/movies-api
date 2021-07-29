## How To Run the Application along with DB

1. Create Network
   - docker network create movies-mysql

2. Build Production Ready Application
   - mvn clean install -Pprod

3. Docker command to up and down the application
  - docker-compose down
  - docker-compose up -d
