package com.bfsi.dto;

/**
 * DTO for Admin fund promotion / demotion
 */
public class FundPromotionDTO {

    private String fundId;
    private String promotionStatus;

    public FundPromotionDTO(String fundId, String promotionStatus) {
        this.fundId = fundId;
        this.promotionStatus = promotionStatus;
    }

    public String getFundId() {
        return fundId;
    }

    public String getPromotionStatus() {
        return promotionStatus;
    }
}