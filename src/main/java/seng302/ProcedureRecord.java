package seng302;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Optional;

import seng302.Utilities.Enums.Organ;

/**
 * A record of a single procedure for a client.
 * Contains a summary, description, date and an optional affected organ.
 */
public class ProcedureRecord {
    public static final Comparator<ProcedureRecord> Comparator = (o1, o2) -> {
        int result = o1.date.compareTo(o2.date);
        if (result == 0) {
            return Integer.compare(o1.hashCode(), o2.hashCode());
        }
        return result;
    };

    private final String summary;
    private final String description;
    private final LocalDate date;
    private final Optional<Organ> affectedOrgan;

    public ProcedureRecord(String summary, String description, LocalDate date) {
        this.summary = summary;
        this.description = description;
        this.date = date;
        affectedOrgan = Optional.empty();
    }

    public ProcedureRecord(String summary, String description, LocalDate date, Organ affectedOrgan) {
        this.summary = summary;
        this.description = description;
        this.date = date;
        this.affectedOrgan = Optional.of(affectedOrgan);
    }

    /**
     * Returns the summary of this procedure.
     */
    public String getSummary() {
        return summary;
    }

    /**
     * Returns the description of this procedure.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the date of this procedure.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Returns an optional organ that this procedure affects.
     */
    public Optional<Organ> getAffectedOrgan() {
        return affectedOrgan;
    }

    @Override
    public String toString() {
        return String.format("ProcedureRecord{summary='%s', description='%s', date=%s, affectedOrgan=%s}",
                summary,
                description,
                date,
                affectedOrgan);
    }
}
