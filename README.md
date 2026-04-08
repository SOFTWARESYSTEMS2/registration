# registration
![CI](https://github.com/SOFTWARESYSTEMS2/registration/actions/workflows/main.yml/badge.svg?branch=main)
- [jira board](https://se2project2.atlassian.net/jira/software/projects/SCRUM/boards/1/backlog)
- [discord](https://discord.gg/MDuadk57mZ)
- [big doc](https://indiana-my.sharepoint.com/:w:/r/personal/tredix_iu_edu/Documents/SE2%20Project%20Questions.docx?d=wafefe8f2c9144a81a0034df4f351e212&csf=1&web=1&e=yneUub)

- ```docker compose up -d```
- ```docker ps``` should show a postgres container loaded up
- ```docker exec -it registration-postgres psql -U postgres -d registration``` this lets you load into the db and do direct SQL stuff
    1. ``` \dt ``` shows all the tables
    2. ```SELECT * FROM <table_name>;``` shows all the rows in a table
    3. ```\q``` lets you exit the DB
- ```./mvnw spring-boot:run``` run the thing
- ```docker compose down``` stops the db ```docker compose down -v``` wipes the db
