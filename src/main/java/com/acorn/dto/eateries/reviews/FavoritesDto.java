package com.acorn.dto.eateries.reviews;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *
 * @author jaeuk-choi
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoritesDto {

    private int memberNo;
    private int status; // 0: 비활성, 1: 활성
}
