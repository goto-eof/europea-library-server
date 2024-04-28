```
                            ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
                            ⣿⣿⣿⣿⣿⣿⣿⣿⡿⠏⠻⣿⣿⣷⢀⡀⣾⣿⣿⠟⠙⢿⣿⣿⣿⣿⣿⣿⣿⣿
                            ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣠⣄⣿⣿⣿⣿⣿⣿⣿⣿⣠⣄⣿⣿⣿⣿⣿⣿⣿⣿⣿
                            ⣿⣿⣿⣿⡿⠏⠻⢿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡿⠟⠹⢿⣿⣿⣿⣿
                            ⣿⣿⣿⣿⣟⣠⣄⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣠⣄⣻⣿⣿⣿⣿
                            ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
                            ⣿⣿⣿⡍⠀⢩⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⡍⠀⢩⣿⣿⣿
                            ⣿⣿⣿⣷⣶⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣷⣶⣾⣿⣿⣿
                            ⣿⣿⣿⣿⡿⠟⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⠿⠻⢿⣿⣿⣿⣿
                            ⣿⣿⣿⣿⡷⣀⡀⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⢀⣀⢾⣿⣿⣿⣿
                            ⣿⣿⣿⣿⣿⣿⣿⣿⣿⠟⠻⣿⣿⣿⣿⣿⣿⣿⣿⠟⠻⣿⣿⣿⣿⣿⣿⣿⣿⣿
                            ⣿⣿⣿⣿⣿⣿⣿⣿⣷⣀⣀⣿⣿⣿⠋⠙⣿⣿⣿⣀⣀⣾⣿⣿⣿⣿⣿⣿⣿⣿
                            ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣴⣦⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿
     ______                                     __    _ __
    / ____/_  ___________  ____  ___  ____ _   / /   (_) /_  _________ ________  __
   / __/ / / / / ___/ __ \/ __ \/ _ \/ __ `/  / /   / / __ \/ ___/ __ `/ ___/ / / /
  / /___/ /_/ / /  / /_/ / /_/ /  __/ /_/ /  / /___/ / /_/ / /  / /_/ / /  / /_/ /
 /_____/\__,_/_/   \____/ .___/\___/\__,_/  /_____/_/_.___/_/   \__,_/_/   \__, /
                       /_/                                                /____/
                                                                          SERVER
```

# Europea Library (server - Work In Progress)

A library web application that allows to index, retrieve information about books from file metadata/web (by using
multi-threading), search and download e-books (need to be authenticated in order to be able to download). The front-end project can be
found [here](https://github.com/goto-eof/europea-library-client), while the Google Books API mock application can be
cloned from [here](https://github.com/goto-eof/europea-library-google-books-api-emulator/tree/master).

## Run the project

Before running the software as Spring Boot application it is necessary to follow some steps:

- edit the `application.yml` in the following way:
    - set the `google.books.api_key` to your Google API key
        - Europea Library uses Google Books API to retrieve information about books. This API has daily limits: 1,000
          requests/day. To get the API key go
          to [Google Console](https://console.cloud.google.com/apis/credentials?hl=it) and create an API key. Remember
          also to enable Google Books API. The indexer job runs once per day, so that if you have for example 3.000
          e-books, it will take about 3 days to retrieve all information about your library.
    - edit the default `username`, `email` and `password` (at the first run the application will store these information
      on the db):
      ```
      default-admin-username: admin
      default-admin-email: admin@admin.com
      default-admin-password: password
      ```
    - edit the `qr-code-path` property in order to allow to generate QR Codes for each e-book;
    - generate certificates for encrypting and decrypting our JWT tokens:
        - Generate a Private Key (RSA):
        ```
        openssl genpkey -algorithm RSA -out private-key-old.pem
        ```
        - Extract the Public Key from the Private Key by running:
        ```
        openssl rsa -pubout -in private-key.pem -out public-key.pem
        ```
        - Then convert it to the appropriate PCKS format and replace the old one
        ```
        openssl pkcs8 -topk8 -inform PEM -outform PEM -in private-key-old.pem -out private-key.pem -nocrypt
        ```
        - replace the existing certificates in `src/main/resources/certs` with those that you generated
    - start the DBMS from the projects root directory with `sudo docker-compose up -d` command or create from your PostgreSQL running instance a database named `europea_library`
    - run the project from your IDE or execute from the root of the project
      ```
      ./gradlew bootJar
      ```
      in order to create the jar file
    - then create a file called `start.sh`
      ```
      #!/bin/bash
      /bin/java -Dspring.config.location=application.yml -jar europea-library.jar
      ```
    - make it executable:
      ```
      chmod +x start.sh
      ```
    - run Europea Library jar
      ```
      ./start.sh
      ```

## How the application works?

The core of the application is the indexer job. It retrieves all the information about files and saves them on the DB.
The indexing process consists of file metadata extraction and web metadata retrievement (in particular from Google Books
API). On the first run the job it will take some time to index and extract information from files or retrieve
them from web. This happens because the file metadata extraction and the web metadata retrievement is expensive in terms
of resources even if I implemented a **multithreading job**. The next job run will take less time, because the metadata
extraction was done for all the files (except the cases when the directory contains new e-books). After the job
completed all steps, the API becomes available for queries, so that the client application can interact with the API (
otherwise an HTTP 404 status is returned). Moreover, the indexer job starts every night at 11:00 PM (configurable). If
the job is already running then it will continue to process files and no other job will run.

## Features
- index large digital books collection quickly;
- explore e-books by directory;
- explore e-books by category;
- explore e-books by tag;
- explore e-books by file type;
- view book information;
- download e-books;
- search by title, author, publisher, ISBN and published date;
- edit e-book information (only administrator is able to do this);
- generate e-book URL QR Code;
- login/register to the system (2 categories of user: ADMINISTRATOR and USER).

## Job steps

Because the core of the application is the job indexer, I am attaching the job schema in which is explained in summary
how it works.
![job_schema](images/job_steps.png)

## Technologies

Java • Spring Boot • Spring Batch • Spring Security • Spring JPA • Queryds • Hibernate • Feign • Liquibase • PostgreSQL
• Docker • epublib • pdfbox • Google ZXing

## External services

Google Books API

## DB schema

![db_schema](images/db_schema.png)

## More

- Currently, I do not add new changesets to liquibase (the base schema is still in definition status), so that sometimes
  it is necessary to drop all tables and restart the application.
- During my tests (in debug mode) I noticed that the job, in order to index and extract metadata from 8.850 files in a
  single-thread context,
  takes about 1 hour on a notebook (based on Ubuntu) with Intel i5 (2 core, 2.40GHz) equipped with an SSD. Because I
  need to index about 100.000 ebooks, I decided to rewrite the job by implementing a multi-thread job processor. On the
  same notebook I ran
  the multi-thread job and the result is the following: about ~2 minutes and 45 seconds to index 8.850 files. I also run
  the
  job on a set of about 110.000 ebook (~25.000 epub, ~25.000 pdf, ~60.000 other files, with some duplicate files), on
  i7-10750H (6 core, 2.60GHz)
  equipped with SSD, and it took 39 minutes to finish
  the job. Some steps were skipped (like FSI/FMI deleter) because I started the job on an empty database.
- developed and tested on Linux.
- if you have any suggestions or found a bug please contact me [here](https://andre-i.eu/#contactme)
