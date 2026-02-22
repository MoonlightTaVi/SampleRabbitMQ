package com.github.tavi.srmq.dto;

import java.sql.Date;

import com.github.tavi.srmq.annotations.MaxDecimalPlaces;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotNull(message = "The sender name must not be null")
    private String senderName;
    /**
     * The person who receives the money.
     */
    @NotNull(message = "The receiver name must not be null")
    private String receiverName;
    /**
     * The amount of money transaction.
     */
    @MaxDecimalPlaces(value = 2, message = "The number of decimal places for the money transaction must be at most 2 (e.g. $0.01)")
    @Positive(message = "The amount of money must be positive")
    @NotNull(message = "The amount of money must not be null")
    private float amount;

    /**
     * The date of the transaction.
     */
    @NotNull(message = "The date must not be null")
    private Date date;


    /**
     * Sets the date of the transaction to the current date/time
     * (calculated at the moment of the method call).
     */
    public void setCurrentDate() {
        date = new Date(System.currentTimeMillis());
    }

}
