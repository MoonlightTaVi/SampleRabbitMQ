package com.github.tavi.srmq.dto;

import java.sql.Date;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * A basic HTTP request Data-Transfer-Object that represents
 * a money transaction between two users.
 */
@Data
@NoArgsConstructor
public class RequestDTO {

    /**
     * The person who sends the money.
     */
    private String senderName;
    /**
     * The person who receives the money.
     */
    private String receiverName;
    /**
     * The amount of money transaction.
     */
    private int amount;

    /**
     * The date of the transaction.
     */
    private Date date;

}
