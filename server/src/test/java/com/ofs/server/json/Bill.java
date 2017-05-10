package com.ofs.server.json;

import com.ofs.server.OFSServerId;
import com.ofs.server.model.OFSEntity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

public class Bill implements OFSEntity {

    private UUID id;
    private ZonedDateTime createdOn;

    private BigDecimal amount;
    private Date date;


    public Bill() { }

    public Bill(UUID id) {
        this.id = id;
    }

    @OFSServerId("/bill")
    public UUID id() { return id; }


    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }


    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    @Override
    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }
}
