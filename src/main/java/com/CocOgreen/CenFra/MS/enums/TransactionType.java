package com.CocOgreen.CenFra.MS.enums;

public enum TransactionType {
    // IMPORT: nhập kho từ phiếu nhập kho sản xuất
    // EXPORT: xuất kho theo đơn hàng cửa hàng (StoreOrder)
    // SURPLUS_EXPORT: xuất kho thặng dư - không gắn StoreOrder (lấy hàng dư, điều chỉnh tồn kho)
    // DISPOSAL: tiêu hủy lô hàng hết hạn
    IMPORT, EXPORT, SURPLUS_EXPORT, DISPOSAL

}
