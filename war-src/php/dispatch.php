<?php
/*
 * PHP example of the dispatch system.
 *  
 * This file looks up the class path for finding the service. 
 * This means that the folders should look exactly like the 
 * java equivalent service file location
 * 		See: MotDService for an example
 */

$json = file_get_contents('php://input');

$params = json_decode($json);

$interface = $_GET['i'];
$method = $_GET['m'];
$classname = substr($interface,strrpos($interface,'.')+1);

// This is a unsafe method. (can be used to navigate the native filesystem for PHP files)
$file = str_replace('.','/',$interface) . ".php";

require_once($file);

$instance = new $classname();

$result = call_user_func_array(array($instance,$method),$params);

print_r(json_encode($result));
?>
