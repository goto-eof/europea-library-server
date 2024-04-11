package com.andreidodu.europealibrary.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class GoogleBookResponseDTO {
    List<GoogleBookItemDTO> items;
    private String kind;
    private int totalItems;

    @Getter
    @Setter
    @ToString
    public static class GoogleBookItemDTO {

        private String kind;
        private VolumeInfoDTO volumeInfo;


        @Getter
        @Setter
        @ToString
        public static class VolumeInfoDTO {
            private String title;
            private List<String> authors;
            private String publisher;
            private String publishedDate;
            private String description;
            private List<IndustryIdentifierDTO> industryIdentifiers;
            private Integer pageCount;
            private List<String> categories;
            private Double averageRating;
            private Long ratingsCount;
            private String language;
            private ImageLinksDTO imageLinks;

            @Getter
            @Setter
            @ToString
            public static class IndustryIdentifierDTO {
                private String type;
                private String identifier;
            }

            @Getter
            @Setter
            public static class ImageLinksDTO {
                private String smallThumbnail;
                private String thumbnail;
            }
        }
    }
}
