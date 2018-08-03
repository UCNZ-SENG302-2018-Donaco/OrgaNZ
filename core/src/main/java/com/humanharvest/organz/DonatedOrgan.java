package com.humanharvest.organz;

import java.time.Duration;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.humanharvest.organz.utilities.enums.Organ;

@Entity
@Table
public class DonatedOrgan {

    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private Organ organType;
    @ManyToOne
    @JoinColumn(name = "donor_uid")
    @JsonBackReference(value = "donatedOrgan")
    private Client donor;
    @ManyToOne
    @JoinColumn(name = "receiver_uid")
    @JsonBackReference(value = "receivedOrgan")
    private Client receiver;
    private LocalDateTime dateTimeOfDonation;

    protected DonatedOrgan() {
    }

    public DonatedOrgan(Organ organType, Client donor, LocalDateTime dateTimeOfDonation) {
        this.organType = organType;
        this.donor = donor;
        this.dateTimeOfDonation = dateTimeOfDonation;
    }

    public Organ getOrganType() {
        return organType;
    }

    public Client getDonor() {
        return donor;
    }

    public Client getReceiver() {
        return receiver;
    }

    public LocalDateTime getDateTimeOfDonation() {
        return dateTimeOfDonation;
    }

    private Duration getTimeSinceDonation() {
        return Duration.between(dateTimeOfDonation, LocalDateTime.now());
    }

    /**
     * @return if the organ hasn't expired: the duration. else: Duration.ZERO
     */
    public Duration getDurationUntilExpiry() {
        Duration timeToExpiry = organType.getMaxExpiration().minus(getTimeSinceDonation());
        return timeToExpiry.isNegative() ? Duration.ZERO : timeToExpiry;
    }

    /**
     * @return if the organ hasn't expired, based on the minimum expiry: the duration until the minimum expiry.
     * else: Duration.ZERO
     */
    public Duration getDurationUntilPartialExpiry() {
        Duration timeToExpiry = organType.getMinExpiration().minus(getTimeSinceDonation());
        return timeToExpiry.isNegative() ? Duration.ZERO : timeToExpiry;

    }

    /**
     * @return a decimal representation of how far along the organ is. starts at 0 (at time of death) and goes to 1.
     */
    public double getProgressDecimal() {
        Duration timeToExpiry = getDurationUntilExpiry();
        Duration expiration = getOrganType().getMaxExpiration();
        if (timeToExpiry.isZero()) {
            return 1;
        } else {
            return 1 - ((double) timeToExpiry.getSeconds() / expiration.getSeconds());
        }
    }

    /**
     * @return a decimal representation of how far along the organ is until the min expiry.
     * starts at 0 (at time of death) and goes to 1.
     */
    public double getProgressDecimalUntilMinExpiry() {
        Duration timeToMinExpiry = getDurationUntilPartialExpiry();
        Duration minExpiration = getOrganType().getMinExpiration();
        if (timeToMinExpiry.isZero()) {
            return 1;
        } else {
            return 1 - ((double) timeToMinExpiry.getSeconds() / minExpiration.getSeconds());
        }
    }
}
