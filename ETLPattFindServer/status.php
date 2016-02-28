<?php
	include("functions.php");
	html_start("results");

	if (authorised()) {

		echo '<h4 class="heading">Task status</h4>';
		if (!isset($_GET["task"]) || !file_exists($tasksDir."/".$_GET["task"])) {
			alertMsg("danger", "Invalid task ID!");
		} else {
			$dir = $tasksDir."/".$_GET["task"];
			$infoFile = fopen("$dir/inputInfo.txt", "r");
			$taskName = fgets($infoFile);
			$startTime = fgets($infoFile);
			fgets($infoFile);	// Skip task directory
			$inputFile = fgets($infoFile);
			fclose($infoFile);

			echo "<p><b>Task name:</b> $taskName</p>\n";
			echo "<p><b>Submit time:</b> $startTime</p>\n";
			echo "<p><b>Input file:</b> $inputFile</p><br />\n";

			echo "<p><b>Status:</b></p>\n";
			echo "<pre>\n";
			$statusFilePath = "$dir/sysout.txt";
			if (!file_exists($statusFilePath)) {
				echo "Error: status file not found.";
			} else {
				$statusFile = fopen($statusFilePath, "r");
				while(!feof($statusFile)) {
					echo fgets($statusFile);
				}
				fclose($statusFile);
			}
			echo "</pre>\n";
		}
		echo '<div id="end"></div>';
		
	} // if (authorised())
	html_end("home");
?>