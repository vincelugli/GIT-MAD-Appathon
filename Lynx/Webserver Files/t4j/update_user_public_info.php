<?php
 
/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();

$tempInfo = array();
$username;
// check for required fields
if (isset($_POST['username'])) {
    $username = $_POST['username'];

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

    if (isset($_POST['displayName'])) {
        $displayName = $_POST['displayName'];
        $tempSql = "UPDATE users SET displayName = \"$displayName\" WHERE username = \"$username\"";
        $result = mysql_query($tempSql);
        if ($result === FALSE) {
            die(mysql_error());
        }
    }

    // mysql inserting a new row
    foreach ($tempInfo as $key => $value) {
        $sql = "UPDATE users SET $key=$value WHERE username = \"$username\"";
        $result = mysql_query($sql);
        if ($result === FALSE) {
            die(mysql_error());
        }
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