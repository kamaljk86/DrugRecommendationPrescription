<?php
	include('dbConnect.php');
	$chk=mysql_query("SELECT * from `tablets`");
	if(mysql_num_rows($chk) > 0)
	{	
		$result = array();
		while($rlt = mysql_fetch_assoc($chk))
		{
			$result[] = $rlt;
		}
		echo json_encode($result);
	}
	else
	{
	   $res = array('Status' => "Failed", "msg" => "No Datas Available"); 
   	   echo json_encode($res);
	}
?>