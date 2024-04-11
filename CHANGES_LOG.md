## Changes log

- 2023-04-01 - finished conversion of job indexer to a multi-threading job.
- 2024-03-27 - fix: do not save failed status when retrieving ebook from google books api, feature: add search for
  filSystemItem entities by category and search by tag (cursored).
- 2024-03-26 - fix: store large book descriptions error
- 2024-03-25 - fix sort by directory and filename.
- 2024-03-24 - changed the two boolean columns which describes the file metadata extraction and web metadata
  retrievement of book info table to integer type because need to have more info about the processing status. This
  change will speed up the reindexing and recataloging of the directory.
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
