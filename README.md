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

A library web application that allows to index, retrieve information about books from file metadata/web (Google Books API) and search for ebooks. The front-end
project
can be
found [here](https://github.com/goto-eof/europea-library-client).

## Run the project

Before running the software as Spring Boot application it is necessary to follow some steps:

- edit the application.yml
    - set the `google.books.api_key` to your google api key
        - Europea Library uses Google Books API to retrieve information about books. This API has daily limits: 1,000
          requests/day. To get the API key go
          to [Google Console](https://console.cloud.google.com/apis/credentials?hl=it) and create an API key. Remember
          also to enable Google Books API. The indexer job runs once per day, so that if you have for example 3.000
          e-books, it will take about 3 days to retrieve all information about your library.
    - start from the projects root directory the DBMS with `sudo docker-compose up -d` command
    - run the project as Spring Boot application

## Technologies

Java (JDK 21) • Spring Boot • Spring Batch • Spring JPA • Feign • Queryds • Hibernate • Liquibase • PostgreSQL
• Docker • epublib • pdfbox

## External services

Google Books API

## DB schema

![db_schema](images/db_schema.png)

## Changes log
- 2024-03-23 - filter by file extensions (new properties: `skip-file-extensions` and `allow-file-extensions`)
- 2024-03-22 - Added categories table, which is filled up by google books response. Now epub metadata extractor fills up
  also the tags table. Fixed hibernate exception bug when trying to store parent with child. Filling up the file
  extension column. Added option to avoid the extraction of metadata from a specified file
  list (`do-not-extract-metadata-from-file-extensions`).
- 2024-03-21 - Improved job performance.
- 2024-03-20
    - added PDF meta-data extractor strategy in addition to the EPUB meta-data extractor.
    - refactor of the indexer and cataloguer job (for the first step I moved the business logic in the
      processor as should be done)
    - added the language, num_pages, average_rating and number of raters columns
    - changed column names sbn and isbn to isbn10 and isbn13
    - integration with Google Books API -> now Europea Library downloads book's information from web
    - removed openlibrary.org client (obsolete)
- 2024-03-19 - added Feign (login to openlibrary.org) and implemented .epub meta-data extractor.
- 2024-03-18 - indexer job now restores the relationship between file system item and meta info record.
- 2024-03-16 - finished CRUD implementation of Book info module and fixed the db schema.
- 2024-03-15 - finished implementation regarding the file meta-info schema.
- 2024-03-13 - Job and files/directories DB schema
    - completed implementation of the db schema for storing files and directories information
    - completed implementation of the job that indexes the directory files and stores them on DB hierarchically
        - added an endpoint that allows to run the job
        - the job starts every X minutes (configurable)
        - the job consists of 3 steps:
            - step 0 - directory/subdirectory indexer - retrieves information about directories and subdirectories and
              stores them on DB (job step = INSERTED). Extracts information from epub files and stores them into db.
            - step 1 - record remover - delete all obsolete files/directories records from DB
            - step 2 - record updater - update the step number of new files/directories (from INSERTED to READY)

## More

- Currently, I do not add new changesets to liquibase (the base schema is still in definition status), so that sometimes
  it is necessary to drop all tables and restart the application.
- developed and tested on Linux.
- if you have any suggestions or found a bug please contact me [here](https://andre-i.eu/#contactme)
