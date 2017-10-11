package Service_handler;

public interface SERVER {

    String Server = "https://fastexpresscargo.com/cargoapp";

    String LOGIN = Server + "/api/users/login";
    String SUBMIT_DELIVERY = Server + "/api/deliverystatus/update";
    String HISTORY = Server + "/api/deliverystatus";
    String CHANGE_PASSWORD = Server +"/api/users/changepassword";
    String LOGOUT = Server + "/api/users/logout";
    String OUT_FOR_DELIVERY =  Server + "/api/deliverystatus/create";
    String CREATE_PROPERTY = "http://app.jvhub.co.uk/api/properties/create";
    String SEARCH_RESULT = "http://app.jvhub.co.uk/api/properties/";
    String Get_Property_Detail_By_ID = "http://app.jvhub.co.uk/api/properties/show/";
    String UPDATE_PROFILE = "http://app.jvhub.co.uk/api/users/profileupdate";
    String Contact_Us ="http://app.jvhub.co.uk/api/users/contactus";

}