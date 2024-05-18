package com.andreidodu.europealibrary.dto.common;

import com.andreidodu.europealibrary.dto.BookInfoDTO;
import com.andreidodu.europealibrary.dto.TagDTO;
import com.andreidodu.europealibrary.dto.stripe.StripePriceDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FileMetaInfoDTO extends CommonDTO {
    protected String title;
    protected String description;
    private List<TagDTO> tagList;
    private BookInfoDTO bookInfo;
    private StripePriceDTO stripePrice;
    private Boolean onSale;
    private Boolean hidden;
    private Boolean downloadable;
}
