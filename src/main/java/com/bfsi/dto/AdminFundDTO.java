package com.bfsi.dto;

/**
 * DTO used to display Mutual Fund details in Admin views
 */
public class AdminFundDTO {

    private String fundId;
    private String name;
    private String category;
    private String risk;
    private String status;

    public AdminFundDTO(String fundId,
                        String name,
                        String category,
                        String risk,
                        String status) {
        this.fundId = fundId;
        this.name = name;
        this.category = category;
        this.risk = risk;
        this.status = status;
    }

    public String getFundId() {
        return fundId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getRisk() {
        return risk;
    }

    public String getStatus() {
        return status;
    }
}