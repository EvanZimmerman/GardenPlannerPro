package dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects;

import android.util.Log;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dyly on 11/4/15.
 */
public class PlantHistoryTableData implements Serializable {
    private int id;
    private Date date;
    private String note;

    public PlantHistoryTableData()
    {
        this.id = -1;
        this.date = null;
        this.note = null;
    }

    public PlantHistoryTableData(int id, Date date, String note) {
        this.id = id;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * used for database retrieval of code
     * @param date string to be formated to a date
     */
    public void setDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            Log.d("Date for Plant History", e.getMessage());
        }

        this.date = convertedDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
