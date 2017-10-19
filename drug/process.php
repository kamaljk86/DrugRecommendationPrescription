<?php
	include('dbConnect.php');
	$msg='';
	$dname  = $_POST['dname'];
	$mname 	= $_POST['mname'];
	$uname  = $_POST['uname'];
	$rname 	= $_POST['rname'];
	$post_skils = $_POST['post_skils'];
	$chk=mysql_query("SELECT * FROM tablets WHERE Drugname='$dname' AND Manufactured_by='$mname'");
	if(mysql_num_rows($chk) > 0)
	{	
		echo $msg = '<font color="#cc0000">Already registered.!</font>';					
	}
	else
	{
	   	$ins="INSERT into `tablets` (`Drugname`, `Manufactured_by` , `Used_for`, `Rate` , `Description`) VALUES ('$dname', '$mname' , '$uname', '$rname' ,'$post_skils')";
		if (!mysql_query($ins))
		{
			die('Error: ' . mysql_error());
		}
		else
		{
			echo $msg= "<font color='#008000'>Success</font>";
		}
	}
?>