package com.andreidodu.europealibrary.batch.indexer.step.file;

import com.andreidodu.europealibrary.batch.indexer.JobStepEnum;
import com.andreidodu.europealibrary.dto.BookCodesDTO;
import com.andreidodu.europealibrary.dto.FileDTO;
import com.andreidodu.europealibrary.mapper.FileSystemItemMapper;
import com.andreidodu.europealibrary.model.BookInfo;
import com.andreidodu.europealibrary.model.FileMetaInfo;
import com.andreidodu.europealibrary.model.FileSystemItem;
import com.andreidodu.europealibrary.repository.FileSystemItemRepository;
import com.andreidodu.europealibrary.util.EpubUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Metadata;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class FileItemWriter implements ItemWriter<FileDTO> {
    private final FileSystemItemRepository fileSystemItemRepository;
    private final FileSystemItemMapper fileSystemItemMapper;
    private final EpubUtil epubUtil;

    @Override
    public void write(Chunk<? extends FileDTO> chunk) {
        chunk.getItems().forEach(fileDTO -> {
            // if job was stopped prematurely, then i have already records in insert step
            Optional<FileSystemItem> fileSystemItemOptional = getParentFileSystemItem(fileDTO.getBasePath(), fileDTO.getName(), JobStepEnum.INSERTED.getStepNumber());
            if (fileSystemItemOptional.isEmpty()) {
                createAndSaveFileSystemItem(fileDTO);
                log.info("INSERT: " + fileDTO);
            }
        });
    }

    private void createAndSaveFileSystemItem(FileDTO fileSystemItemDTO) {
        FileSystemItem fileSystemItem = buildFileSystemItem(fileSystemItemDTO);
        fileSystemItemRepository.save(fileSystemItem);
    }

    private FileSystemItem buildFileSystemItem(FileDTO fileSystemItemDTO) {
        FileSystemItem model = this.fileSystemItemMapper.toModel(fileSystemItemDTO);
        model.setJobStep(JobStepEnum.INSERTED.getStepNumber());
        associateMetaInfoEntity(model);
        this.getParentFileSystemItem(calculateParentBasePath(fileSystemItemDTO.getBasePath()), calculateParentName(fileSystemItemDTO.getBasePath()), JobStepEnum.INSERTED.getStepNumber())
                .ifPresent(model::setParent);
        return model;
    }

    private void associateMetaInfoEntity(FileSystemItem model) {
        if (model.getIsDirectory()) {
            return;
        }
        if (loadMetaInfoFromDB(model)) {
            return;
        }
        if (buildMetaInfoFromEbook(model)) {
            return;
        }
        buildMetaInfoFromWeb(model);
    }

    private void buildMetaInfoFromWeb(FileSystemItem model) {
        // load metadata from internet

    }

    private boolean loadMetaInfoFromDB(FileSystemItem model) {
        List<FileSystemItem> oldItems = this.fileSystemItemRepository.findBySha256AndJobStep(model.getSha256(), JobStepEnum.READY.getStepNumber());
        if (!oldItems.isEmpty() && oldItems.stream().anyMatch(oldItem -> oldItem.getFileMetaInfo() != null)) {
            model.setFileMetaInfo(oldItems.getFirst().getFileMetaInfo());
            return true;
        }
        return false;
    }

    private boolean buildMetaInfoFromEbook(FileSystemItem model) {
        String fullPath = model.getBasePath() + "/" + model.getName();
        log.info("checking for meta-info for file {}...", fullPath);
        return epubUtil.retrieveBook(fullPath)
                .map(book -> {
                    if (book.getMetadata().getFirstTitle().trim().isEmpty()) {
                        return false;
                    }
                    return manageCaseBookTitleNotEmpty(model, book, fullPath);
                }).orElse(false);

    }

    private boolean manageCaseBookTitleNotEmpty(FileSystemItem model, Book book, String fullPath) {
        log.info("gathering information from ebook {}", fullPath);
        FileMetaInfo fileMetaInfo = new FileMetaInfo();
        List<FileSystemItem> list = new ArrayList<>();
        list.add(model);
        fileMetaInfo.setFileSystemItemList(list);

        Metadata metadata = book.getMetadata();
        fileMetaInfo.setTitle(metadata.getFirstTitle());
        fileMetaInfo.setDescription(this.getFirst(metadata.getDescriptions()));

        BookInfo bookInfo = buildBookInfo(book, fileMetaInfo, metadata);

        fileMetaInfo.setBookInfo(bookInfo);
        model.setFileMetaInfo(fileMetaInfo);

        return true;
    }

    private BookInfo buildBookInfo(Book book, FileMetaInfo fileMetaInfo, Metadata metadata) {
        BookInfo bookInfo = new BookInfo();
        bookInfo.setFileMetaInfo(fileMetaInfo);
        bookInfo.setNote(metadata.getLanguage());
        bookInfo.setIsbn(getFirst(metadata.getIdentifiers().stream().map(Identifier::getValue).toList()));
        bookInfo.setAuthors(String.join(", ", metadata.getAuthors()
                .stream()
                .map(author -> author.getFirstname() + " " + author.getLastname())
                .toList()));
        BookCodesDTO<Optional<String>, Optional<String>> bookCodes = this.epubUtil.retrieveISBN(book);
        setISBN(bookCodes, bookInfo);
        setSBN(bookCodes, bookInfo);
        bookInfo.setPublisher(String.join(", ", metadata.getPublishers()));
        return bookInfo;
    }

    private static void setISBN(BookCodesDTO<Optional<String>, Optional<String>> bookCodes, BookInfo bookInfo) {
        bookCodes.getIsbn()
                .ifPresent(isbn -> {
                    bookInfo.setIsbn(isbn);
                    log.info("ISBN found: {}", isbn);
                });
    }

    private static void setSBN(BookCodesDTO<Optional<String>, Optional<String>> bookCodes, BookInfo bookInfo) {
        bookCodes.getSbn()
                .ifPresent(sbn -> {
                    bookInfo.setSbn(sbn);
                    log.info("SBN found: {}", sbn);
                });
    }

    private String getFirst(List<String> list) {
        if (list != null && !list.isEmpty()) {
            list.getFirst();
        }
        return null;
    }

    private String calculateParentName(String basePath) {
        return new File(basePath).getName();
    }

    private String calculateParentBasePath(String basePath) {
        return new File(basePath).getParentFile().getAbsolutePath();
    }

    private Optional<FileSystemItem> getParentFileSystemItem(String basePath, String name, int jobStep) {
        return this.fileSystemItemRepository.findByBasePathAndNameAndJobStep(basePath, name, jobStep);
    }
}
