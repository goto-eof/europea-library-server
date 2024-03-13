```

    ______                                     __    _ __                         
   / ____/_  ___________  ____  ___  ____ _   / /   (_) /_  _________ ________  __
  / __/ / / / / ___/ __ \/ __ \/ _ \/ __ `/  / /   / / __ \/ ___/ __ `/ ___/ / / /
 / /___/ /_/ / /  / /_/ / /_/ /  __/ /_/ /  / /___/ / /_/ / /  / /_/ / /  / /_/ / 
/_____/\__,_/_/   \____/ .___/\___/\__,_/  /_____/_/_.___/_/   \__,_/_/   \__, /  
                      /_/                                                /____/   

                                                                         SERVER
```

# Europea Library

Web application library that allows to catalog and download e-books.

(Work in progress)

## Technologies

- Java (JDK 21)
- Spring Batch (Spring Boot 3.3.0)
- Spring JPA (Spring Boot 3.3.0)
- Liquibase (4.21)

## Log changes

- 2024-03-13 - Job and files/directories DB schema
    - completed implementation of the db schema for storing files and directories information
    - completed implementation of the job that indexes the directory file and stores them on DB hierarchically
        - added an endpoint that allows to run the job
        - the job starts every X minutes (configurable)
        - the job consists of 3 steps:
            - step 0 - directory/subdirectory indexer - retrieves information about directories and subdirectories and
              stores them on DB (job step = INSERTED)
            - step 1 - record remover - delete all obsolete files/directories records from DB
            - step 2 - record updater - update the step number of new files/directories (from INSERTED to READY)

## More

- developed and tested on Linux.