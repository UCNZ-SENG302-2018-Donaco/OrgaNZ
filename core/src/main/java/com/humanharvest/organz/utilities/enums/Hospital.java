package com.humanharvest.organz.utilities.enums;

public enum Hospital {

    AUCKLAND_CITY_HOSPITAL("Auckland City Hospital", -36.8604597, 174.7691264, "2 Park Road, Grafton, Auckland 1023"),
    GREENLANE_CLINICAL_CENTRE("Greenlane Clinical Centre", -36.8944687, 174.7805867, "214 Green Lane West, Epsom, Auckland 1051"),
    OPOTIKI_HEALTH_CARE_CENTRE("Opotiki Health Care Centre", -38.0066033, 177.2854971, "32A King Street, Opotiki 3122"),
    TAURANGA_HOSPITAL("Tauranga Hospital", -37.7073537, 176.1498893, "829 Cameron Road, Tauranga South, Tauranga 3112"),
    WHAKATANE_HOSPITAL("Whakatane Hospital", -37.963953, 176.9784283, "33 Stewart Street, Whakatane 3120"),
    ASHBURTON_HOSPITAL("Ashburton Hospital", -43.8941404, 171.7458235, "34 Elizabeth Street, Allenton, Ashburton 7700"),
    BURWOOD_HOSPITAL("Burwood Hospital", -43.480515, 172.686152, "255 Mairehau Road, Burwood, Christchurch 8083"),
    CHRISTCHURCH_HOSPITAL("Christchurch Hospital", -43.5336199, 172.626228, "2 Riccarton Avenue, Christchurch Central, Christchurch 8011"),
    KENEPURU_HOSPITAL("Kenepuru Hospital", -41.1466071, 174.8328923, "16 Hospital Drive, Porirua 5022"),
    WELLINGTON_HOSPITAL("Wellington Hospital", -41.3085774, 174.7790445, "39 Riddiford Street, Newtown, Wellington 6021"),
    MANUKAU_SURGERY_CENTRE("Manukau Surgery Centre", -37.0080154, 174.8881918, "901 Great South Road, Wiri, Auckland 2104"),
    MIDDLEMORE_HOSPITAL("Middlemore Hospital", -36.9624296, 174.8397468, "100 Hospital Road, Mangere East, Auckland 2025"),
    HAWKES_BAY_HOSPITAL("Hawke's Bay Hospital", -39.6282509, 176.8251802, "210 Omahu Road, Camberley, Hastings 4120"),
    HUTT_VALLEY_HOSPITAL("Hutt Valley Hospital", -41.2044069, 174.924764, "638 High Street, Boulcott, Lower Hutt 5010"),
    ROTORUA_HOSPITAL("Rotorua Hospital", -38.1348322, 176.2481062, "Corner of Arawa Street and Pukeroa Hill, Rotorua 3010"),
    TAUPO_HOSPITAL("Taupo Hospital", -38.6986352, 176.0972201, "38 Kotare Street, Hilltop, Taupo 3330"),
    PALMERSTON_NORTH_HOSPITAL("Palmerston North Hospital", -40.33893860000001, 175.6206208, "50 Ruahine Street, Roslyn, Palmerston North 4414"),
    NELSON_HOSPITAL("Nelson Hospital", -41.2869557, 173.2737342, "98 Waimea Road, Nelson South, Nelson 7010"),
    WAIRAU_HOSPITAL("Wairau Hospital", -41.5346037, 173.9459112, "30 Hospital Road, Witherlea, Blenheim 7201"),
    BAY_OF_ISLANDS_HOSPITAL("Bay of Islands Hospital", -35.3846167, 174.0689102, "21 Hospital Road, Kawakawa 0210"),
    DARGAVILLE_HOSPITAL("Dargaville Hospital", -35.9308237, 173.8713222, "73 Awakino Road, Dargaville 0310"),
    KAITAIA_HOSPITAL("Kaitaia Hospital", -35.1187972, 173.2614198, "29 Redan Road, Kaitaia 0410"),
    WHANGAREI_HOSPITAL("Whangarei Hospital", -35.7342485, 174.3057151, "115 Maunu Road, Woodhill, Whangarei 0110"),
    TIMARU_HOSPITAL("Timaru Hospital", -44.4084906, 171.2563595, "14 Queen Street, Parkside, Timaru 7910"),
    DUNEDIN_HOSPITAL("Dunedin Hospital", -45.86901899999999, 170.5083971, "275 Great King Street, Dunedin Central, Dunedin 9016"),
    SOUTHLAND_HOSPITAL("Southland Hospital", -46.4369203, 168.3594001, "145 Kew Road, Kew, Invercargill 9812"),
    GISBORNE_HOSPITAL("Gisborne Hospital", -38.6407772, 178.0025945, "421 Ormond Road, Lytton West, Gisborne 4010"),
    TARANAKI_BASE_HOSPITAL("Taranaki Base Hospital", -39.0744636, 174.0578096, "118 Tukapa Street, Westown, New Plymouth 4310"),
    THAMES_HOSPITAL("Thames Hospital", -37.1368733, 175.5429171, "606 MacKay Street, Thames 3500"),
    WAIRARAPA_HOSPITAL("Wairarapa Hospital", -40.9503828, 175.6908933, "20 Te Ore Ore Road, Lansdowne, Masterton 5910"),
    ELECTIVE_SURGERY_CENTRE("Elective Surgery Centre", -36.781103, 174.7570023, "124 Shakespeare Road, Takapuna, Auckland 0622"),
    NORTH_SHORE_HOSPITAL("North Shore Hospital", -36.77966809999999, 174.7566364, "132 Shakespeare Road, Takapuna, Auckland 0622"),
    WAITAKERE_HOSPITAL("Waitakere Hospital", -36.870436, 174.628378, "55 Lincoln Road, Henderson, Auckland 0610"),
    BULLER_HEALTH("Buller Health", -41.7466328, 171.6043599, "45 Derby Street, Westport 7825"),
    GREY_BASE_HOSPITAL("Grey Base Hospital", -42.4631061, 171.1918604, "71 Water Walk Road, Greymouth 7805"),
    REEFTON_HEALTH_SERVICES("Reefton Health Services", -42.1191959, 171.8604725, "112 Broadway, Reefton 7830"),
    WHANGANUI_HOSPITAL("Whanganui Hospital", -39.9451776, 175.0369961, "100 Heads Road, Gonville, Wanganui 4501");

    private final String name;
    private final double latitude;
    private final double longitude;
    private final  String address;

    Hospital(String name, double latitude, double longitude, String address) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    @Override
    public String toString() {
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
}
