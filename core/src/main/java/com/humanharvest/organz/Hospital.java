package com.humanharvest.organz;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.humanharvest.organz.utilities.algorithms.DistanceCalculation;
import com.humanharvest.organz.utilities.enums.Organ;
import com.humanharvest.organz.utilities.enums.Region;
import com.humanharvest.organz.views.client.Views;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * A hospital, used to mark where a client's local hospital is and what hospitals can do what transplants.
 * Also used to record where transplants take place.
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
@Entity
@Table
@Access(AccessType.FIELD)
public class Hospital {

    /**
     * The speed of the BK 117 helicopter in km/h, as used by NZ Emergency Services.
     */
    private static final double DEFAULT_HELICOPTER_SPEED = 250;

    @Id
    @GeneratedValue
    @JsonView(Views.Overview.class)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Config_id")
    @JsonBackReference
    private Config config;

    @JsonView(Views.Overview.class)
    private String name;
    @JsonView(Views.Overview.class)
    private double latitude = Double.NaN;
    @JsonView(Views.Overview.class)
    private double longitude = Double.NaN;
    @JsonView(Views.Details.class)
    private String address;

    /*
     * A set of organs that the hospital is registered to transplant.
     */
    @ElementCollection(targetClass = Organ.class)
    @Enumerated(EnumType.STRING)
    @JsonView(Views.Details.class)
    private Set<Organ> transplantPrograms;

    protected Hospital() {
    }

    public Hospital(String name, double latitude, double longitude, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        transplantPrograms = new HashSet<>();
    }

    public Hospital(String name, double latitude, double longitude, String address, Set<Organ> transplantPrograms) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.transplantPrograms = new HashSet<>(transplantPrograms);
    }

    /**
     * Returns a set of hospitals.
     * This was generated by getting the list of public hospitals from the Ministry of Health NZ
     * that do surgery, and using a Google Maps API to generate coords for their addresses.
     *
     * @return a set of hospitals
     */
    public static Set<Hospital> getDefaultHospitals() {
        Set<Hospital> hospitals = new HashSet<>(Arrays.asList(
                new Hospital("Auckland City Hospital", -36.8604597, 174.7691264,
                        "2 Park Road, Grafton, Auckland 1023"),
                new Hospital("Greenlane Clinical Centre", -36.8944687, 174.7805867,
                        "214 Green Lane West, Epsom, Auckland 1051"),
                new Hospital("Opotiki Health Care Centre", -38.0066033, 177.2854971,
                        "32A King Street, Opotiki 3122"),
                new Hospital("Tauranga Hospital", -37.7073537, 176.1498893,
                        "829 Cameron Road, Tauranga South, Tauranga 3112"),
                new Hospital("Whakatane Hospital", -37.963953, 176.9784283,
                        "33 Stewart Street, Whakatane 3120"),
                new Hospital("Ashburton Hospital", -43.8941404, 171.7458235,
                        "34 Elizabeth Street, Allenton, Ashburton 7700"),
                new Hospital("Burwood Hospital", -43.480515, 172.686152,
                        "255 Mairehau Road, Burwood, Christchurch 8083"),
                new Hospital("Christchurch Hospital", -43.5336199, 172.626228,
                        "2 Riccarton Avenue, Christchurch Central, Christchurch 8011"),
                new Hospital("Kenepuru Hospital", -41.1466071, 174.8328923,
                        "16 Hospital Drive, Porirua 5022"),
                new Hospital("Wellington Hospital", -41.3085774, 174.7790445,
                        "39 Riddiford Street, Newtown, Wellington 6021"),
                new Hospital("Manukau Surgery Centre", -37.0080154, 174.8881918,
                        "901 Great South Road, Wiri, Auckland 2104"),
                new Hospital("Middlemore Hospital", -36.9624296, 174.8397468,
                        "100 Hospital Road, Mangere East, Auckland 2025"),
                new Hospital("Hawke's Bay Hospital", -39.6282509, 176.8251802,
                        "210 Omahu Road, Camberley, Hastings 4120"),
                new Hospital("Hutt Valley Hospital", -41.2044069, 174.924764,
                        "638 High Street, Boulcott, Lower Hutt 5010"),
                new Hospital("Rotorua Hospital", -38.1348322, 176.2481062,
                        "Corner of Arawa Street and Pukeroa Hill, Rotorua 3010"),
                new Hospital("Taupo Hospital", -38.6986352, 176.0972201,
                        "38 Kotare Street, Hilltop, Taupo 3330"),
                new Hospital("Palmerston North Hospital", -40.33893860000001, 175.6206208,
                        "50 Ruahine Street, Roslyn, Palmerston North 4414"),
                new Hospital("Nelson Hospital", -41.2869557, 173.2737342,
                        "98 Waimea Road, Nelson South, Nelson 7010"),
                new Hospital("Wairau Hospital", -41.5346037, 173.9459112,
                        "30 Hospital Road, Witherlea, Blenheim 7201"),
                new Hospital("Bay of Islands Hospital", -35.3846167, 174.0689102,
                        "21 Hospital Road, Kawakawa 0210"),
                new Hospital("Dargaville Hospital", -35.9308237, 173.8713222,
                        "73 Awakino Road, Dargaville 0310"),
                new Hospital("Kaitaia Hospital", -35.1187972, 173.2614198,
                        "29 Redan Road, Kaitaia 0410"),
                new Hospital("Whangarei Hospital", -35.7342485, 174.3057151,
                        "115 Maunu Road, Woodhill, Whangarei 0110"),
                new Hospital("Timaru Hospital", -44.4084906, 171.2563595,
                        "14 Queen Street, Parkside, Timaru 7910"),
                new Hospital("Dunedin Hospital", -45.86901899999999, 170.5083971,
                        "275 Great King Street, Dunedin Central, Dunedin 9016"),
                new Hospital("Southland Hospital", -46.4369203, 168.3594001,
                        "145 Kew Road, Kew, Invercargill 9812"),
                new Hospital("Gisborne Hospital", -38.6407772, 178.0025945,
                        "421 Ormond Road, Lytton West, Gisborne 4010"),
                new Hospital("Taranaki Base Hospital", -39.0744636, 174.0578096,
                        "118 Tukapa Street, Westown, New Plymouth 4310"),
                new Hospital("Thames Hospital", -37.1368733, 175.5429171,
                        "606 MacKay Street, Thames 3500"),
                new Hospital("Wairarapa Hospital", -40.9503828, 175.6908933,
                        "20 Te Ore Ore Road, Lansdowne, Masterton 5910"),
                new Hospital("Elective Surgery Centre", -36.781103, 174.7570023,
                        "124 Shakespeare Road, Takapuna, Auckland 0622"),
                new Hospital("North Shore Hospital", -36.77966809999999, 174.7566364,
                        "132 Shakespeare Road, Takapuna, Auckland 0622"),
                new Hospital("Waitakere Hospital", -36.870436, 174.628378,
                        "55 Lincoln Road, Henderson, Auckland 0610"),
                new Hospital("Buller Health", -41.7466328, 171.6043599,
                        "45 Derby Street, Westport 7825"),
                new Hospital("Grey Base Hospital", -42.4631061, 171.1918604,
                        "71 Water Walk Road, Greymouth 7805"),
                new Hospital("Reefton Health Services", -42.1191959, 171.8604725,
                        "112 Broadway, Reefton 7830"),
                new Hospital("Whanganui Hospital", -39.9451776, 175.0369961,
                        "100 Heads Road, Gonville, Wanganui 4501")
        ));

        for (Hospital hospital : hospitals) {
            for (Organ organ : Organ.values()) {
                hospital.addTransplantProgramFor(organ);
            }
        }

        return hospitals;
    }

    /**
     * Return the hospital nearest to a given region
     *
     * @param region The region to find the closest hospital
     * @param hospitals The hospitals to check
     * @return The nearest hospital. Will be null if the iterator contained no hospitals
     */
    public static Hospital getNearestHospitalToRegion(Region region, Iterable<Hospital> hospitals) {
        Hospital nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Hospital hospital : hospitals) {
            double distance = hospital.calculateDistanceTo(region);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = hospital;
            }
        }
        return nearest;
    }

    /**
     * Given a Client and a list of Hospitals, find the Hospital for the Client
     * Will default to the Clients Hospital if they have one, otherwise will use their region string,
     * attempt to convert that to one of the Region ENUM items, and if so will find the nearest hospital to that
     * regions from the Iterable given
     *
     * @param client The client to check
     * @param hospitals The Iterable collection of Hospitals to check
     * @return The nearest Hospital, or null if the Client has no Hospital or Region
     */
    public static Hospital getHospitalForClient(Client client, Iterable<Hospital> hospitals) {
        if (client == null) {
            return null;
        } else if (client.getHospital() != null) {
            return client.getHospital();
        } else {
            try {
                Region recipientRegion = Region.fromString(client.getRegion());
                return Hospital.getNearestHospitalToRegion(recipientRegion, hospitals);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Set<Organ> getTransplantPrograms() {
        if (transplantPrograms == null) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(transplantPrograms);
    }

    public void setTransplantPrograms(Set<Organ> transplantPrograms) {
        this.transplantPrograms = new HashSet<>(transplantPrograms);
    }

    /**
     * @return true if transplantPrograms is null
     */
    public boolean transplantProgramsIsNull() {
        return transplantPrograms == null;
    }

    public boolean addTransplantProgramFor(Organ organ) {
        return transplantPrograms.add(organ);
    }

    public boolean removeTransplantProgramFor(Organ organ) {
        return transplantPrograms.remove(organ);
    }

    public boolean hasTransplantProgram(Organ organ) {
        return transplantPrograms.contains(organ);
    }

    /**
     * Calculates and returns the Haversine distance between the current and the given hospitals in km
     *
     * @param hospital hospital to find the distance to from the current one
     * @return distance in km between the two hospitals
     */
    public double calculateDistanceTo(Hospital hospital) {
        return DistanceCalculation.distanceBetweenInKm(latitude, longitude,
                hospital.getLatitude(), hospital.getLongitude());
    }

    /**
     * Calculates and returns the Haversine distance between this hospital and the given region
     *
     * @param region region to find the distance to
     * @return distance in km between this hospital and the given region
     */
    public double calculateDistanceTo(Region region) {
        return DistanceCalculation.distanceBetweenInKm(latitude, longitude,
                region.getLatitude(), region.getLongitude());
    }

    /**
     * Returns the period of time it would take a helicopter at the given speed to the other hospital.
     * Ignores spool up/down time and assumes a straight line with no wind.
     *
     * @param otherHospital The destination hospital.
     * @param speedInKMH The speed in km/h.
     */
    public Duration calculateTimeTo(Hospital otherHospital, double speedInKMH) {
        double distanceKM = calculateDistanceTo(otherHospital);
        double hours = distanceKM / speedInKMH;
        return Duration.ofSeconds((long) (hours * 60 * 60));
    }

    /**
     * Returns the period of time it would take a helicopter at default speed to the other hospital.
     * Ignores spool up/down time and assumes a straight line with no wind.
     *
     * @param otherHospital The destination hospital.
     */
    public Duration calculateTimeTo(Hospital otherHospital) {
        return calculateTimeTo(otherHospital, DEFAULT_HELICOPTER_SPEED);
    }

    /**
     * Return the nearest hospital that can transplant the given organ
     *
     * @param organ The organ required for transplant
     * @param hospitals The other hospitals to check
     * @return The nearest hospital. Will be null if there are no valid hospitals
     */
    public Hospital getNearestWithTransplantProgram(Organ organ, Iterable<Hospital> hospitals) {
        if (hasTransplantProgram(organ)) {
            return this;
        } else {
            Hospital nearest = null;
            double nearestDist = Double.MAX_VALUE;
            for (Hospital hospital : hospitals) {
                if (hospital.hasTransplantProgram(organ)) {
                    double distance = hospital.calculateDistanceTo(this);
                    if (distance < nearestDist) {
                        nearest = hospital;
                        nearestDist = distance;
                    }
                }
            }
            return nearest;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Hospital)) {
            return false;
        }
        Hospital hospital = (Hospital) obj;
        return Objects.equals(id, hospital.id) &&
                Objects.equals(name, hospital.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
