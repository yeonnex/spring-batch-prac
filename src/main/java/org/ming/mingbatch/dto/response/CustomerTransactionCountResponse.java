package org.ming.mingbatch.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CustomerTransactionCountResponse {
    private Long customerId;
    private Long count;
}
