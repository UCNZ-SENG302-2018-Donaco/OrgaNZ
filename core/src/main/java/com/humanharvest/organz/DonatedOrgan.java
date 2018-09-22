package com.humanharvest.organz;

import static com.humanharvest.organz.utilities.enums.DonatedOrganSortOptionsEnum.TIME_UNTIL_EXPIRY;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Comparator;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.humanharvest.organz.utilities.enums.DonatedOrganSortOptionsEnum;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.views.client.Views;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@Entity
@Table
public class DonatedOrgan {

    @Id
    @GeneratedValue
    @JsonView(Views.Overview.class)
    private Long id;
    @JsonView(Views.Overview.class)
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
    @JsonView(Views.Overview.class)
    private LocalDateTime dateTimeOfDonation;
    @JsonView(Views.Overview.class)
    private String overrideReason;  // If null this implies the organ was not manually overriden
    @JsonView(Views.Overview.class)
    private boolean available = true;

    protected DonatedOrgan() {
    }

    public DonatedOrgan(Organ organType, Client donor, LocalDateTime dateTimeOfDonation) {
        this.organType = organType;
        this.donor = donor;
        this.dateTimeOfDonation = dateTimeOfDonation;
    }

    public DonatedOrgan(Organ organType, Client donor, LocalDateTime dateTimeOfDonation, Long id) {
        this.organType = organType;
        this.donor = donor;
        this.dateTimeOfDonation = dateTimeOfDonation;
        this.id = id;
    }

    public Organ getOrganType() {
        return organType;
    }

    public Client getDonor() {
        return donor;
    }

    public void setDonor(Client donor) {
        this.donor = donor;
    }

    public Client getReceiver() {
        return receiver;
    }

    public void setReceiver(Client receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getDateTimeOfDonation() {
        return dateTimeOfDonation;
    }

    public void setDateTimeOfDonation(LocalDateTime dateTimeOfDonation) {
        this.dateTimeOfDonation = dateTimeOfDonation;
    }

    /**
     * @return the {@link Duration} between the datetime of donation and the current datetime
     */
    private Duration getTimeSinceDonation() {
        return Duration.between(dateTimeOfDonation, LocalDateTime.now());
    }

    /**
     * @return true if the organ has expired
     */
    public boolean hasExpired() {
        return getDurationUntilExpiry() == Duration.ZERO;
    }

    /**
     * @return the duration; except if the organ has expired or is overridden, then it returns Duration.ZERO
     */
    public Duration getDurationUntilExpiry() {
        if (organType.getMaxExpiration() == null) {
            return null;
        }
        Duration timeToExpiry = organType.getMaxExpiration().minus(getTimeSinceDonation());
        return timeToExpiry.isNegative() || getOverrideReason() != null ? Duration.ZERO : timeToExpiry;
    }

    /**
     * @return a decimal representation of how far along the organ is. starts at 0 (at time of death) and goes to 1.
     */
    public double getProgressDecimal() {
        Duration timeToExpiry = getDurationUntilExpiry();
        Duration expiration = getOrganType().getMaxExpiration();

        if (timeToExpiry == null) { // no expiration
            return -1;
        } else if (timeToExpiry.isZero()) { // expired
            return 1;
        } else {
            return 1 - (double) timeToExpiry.getSeconds() / expiration.getSeconds();
        }
    }

    /**
     * @return a decimal representation of when the organ enters the lower bound (e.g. 0.5 for halfway, or 0.9 for
     * near the end)
     */
    public double getFullMarker() {
        Duration min = getOrganType().getMinExpiration();
        Duration max = getOrganType().getMaxExpiration();
        if (min == null || max == null) {
            return 1;
        } else {
            return (double) min.getSeconds() / max.getSeconds();
        }
    }

    public Long getId() {
        return id;
    }

    public String getOverrideReason() {
        return overrideReason;
    }

    /**
     * Sets the override reason for this donated organ and tells the donor that one of its organs overridden status
     * has changed.
     *
     * @param overrideReason the reason for overriding the organ,
     */
    public void manuallyOverride(String overrideReason) {
        this.overrideReason = overrideReason;
        donor.updateHasOverriddenOrgans();
    }

    /**
     * Removes the override reason for this donated organ and tells the donor that one of its organs is no
     * longer overridden.
     */
    public void cancelManualOverride() {
        this.overrideReason = null;
        donor.updateHasOverriddenOrgans();
    }

    /**
     * Returns the comparator that matches the sort option
     *
     * @param sortOption the sort option
     * @return the comparator that matches the sort option
     */
    public static Comparator<DonatedOrgan> getComparator(DonatedOrganSortOptionsEnum sortOption) {
        if (sortOption == null) {
            sortOption = TIME_UNTIL_EXPIRY;
        }

        switch (sortOption) {
            case CLIENT:
                return Comparator.comparing(organ -> organ.getDonor().getFullName());
            case ORGAN_TYPE:
                return Comparator.comparing(organ -> organ.getOrganType().toString());
            case REGION_OF_DEATH:
                return Comparator.comparing(organ -> organ.getDonor().getRegionOfDeath());
            case TIME_OF_DEATH:
                return Comparator.comparing(organ -> organ.getDonor().getDateOfDeath());
            default:
            case TIME_UNTIL_EXPIRY:
                return Comparator.comparing(DonatedOrgan::getDurationUntilExpiry,
                        Comparator.nullsLast(Comparator.naturalOrder()));
        }
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DonatedOrgan)) {
            return false;
        }
        DonatedOrgan that = (DonatedOrgan) o;
        return Objects.equals(id, that.id) &&
                organType == that.organType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, organType);
    }
}
