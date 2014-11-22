<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
$tempInfo = array();
 
// check for required fields
if (isset($_POST['reqFrom']) && isset($_POST['reqTo'])){
 
    $reqFrom = $_POST['reqFrom'];
    $reqTo = $_POST['reqTo'];

    if (isset($_POST['fb'])) {
        $tempInfo["fb"] = $_POST['fb'];
    }

    if (isset($_POST['google'])) {
        $tempInfo["google"] = $_POST['google'];
    }

    if (isset($_POST['twitter'])) {
        $tempInfo["twitter"] = $_POST['twitter'];
    }

    if (isset($_POST['linkedin'])) {
        $tempInfo["linkedin"] = $_POST['linkedin'];
    }
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO requests(reqFrom, reqTo) VALUES('$reqFrom', '$reqTo')");
    
    foreach ($tempInfo as $key => $value) {
        $sql = "UPDATE requests SET $key=$value WHERE reqFrom = \"$reqFrom\"";
        $result = mysql_query($sql);
        if ($result === FALSE) {
            die(mysql_error());
        }
    }
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Probably works.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>