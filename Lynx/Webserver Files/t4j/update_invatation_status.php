<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();

$username;
// check for required fields
if (isset($_POST['reqID']) && isset($_POST['invatation'])) {
    $reqID = $_POST['reqID'];
    $invatation = $_POST['invatation'];

 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();

    // UPDATE requests SET invatation = 1 WHERE reqID = 2
    $sql = "UPDATE requests SET invatation =  $invatation WHERE reqID = $reqID";
    $result = mysql_query($sql);
    if ($result === FALSE) {
        die(mysql_error());
    }

    // $result = mysql_query("INSERT INTO users(username, password, displayName) VALUES('$username', '$password', '$displayName')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "ALL UPDATED WOOOOOOT.";
 
        // echoing JSON response
        echo json_encode($response);
    } 
    else {
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