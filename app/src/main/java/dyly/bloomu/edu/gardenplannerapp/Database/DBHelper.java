package dyly.bloomu.edu.gardenplannerapp.Database;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects.BedTableData;
import dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects.GardenTableData;
import dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects.HarvestHistoryTableData;
import dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects.HistoryTableData;
import dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects.ImageTableData;
import dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects.LayoutTableData;
import dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects.NoteTableData;
import dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects.PlantHistoryTableData;
import dyly.bloomu.edu.gardenplannerapp.Database.Database_Objects.WorkHistoryTableData;

/**
 * Created by Dyly on 10/29/15.
 */
public class DBHelper extends SQLiteOpenHelper {


    private static DBHelper mInstance = null;
    private static final String DATABASE_NAME = "GPlannerPro";
    private static final int DATABASE_VERSION = 6;
    //    public String GARDEN_CREATE_QUERY = "CREATE TABLE "+GardenTableData.TABLE_NAME+"( INT,"+ GardenTableData.bedID+" INT,"+
//            GardenTableData.noteID+" INT);";
    private SQLiteDatabase sdb;
    Context context;

    public static DBHelper getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        ctx.deleteDatabase(DATABASE_NAME);
        if (mInstance == null) {
            mInstance = new DBHelper(ctx.getApplicationContext());
        }
        return mInstance;

    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        //context.deleteDatabase(DATABASE_NAME);
        Log.d("Database operations", "Constructor");
    }


    @Override
    public void onCreate(SQLiteDatabase sdb) {
        this.sdb = sdb;
        this.executeSQLScript("SQLscript/create_tables.sql");
        this.executeSQLScript("SQLscript/insert_beds.sql");
        Log.d("Database operations", "Database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sdb, int oldVersion, int newVersion) {

        this.sdb = sdb;
        Log.d("Database operations", "Database Upgraded");

    }

    /**
     * Used to parse and execute SQL Scripts
     *
     * @param script the script to be used
     */
    private void executeSQLScript(String script) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            AssetManager assetManager = context.getAssets();
            for(String path:assetManager.list("SQLscript"))
            {
                System.out.println(path);
            }
            InputStream inputStream = null;
            inputStream = assetManager.open(script);
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            //splits text by semi colon from file
            String[] createTableScript = outputStream.toString().split(";");
            for (int i = 0; i < createTableScript.length; i++) {
                String sqlStatement = createTableScript[i].trim();
                if (sqlStatement.length() > 0) {
                    this.sdb.execSQL(sqlStatement + ";");
                }
            }
        } catch (IOException e) {
            Log.d("File error: ", e.getMessage());
        } catch (SQLException e) {
            Log.d("Database error: ", e.getMessage());
        }


    }

    public void setGardenTableData(GardenTableData gardenTableData) {
        try {
            this.sdb = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("bedID", gardenTableData.getBedID());
            contentValues.put("noteID", gardenTableData.getNoteID());
            this.sdb.insert("garden", null, contentValues);

            this.sdb.close();
        } catch (SQLException e) {
            Log.d("Database, Garden add", e.getMessage());
        }
        Log.d("Database", "Garden succesfully added");
    }

    public void setBedTableData(BedTableData bedTableData) {
        try {
            this.sdb = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("gardenID", bedTableData.getGardenID());
            this.sdb.insert("bed", null, contentValues);

            this.sdb.close();
        } catch (SQLException e) {
            Log.d("Database, Bed add", e.getMessage());
        }
        Log.d("Database", "Bed succesfully added");
    }

    public void setImageTableData(ImageTableData imageTableData) {
        try {
            this.sdb = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("image", imageTableData.getImage());
            contentValues.put("bedID", imageTableData.getBedID());
            contentValues.put("gardenID", imageTableData.getGardenID());
            this.sdb.insert("image", null, contentValues);

            this.sdb.close();
        } catch (SQLException e) {
            Log.d("Database, Image add", e.getMessage());
        }
        Log.d("Database", "Image succesfully added");
    }

    public void setLayoutTableData(LayoutTableData layoutTableData) {
        try {
            //this.sdb = getWritableDatabase();
            SQLiteDatabase tempSDB = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("image", layoutTableData.getImage());
            contentValues.put("bedID", layoutTableData.getBedID());
            this.sdb.insert("layout", null, contentValues);

            //this.sdb.close();
            tempSDB.close();
        }catch(SQLException e){
            Log.d("Database, Layout add", e.getMessage());
        }
        Log.d("Database", "Layout succesfully added");
    }

    public void setNoteTableData(NoteTableData noteTableData) {
        try {
            this.sdb = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("subject", noteTableData.getSubject());
            contentValues.put("note", noteTableData.getNote());
            contentValues.put("bedID", noteTableData.getBedID());
            contentValues.put("gardenID", noteTableData.getGardenID());
            contentValues.put("alert", noteTableData.getAlert());
            this.sdb.insert("note", null, contentValues);

            this.sdb.close();
        }catch(SQLException e){
            Log.d("Database, Note add",e.getMessage());
        }
        Log.d("Database", "Note succesfully added");
    }

    public void setHistoryTableData(HistoryTableData historyTableData) {
        try {
            this.sdb = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("havrvestHistoryID", historyTableData.getHarvestHistoryID());
            contentValues.put("plantHistoryID", historyTableData.getPlantHistoryID());
            contentValues.put("workHistoryID", historyTableData.getWorkHistoryID());
            contentValues.put("bedID", historyTableData.getBedID());
            this.sdb.insert("history", null, contentValues);

            this.sdb.close();
        }catch(SQLException e){
            Log.d("Database, History add", e.getMessage());
        }
        Log.d("Database", "History succesfully added");
    }

    public void setPlantHistoryTableData(PlantHistoryTableData plantHistoryTableData) {
       try {
           this.sdb = getWritableDatabase();

           ContentValues contentValues = new ContentValues();
           contentValues.put("note", plantHistoryTableData.getNote());
           contentValues.put("date", plantHistoryTableData.getDate().toString());
           this.sdb.insert("plant_history", null, contentValues);

           this.sdb.close();
       }catch (SQLException e){
           Log.d("Database, PHistory add", e.getMessage());
       }
        Log.d("Database", "PHistory succesfully added");
    }

    public void setHarvestHistoryTableData(HarvestHistoryTableData harvestHistoryTableData) {
        try {
            this.sdb = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("note", harvestHistoryTableData.getNote());
            contentValues.put("date", harvestHistoryTableData.getDate().toString());
            this.sdb.insert("harvest_history", null, contentValues);

            this.sdb.close();
        }catch(SQLException e){
            Log.d("Database, HHistory add", e.getMessage());
        }
        Log.d("Database", "HHistory succesfully added");
    }

    public void setWorkHistoryTableData(WorkHistoryTableData workHistoryTableData) {
        try {

            this.sdb = getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("note", workHistoryTableData.getNote());
            contentValues.put("date", workHistoryTableData.getDate().toString());
            this.sdb.insert("work_history", null, contentValues);

            this.sdb.close();

        } catch (SQLException e) {
            Log.d("DataBase, WHistory add", e.getMessage());
        }
        Log.d("Database", "WHistory succesfully added");
    }

    /**
     * Used to get all of <GardenTableData/> from the database, listed from frst row to last row
     * @param id Garden id to get
     * @return ArrayList of <GardenTableData/> Objects
     */
    public ArrayList<GardenTableData> getGardenTableData( int id)
    {
        ArrayList<GardenTableData> listOfGardenTableData = new ArrayList<>();
        try{
            this.sdb = getReadableDatabase();
            GardenTableData gardenTableData = new GardenTableData();

            //String dataExtracter = "SELECT *  FROM garden where id = '"+id+"';";
            Cursor cursor =  this.sdb.query("garden", null, "_id = ?", new String[]{""+id}, null, null, null, null);
            //return empty arraylist if cursor is null
            if (cursor == null)
                return listOfGardenTableData;

            //get all of the gardens beds with notes

            if(cursor.moveToFirst())
            {
                do{
                    gardenTableData.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    gardenTableData.setId(cursor.getInt(cursor.getColumnIndex("bedID")));
                    gardenTableData.setId(cursor.getInt(cursor.getColumnIndex("noteID")));

                    listOfGardenTableData.add(gardenTableData);

                }while (cursor.moveToNext());
            }

        }catch (Exception e)
        {
            Log.d("GardenTableData", e.getMessage());
        }

        return listOfGardenTableData;
    }

    /**
     * Used to get all of <BedTableData/> from the database, listed from frst row to last row
     * @param id Bed id to get
     * @return ArrayList of <BedTableData/> Objects
     */
    public ArrayList<BedTableData> getBedTableData( int id) {
        ArrayList<BedTableData> listOfBedTableData = new ArrayList<>();
        try {
            this.sdb = getReadableDatabase();
            BedTableData bedTableData = new BedTableData();

            //String dataExtracter = "SELECT *  FROM garden where id = '"+id+"';";
            Cursor cursor = sdb.query("bed", null, "_id = ?", new String[]{""+id}, null, null, null, null);

            //return empty arraylist if cursor is null
            if (cursor == null)
                return listOfBedTableData;

            //get all of the gardens beds with notes
            if (cursor.moveToFirst()) {
                do {
                    bedTableData.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    bedTableData.setGardenID(cursor.getInt(cursor.getColumnIndex("gardenID")));

                    listOfBedTableData.add(bedTableData);

                } while (cursor.moveToNext());
            }

            this.sdb.close();
        } catch (Exception e) {
            Log.d("BedTableData", e.getMessage());
        }
        Log.d("Database", "Bed succesfully retrieved");
        return listOfBedTableData;
    }

    /**
     * Used to get <ImageTableData/> from the database, listed from first row to last row
     * @param id Bed id of image to get
     * @return ArrayList of <ImageTableData/> Objects
     */
    public ArrayList<ImageTableData> getImageTableData(int id) {


        ArrayList<ImageTableData> listOfImagaeTableData = new ArrayList<>();
        try {
            this.sdb = getReadableDatabase();
            ImageTableData imageTableData = new ImageTableData();

            Cursor cursor = this.sdb.query("image", null, "bedID = ?", new String[]{""+id}, null, null, null, null);

            //if cursor is empty check to see if its a garden image
            if(!cursor.moveToFirst())
            {
                cursor = this.sdb.query("image", null, "gardenID = ?", new String[]{""+id}, null, null, null, null);
            }
            //return empty arraylist if cursor is null
            if (cursor == null)
                return listOfImagaeTableData;

            //get all of the beds images

            if (cursor.moveToFirst()) {
                do {
                    imageTableData.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    imageTableData.setImage(cursor.getBlob(cursor.getColumnIndex("image")));

                    listOfImagaeTableData.add(imageTableData);

                } while (cursor.moveToNext());
            }
            this.sdb.close();
        } catch (Exception e) {
            Log.d("ImageTableData", e.getMessage());
        }

        Log.d("Database", "Image succesfully retrieved");
        return listOfImagaeTableData;
    }

    /**
     * Used to get <LayoutTableData/> from the database, listed from first row to last row
     * @param id Bed id of layout to get
     * @return ArrayList of <LayoutTableData/> Objects
     */
    public ArrayList<LayoutTableData> getLayoutTableData(int id) {

        ArrayList<LayoutTableData> listOfLayoutTableData = new ArrayList<>();
        try{
            this.sdb = getReadableDatabase();
            LayoutTableData layoutTableData = new LayoutTableData();

            //String dataExtracter = "SELECT *  FROM garden where id = '" + id + "';";
            Cursor cursor = this.sdb.query("layout", null, "bedID = ?", new String[]{""+id}, null, null, null, null);

            //return empty arraylist if cursor is null
            if (cursor == null)
                return listOfLayoutTableData;

            //get all of the beds layouts with notes

            if (cursor.moveToFirst()) {
                do {
                    layoutTableData.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    layoutTableData.setImage(cursor.getBlob(cursor.getColumnIndex("image")));

                    listOfLayoutTableData.add(layoutTableData);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            Log.d("LayoutTableData", e.getMessage());
        }
        Log.d("Database", "Layout succesfully retrieved");
        return listOfLayoutTableData;
    }

    /**
     * Used to get <NoteTableData/> from the database, listed from first row to last row
     * @param id bed id of notes to get
     * @return ArrayList of <NoteTableData/> Objects
     */
    public ArrayList<NoteTableData> getNoteTableData(int id) {

        ArrayList<NoteTableData> listOfNoteTableData = new ArrayList<>();
        try {
            this.sdb = getReadableDatabase();
            NoteTableData noteTableData = new NoteTableData();

            //String dataExtracter = "SELECT *  FROM garden where id = '"+id+"';";
            Cursor cursor = this.sdb.query("note", null, "bedID = ?", new String[]{""+id}, null, null, null, null);

            //if cursor is empty check to see if its a garden image
            if(!cursor.moveToFirst())
            {
                cursor = this.sdb.query("image", null, "gardenID = ?", new String[]{""+id}, null, null, null, null);
            }

            //return empty arraylist if cursor is null
            if (cursor == null)
                return listOfNoteTableData;

            //get all of the beds or gardens notes

            if (cursor.moveToFirst()) {
                do {
                    noteTableData.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    noteTableData.setSubject(cursor.getString(cursor.getColumnIndex("subject")));
                    noteTableData.setNote(cursor.getString(cursor.getColumnIndex("note")));

                    listOfNoteTableData.add(noteTableData);

                } while (cursor.moveToNext());
            }
            this.sdb.close();
        } catch (Exception e) {
            Log.d("NoteTableData", e.getMessage());
        }

        Log.d("Database", "Note succesfully retrieved");
        return listOfNoteTableData;
    }

    /**
     * Used to get <HistoryTableData/> from the database, listed from first row to last row
     * @param id Bed id of history to get
     * @return ArrayList of <HistoryTableData/> Objects
     */
    public ArrayList<HistoryTableData> getHistoryTableData(int id) {
        ArrayList<HistoryTableData> listOfHistoryTableData = new ArrayList<>();
        try{
            this.sdb = getReadableDatabase();


            HistoryTableData historyTableData = new HistoryTableData();

            //String dataExtracter = "SELECT *  FROM garden where id = '"+id+"';";
            Cursor cursor = sdb.query("history", null, "bedID = ?", new String[]{""+id}, null, null, null, null);

            //return empty arraylist if cursor is null
            if (cursor == null)
                return listOfHistoryTableData;

            //get all of the beds history

            if (cursor.moveToFirst()) {
                do {
                    historyTableData.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    historyTableData.setPlantHistoryID(cursor.getInt(cursor.getColumnIndex("plantHistoryID")));
                    historyTableData.setWorkHistoryID(cursor.getInt(cursor.getColumnIndex("workHistoryID")));
                    historyTableData.setHarvestHistoryID(cursor.getInt(cursor.getColumnIndex("harvestHistoryID")));

                    listOfHistoryTableData.add(historyTableData);

                } while (cursor.moveToNext());
            }
            this.sdb.close();
        } catch (Exception e) {
            Log.d("HistoryTableData", e.getMessage());
        }

        Log.d("Database", "History succesfully retrieved");
        return listOfHistoryTableData;
    }

    /**
     * Used to get <PlantHistoryTableData/> from the database, listed from first row to last row
     * @param id Plant Histories id to get
     * @return ArrayList of <PlantHistoryTableData/> Objects
     */
    public ArrayList<PlantHistoryTableData> getPlantHistoryTableData(int id) {
        ArrayList<PlantHistoryTableData> listOfPlantHistoryTableData = new ArrayList<>();
        try {
            this.sdb = getReadableDatabase();


            PlantHistoryTableData plantHistoryTableData = new PlantHistoryTableData();

            //String dataExtracter = "SELECT *  FROM garden where id = '"+id+"';";
            Cursor cursor = this.sdb.query("plant_history", null, "_id = ?", new String[]{""+id}, null, null, null, null);

            //return empty arraylist if cursor is null
            if (cursor == null)
                return listOfPlantHistoryTableData;

            //get all of the beds plant history

            if (cursor.moveToFirst()) {
                do {
                    plantHistoryTableData.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    plantHistoryTableData.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    plantHistoryTableData.setNote(cursor.getString(cursor.getColumnIndex("note")));

                    listOfPlantHistoryTableData.add(plantHistoryTableData);

                } while (cursor.moveToNext());
            }
            this.sdb.close();
        } catch (Exception e) {
            Log.d("PlantHistoryTableData", e.getMessage());
        }

        Log.d("Database", "PHistory succesfully retrieved");
        return listOfPlantHistoryTableData;
    }

    /**
     * Used to get <WorkHistoryTableData/> from the database, listed from first row to last row
     * @param id Work history id to get
     * @return ArrayList of <WorkHistoryTableData/> Objects
     */
    public ArrayList<WorkHistoryTableData> getWorkHistoryTableData(int id) {

        ArrayList<WorkHistoryTableData> listOfWorkHistoryTableData = new ArrayList<>();
        try {
            this.sdb = getReadableDatabase();


            WorkHistoryTableData workHistoryTableData = new WorkHistoryTableData();

            //String dataExtracter = "SELECT *  FROM garden where id = '"+id+"';";
            Cursor cursor = this.sdb.query("work_history", null, "_id = ?", new String[]{""+id}, null, null, null, null);

            //return empty arraylist if cursor is null
            if (cursor == null)
                return listOfWorkHistoryTableData;

            //get all of the beds work history

            if (cursor.moveToFirst()) {
                do {
                    workHistoryTableData.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    workHistoryTableData.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    workHistoryTableData.setNote(cursor.getString(cursor.getColumnIndex("note")));

                    listOfWorkHistoryTableData.add(workHistoryTableData);

                } while (cursor.moveToNext());
            }
            this.sdb.close();
        } catch (Exception e) {
            Log.d("WorkHistoryTableData", e.getMessage());
        }

        Log.d("Database", "WHistory succesfully retrieved");
        return listOfWorkHistoryTableData;
    }

    /**
     * Used to get <HarvestHistoryTableData/> from the database, listed from first row to last row
     * @param id Harvest histories id to get
     * @return ArrayList of <HarvestHistoryTableData/> Objects
     */
    public ArrayList<HarvestHistoryTableData> getHarvestHistoryTableData(int id) {

        ArrayList<HarvestHistoryTableData> listOfHarvestHistoryTableData = new ArrayList<>();

        try {
        this.sdb = getReadableDatabase();

        HarvestHistoryTableData harvestHistoryTableData = new HarvestHistoryTableData();

        //String dataExtracter = "SELECT *  FROM garden where id = '"+id+"';";
        Cursor cursor = this.sdb.query("harvest_history", null, "_id = ?", new String[]{"" + id}, null, null, null, null);

        //return empty arraylist if cursor is null
        if (cursor == null)
            return listOfHarvestHistoryTableData;

        //get all of the beds harvest history

            if (cursor.moveToFirst()) {
                do {
                    harvestHistoryTableData.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                    harvestHistoryTableData.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    harvestHistoryTableData.setNote(cursor.getString(cursor.getColumnIndex("note")));

                    listOfHarvestHistoryTableData.add(harvestHistoryTableData);

                } while (cursor.moveToNext());
            }
            this.sdb.close();
        } catch (Exception e) {
            Log.d("HarvestHistoryTableData", e.getMessage());
        }

        Log.d("Database", "HHistory succesfully retrieved");
        return listOfHarvestHistoryTableData;
    }

    public void removeGardenTableData(int id)
    {
        try{
            this.sdb = getReadableDatabase();
            ArrayList<GardenTableData> gardenTableData = this.getGardenTableData(id);
            //remove bed object
            this.removeBedTableData(gardenTableData.get(0).getBedID());
            //remove garden object
            this.sdb.delete("garden", "_id =?", new String[] {""+id});
            Log.d("Database", "garden Deleted");
        }catch (SQLException e)
        {
            Log.d("Database error", e.getMessage());
        }

        this.sdb.close();
    }

    /**
     * removes <BedTableData/> for whatever bed id is passed and also removes
     * all other data related to bed
     * @param id Bed id to be deleted
     */
    public void removeBedTableData(int id)
    {

        try{
            this.sdb = getReadableDatabase();
            ArrayList<BedTableData> bedTableData = this.getBedTableData(id);
            ArrayList<HistoryTableData> historyTableData = this.getHistoryTableData(id);
            ArrayList<ImageTableData> imageTableData = this.getImageTableData(id);
            ArrayList<LayoutTableData> layoutTableData = this.getLayoutTableData(id);
            ArrayList<NoteTableData> noteTableData = this.getNoteTableData(id);

            this.sdb = getWritableDatabase();
            //delete history of bed
            for(HistoryTableData tempHistoryTableData: historyTableData)
            {
                this.removeHarvestHistoryTableData(tempHistoryTableData.getHarvestHistoryID());
                this.removePlantHistoryTableData(tempHistoryTableData.getPlantHistoryID());
                this.removeWorkHistoryTableData(tempHistoryTableData.getWorkHistoryID());
            }
            this.removeHistoryTableData(bedTableData.get(0).getId());

            //delete images related to bed
            for(ImageTableData tempImageTableData: imageTableData)
            {
                this.removeImageTableData(tempImageTableData.getId());
            }
            //delete Layouts related to this bed
            for(LayoutTableData tempLayoutTableData: layoutTableData)
            {
                this.removeLayoutTableData(tempLayoutTableData.getId());
            }
            //delete notes related to this bed
            for(NoteTableData tempNoteTableData: noteTableData)
            {
                this.removeNoteTableData(tempNoteTableData.getId());
            }

            this.sdb.delete("bed", "_id =?", new String[] {""+id});
            Log.d("Database", "bed Deleted");
        }catch (SQLException e)
        {
            Log.d("Database error", e.getMessage());
        }

        this.sdb.close();
    }

    public void removeImageTableData(int id)
    {
        this.sdb = getWritableDatabase();
        try{
            this.sdb.delete("image", "_id =?", new String[] {""+id});
            Log.d("Database","image Deleted");
        }catch (SQLException e)
        {
            Log.d("Database error", e.getMessage());
        }

        this.sdb.close();
    }

    public void removeNoteTableData(int id)
    {
        this.sdb = getWritableDatabase();
        try{
            this.sdb.delete("note", "_id =?", new String[] {""+id});
            Log.d("Database","note Deleted");
        }catch (SQLException e)
        {
            Log.d("Database error", e.getMessage());
        }

        this.sdb.close();
    }

    public void removeLayoutTableData(int id)
    {
        this.sdb = getWritableDatabase();
        try{
            this.sdb.delete("layout", "_id =?", new String[] {""+id});
            Log.d("Database","layout Deleted");
        }catch (SQLException e)
        {
            Log.d("Database error", e.getMessage());
        }

        this.sdb.close();
    }

    public void removeHistoryTableData(int id)
    {
        this.sdb = getWritableDatabase();
        try{
            this.sdb.delete("history", "bedID =?", new String[] {""+id});
            Log.d("Database","plant History Deleted");
        }catch (SQLException e)
        {
            Log.d("Database error", e.getMessage());
        }

        this.sdb.close();
    }

    public void removePlantHistoryTableData(int id)
    {
        this.sdb = getWritableDatabase();
        try{
            this.sdb.delete("plant_history", "_id =?", new String[] {""+id});
            Log.d("Database","plant History Deleted");
        }catch (SQLException e)
        {
            Log.d("Database error", e.getMessage());
        }

        this.sdb.close();
    }

    public void removeHarvestHistoryTableData(int id)
    {
        this.sdb = getWritableDatabase();
        try{
            this.sdb.delete("harvest_history", "_id =?", new String[] {""+id});
            Log.d("Database","harvest History Deleted");
        }catch (SQLException e)
        {
            Log.d("Database error", e.getMessage());
        }

        this.sdb.close();
    }

    public void removeWorkHistoryTableData(int id)
    {
        this.sdb = getWritableDatabase();
        try{
            this.sdb.delete("work_history", "_id =?", new String[] {""+id});
            Log.d("Database","work History Deleted");
        }catch (SQLException e)
        {
            Log.d("Database error", e.getMessage());
        }

        this.sdb.close();
    }
}
