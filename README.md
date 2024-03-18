```

    ______                                     __    _ __                         
   / ____/_  ___________  ____  ___  ____ _   / /   (_) /_  _________ ________  __
  / __/ / / / / ___/ __ \/ __ \/ _ \/ __ `/  / /   / / __ \/ ___/ __ `/ ___/ / / /
 / /___/ /_/ / /  / /_/ / /_/ /  __/ /_/ /  / /___/ / /_/ / /  / /_/ / /  / /_/ / 
/_____/\__,_/_/   \____/ .___/\___/\__,_/  /_____/_/_.___/_/   \__,_/_/   \__, /  
                      /_/                                                /____/   

                                                                         SERVER
```

# Europea Library (server - Work In Progress)

A library web application that allows to catalog and download e-books. The front-end project can be
found [here](https://github.com/goto-eof/europea-library-client).

## Technologies

- Java (JDK 21)
- Spring Batch (Spring Boot 3.3.0)
- Spring JPA (Spring Boot 3.3.0)
- Querydsl
- Hibernate
- Liquibase (4.21)
- PostgreSQL
- Docker

## DB schema

![db_schema](images/db_schema.png)

## Changes log

- 2023-03-16 - finished CRUD implementation of Book info module and fixed the db schema.
- 2023-03-15 - finished implementation regarding the file meta-info schema.
- 2024-03-13 - Job and files/directories DB schema
    - completed implementation of the db schema for storing files and directories information
    - completed implementation of the job that indexes the directory files and stores them on DB hierarchically
        - added an endpoint that allows to run the job
        - the job starts every X minutes (configurable)
        - the job consists of 3 steps:
            - step 0 - directory/subdirectory indexer - retrieves information about directories and subdirectories and
              stores them on DB (job step = INSERTED)
            - step 1 - record remover - delete all obsolete files/directories records from DB
            - step 2 - record updater - update the step number of new files/directories (from INSERTED to READY)

## More

- developed and tested on Linux.