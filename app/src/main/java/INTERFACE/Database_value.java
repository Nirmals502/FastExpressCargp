package INTERFACE;

public class Database_value {

    //private variables
    String Dockect_number;
    String Spinner_value;
    String Remarks;
    String Photo_url;
    String _TIME;
    String _DETAIL;
    String _DATE;
    String _DATE_with_month;


    // Empty constructor
    public Database_value() {

    }

    // constructor
//    public Database_value(int id, String name, String _phone_number) {
//        this._id = id;
//        this._name = name;
//        this._phone_number = _phone_number;
//    }

    //	// constructor
    public Database_value(String Docket_number, String Spinner_value, String Remarks, String Photo_url) {
        this.Dockect_number = Docket_number;
        this.Spinner_value = Spinner_value;
        this.Remarks = Remarks;
        this.Photo_url = Photo_url;


    }

    // getting ID
    public String getDockect_number() {
        return this.Dockect_number;
    }

    // setting id
    public void setDockect_number(String docket) {
        this.Dockect_number = docket;
    }

    // getting name
    public String getSpinner_value() {
        return this.Spinner_value;
    }

    // setting name
    public void setSpinner_value(String Spinner_value) {
        this.Spinner_value = Spinner_value;
    }

    // getting phone number
    public String getRemarks() {
        return this.Remarks;
    }

    // setting phone number
    public void setRemarksr(String Remarks) {
        this.Remarks = Remarks;
    }

    // setting getting tittle
    public String getPhoto_url() {
        return this.Photo_url;
    }

    // setting tittle
    public void set_Photo_url(String Photo_url) {
        this.Photo_url = Photo_url;
    }


    // setting getting time
    public String get_time() {
        return this._TIME;
    }

    // setting time
    public void set_TIME(String time) {
        this._TIME = time;
    }

    // setting getting DETAIL
    public String get_DETAIL() {
        return this._DETAIL;
    }

    // setting DETAIL
    public void set_DETAIL(String detail) {
        this._DETAIL = detail;
    }

    // setting getting DATE
    public String get_DATE() {
        return this._DATE;
    }

    // setting DATE
    public void set_DATE(String DATE) {this._DATE = DATE;
    }
    public String get_DATE_with_month() {
        return this._DATE_with_month;
    }

    // setting DATE
    public void set_DATE_with_month(String DATE) {
        this._DATE_with_month = DATE;
    }
}
