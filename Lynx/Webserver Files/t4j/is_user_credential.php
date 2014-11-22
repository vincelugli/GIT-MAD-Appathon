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
if (isset($_GET["username"]) && isset($_GET['password'])) {
    $username = $_GET['username'];
    $password = $_GET['password'];
 
    // get a product from products table
    // SELECT * FROM `users` WHERE username = "test"
    // "SELECT *FROM users WHERE username = $username"
    $result = mysql_query("SELECT username, password FROM `users` WHERE username = \"$username\"");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $user = array();
            $user["username"] = $result["username"];
            $user["password"] = $result["password"];

            if($user["username"]==$username && $user["password"]==$password){
                // success
                $response["success"] = 1;
            }

            else{
                $response["success"] = 0;
                $response["message"] = "BAD USER THINGS";
            }

            
 
            // user node
            // $response["user"] = array();
 
            // array_push($response["user"], $user);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no user found
            $response["success"] = 0;
            $response["message"] = "No user FAIL found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no user found
        $response["success"] = 0;
        $response["message"] = "No user found";
 
        // echo no users JSON
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