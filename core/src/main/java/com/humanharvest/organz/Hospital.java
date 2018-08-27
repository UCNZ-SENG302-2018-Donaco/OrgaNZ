package com.humanharvest.organz;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.humanharvest.organz.utilities.enums.Organ;

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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Access(AccessType.FIELD)
public class Hospital {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Config_id")
    @JsonBackReference
    private Config config;

    // todo should these be @tagged?
    private String name;
    private double latitude;
    private double longitude;
    private String address;

    @ElementCollection(targetClass = Organ.class)
    @Enumerated(EnumType.STRING)
    private Collection<Organ> organs;


    protected Hospital() {
    }

    public Hospital(String name, double latitude, double longitude, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.organs = new HashSet<>();
    }

    public Hospital(String name, double latitude, double longitude, String address, Collection<Organ> organs) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.organs = organs;
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

    public Collection<Organ> getOrgans() {
        return organs;
    }

    public void setOrgans(Collection<Organ> organs) {
        this.organs = new HashSet<>(organs);
    }

    public boolean addOrgan(Organ organ) {
        return organs.add(organ);
    }

    public boolean removeOrgan(Organ organ) {
        return organs.remove(organ);
    }

    public static Set<Hospital> getDefaultHospitals() {
        Set<Hospital> hospitals = new HashSet<>();
        hospitals.add(new Hospital("Auckland City Hospital", -36.8604597, 174.7691264, "2 Park Road, Grafton, Auckland 1023"));
        hospitals.add(new Hospital("Greenlane Clinical Centre", -36.8944687, 174.7805867, "214 Green Lane West, Epsom, Auckland 1051"));
        hospitals.add(new Hospital("Opotiki Health Care Centre", -38.0066033, 177.2854971, "32A King Street, Opotiki 3122"));
        hospitals.add(new Hospital("Tauranga Hospital", -37.7073537, 176.1498893, "829 Cameron Road, Tauranga South, Tauranga 3112"));
        hospitals.add(new Hospital("Whakatane Hospital", -37.963953, 176.9784283, "33 Stewart Street, Whakatane 3120"));
        hospitals.add(new Hospital("Ashburton Hospital", -43.8941404, 171.7458235, "34 Elizabeth Street, Allenton, Ashburton 7700"));
        hospitals.add(new Hospital("Burwood Hospital", -43.480515, 172.686152, "255 Mairehau Road, Burwood, Christchurch 8083"));
        hospitals.add(new Hospital("Christchurch Hospital", -43.5336199, 172.626228, "2 Riccarton Avenue, Christchurch Central, Christchurch 8011"));
        hospitals.add(new Hospital("Kenepuru Hospital", -41.1466071, 174.8328923, "16 Hospital Drive, Porirua 5022"));
        hospitals.add(new Hospital("Wellington Hospital", -41.3085774, 174.7790445, "39 Riddiford Street, Newtown, Wellington 6021"));
        hospitals.add(new Hospital("Manukau Surgery Centre", -37.0080154, 174.8881918, "901 Great South Road, Wiri, Auckland 2104"));
        hospitals.add(new Hospital("Middlemore Hospital", -36.9624296, 174.8397468, "100 Hospital Road, Mangere East, Auckland 2025"));
        hospitals.add(new Hospital("Hawke's Bay Hospital", -39.6282509, 176.8251802, "210 Omahu Road, Camberley, Hastings 4120"));
        hospitals.add(new Hospital("Hutt Valley Hospital", -41.2044069, 174.924764, "638 High Street, Boulcott, Lower Hutt 5010"));
        hospitals.add(new Hospital("Rotorua Hospital", -38.1348322, 176.2481062, "Corner of Arawa Street and Pukeroa Hill, Rotorua 3010"));
        hospitals.add(new Hospital("Taupo Hospital", -38.6986352, 176.0972201, "38 Kotare Street, Hilltop, Taupo 3330"));
        hospitals.add(new Hospital("Palmerston North Hospital", -40.33893860000001, 175.6206208, "50 Ruahine Street, Roslyn, Palmerston North 4414"));
        hospitals.add(new Hospital("Nelson Hospital", -41.2869557, 173.2737342, "98 Waimea Road, Nelson South, Nelson 7010"));
        hospitals.add(new Hospital("Wairau Hospital", -41.5346037, 173.9459112, "30 Hospital Road, Witherlea, Blenheim 7201"));
        hospitals.add(new Hospital("Bay of Islands Hospital", -35.3846167, 174.0689102, "21 Hospital Road, Kawakawa 0210"));
        hospitals.add(new Hospital("Dargaville Hospital", -35.9308237, 173.8713222, "73 Awakino Road, Dargaville 0310"));
        hospitals.add(new Hospital("Kaitaia Hospital", -35.1187972, 173.2614198, "29 Redan Road, Kaitaia 0410"));
        hospitals.add(new Hospital("Whangarei Hospital", -35.7342485, 174.3057151, "115 Maunu Road, Woodhill, Whangarei 0110"));
        hospitals.add(new Hospital("Timaru Hospital", -44.4084906, 171.2563595, "14 Queen Street, Parkside, Timaru 7910"));
        hospitals.add(new Hospital("Dunedin Hospital", -45.86901899999999, 170.5083971, "275 Great King Street, Dunedin Central, Dunedin 9016"));
        hospitals.add(new Hospital("Southland Hospital", -46.4369203, 168.3594001, "145 Kew Road, Kew, Invercargill 9812"));
        hospitals.add(new Hospital("Gisborne Hospital", -38.6407772, 178.0025945, "421 Ormond Road, Lytton West, Gisborne 4010"));
        hospitals.add(new Hospital("Taranaki Base Hospital", -39.0744636, 174.0578096, "118 Tukapa Street, Westown, New Plymouth 4310"));
        hospitals.add(new Hospital("Thames Hospital", -37.1368733, 175.5429171, "606 MacKay Street, Thames 3500"));
        hospitals.add(new Hospital("Wairarapa Hospital", -40.9503828, 175.6908933, "20 Te Ore Ore Road, Lansdowne, Masterton 5910"));
        hospitals.add(new Hospital("Elective Surgery Centre", -36.781103, 174.7570023, "124 Shakespeare Road, Takapuna, Auckland 0622"));
        hospitals.add(new Hospital("North Shore Hospital", -36.77966809999999, 174.7566364, "132 Shakespeare Road, Takapuna, Auckland 0622"));
        hospitals.add(new Hospital("Waitakere Hospital", -36.870436, 174.628378, "55 Lincoln Road, Henderson, Auckland 0610"));
        hospitals.add(new Hospital("Buller Health", -41.7466328, 171.6043599, "45 Derby Street, Westport 7825"));
        hospitals.add(new Hospital("Grey Base Hospital", -42.4631061, 171.1918604, "71 Water Walk Road, Greymouth 7805"));
        hospitals.add(new Hospital("Reefton Health Services", -42.1191959, 171.8604725, "112 Broadway, Reefton 7830"));
        hospitals.add(new Hospital("Whanganui Hospital", -39.9451776, 175.0369961, "100 Heads Road, Gonville, Wanganui 4501"));
        return hospitals;
    }
}
