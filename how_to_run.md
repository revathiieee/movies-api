# 

docker network create movies-mysql

mvn clean install -Pprod

docker-compose down

docker-compose up -d