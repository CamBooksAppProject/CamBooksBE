package org.example.v1.post.usedTrade.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.v1.post.usedTrade.domain.TradeMethod;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsedTradeRequestDto {
    private String title;
    private String content;
    private int price;
    private String isbn;
    private TradeMethod tradeMethod;
}
