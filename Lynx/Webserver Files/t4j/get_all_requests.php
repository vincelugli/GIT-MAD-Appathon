<?php
 
/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["reqTo"])) {
    $reqTo = $_GET['reqTo'];

    $result = mysql_query("SELECT * FROM `requests` WHERE reqTo = \"$reqTo\" AND `invatation` IS NULL");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
            $response["reqData"] = array();
            while($row = mysql_fetch_array($result)){
                $orange = array();
                $orange["reqID"] = $row["reqID"];
                $orange["reqFrom"] = $row["reqFrom"];
                $orange["reqTo"] = $row["reqTo"];
                $orange["fb"] = $row["fb"];
                $orange["google"] = $row["google"];
                $orange["twitter"] = $row["twitter"];
                $orange["linkedin"] = $row["linkedin"];
                $orange["invatation"] = $row["invatation"];
                array_push($response["reqData"], $orange);
            }

            $response["success"] = 1;
            echo json_encode($response);
        } else {
            // no user found
            $response["success"] = 0;
            $response["message"] = "YOLO DONT WORK DOE";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "MEOW";
 
        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "hw_Root(oid)equired field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>