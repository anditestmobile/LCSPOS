package id.co.lcs.pos.constants;

public class Constants {
    public static final String AUTHORIZATION_LOGIN = "Basic V0VCX0NMSUVOVDpXRUJfQ0xJRU5U";
    public static final String BEARER = "Bearer ";
    public static final String HTTP_HEADER_CONTENT_TYPE = "application/x-www-form-urlencoded";
    public static final String TOKEN = "TOKEN";

    public static final long LONG_1000 = 1000;
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String PAYMENT = "payment";
    public static final String LOGIN = "LOGINS";
    public static final String POS_MENU = "Position Menu";
    public static final String BARCODERESULT = "Barcode Result";
    public static final String WAREHOUSE = "WH Result";
    public static final String LIST_WAREHOUSE = "List WH Result";
    public static final String LIST_BIN = "List Bin Result";
    public static final String BIN = "Bin Result";

    public static final String API_PREFIX = "/Service/ZEN_IntegrationService.svc\\";
    public static final String API_GET_QUOTATION = "GET_QuotationDocument\\";
    public static final String API_GET_QUOTATION_ITEM = "GET_QuotationDocument_Item\\";
    public static final String API_GET_QUOTATION_DETAILS = "GET_QuotationDocument_AllItem\\";
    public static final String API_GET_SALES_ORDER_DETAILS = "GET_OrderDocument_AllItem\\";
    public static final String API_GET_ORDER_ITEM = "GET_OrderDocument_Item\\";
    public static final String API_GET_ORDER = "GET_OrderDocument\\";
    public static final String API_POST_SO_ARINVOICE = "POST_SO_ARInvoice";
    public static final String API_POST_ARINVOICE = "POST_ARInvoice";
    public static final String API_GET_RECEIPT = "POS_Print\\";
    public static final String API_GET_ITEM = "GET_Item\\";
    public static final String API_GET_CUSTOMER = "GET_CustomerDetails\\";
    public static final String API_GET_EMPLOYEE = "GET_EmployeeMasterData\\";
    public static final String API_GET_SN = "GET_SerialNumberDetails\\";
    public static final String API_LOGIN = "POST_Login";

//    public static final String BASE_URL = "http://58.185.191.42:6868";
    public static final String BASE_URL = "http://58.185.191.42:7095";
    public static final int CONNECTION_TIMEOUT = 10; // 10 seconds
    public static final int READ_TIMEOUT = 60;
    public static final int WRITE_TIMEOUT = 60;

    public static final String PREF_NAME = "Qualitas";
    public static final String PREF_NAME_URL = "PREF_URL";
    public static final String PREF_PE = "PREF_PE";
    public static final String PREF_TOKEN = "PREF_TOKEN";
    public static final String KEY_USER = "user";
    public static final String KEY_URL = "key_url";
    public static final String KEY_TOKEN = "key_token";
    public static final String KEY_PE = "key_pe";
    public static final String KEY_DATA = "Login";
    public static final String IS_DATA = "IsLoggedIn";
    public static final String IS_URL = "IsUrl";
    public static final String IS_PE = "IsPE";

    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
    public static final String USER_DETAILS = "USER_DETAILS" ;

    //REQUEST CODE
    public static final int REQUEST_CAMERA_CODE = 4;
    public static final int REQUEST_SIGNATURE_CODE = 5;
    public static final int REQUEST_BARCODE_CODE = 6;
    public static final int REQUEST_READ_PHONE_STATE = 7;
    public static final int CONNECTION_RESOLUTION_REQUEST = 2;

    public static final String FRAGMENT_DIALOG = "dialog";
    public static final String SO_FLAG = "SO_FLAG";
    public static final String OUTPUT_CAMERA = "OutputURI";
    public static final String OUTPUT_BARCODE = "OutputBarcode";
}