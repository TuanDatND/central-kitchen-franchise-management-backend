package com.CocOgreen.CenFra.MS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailDTO {
    private Integer detailId;
    private Integer productId;
<<<<<<< feature/Login-Authorize
=======
    private String productName;
>>>>>>> main
    private Integer quantity;
}
