<?php
	$con=mysql_connect('localhost','drug','drug' );
	if(!$con)
		die('Database connection error..' .mysql_error());
	// else
	// 	echo "Database Connected!!!";

	$db=mysql_select_db("drug",$con);

	if(!$db)
		die('Database selection error..' .mysql_error());
	// else
	// 	echo "Database Selected Conneted!!!";
?>