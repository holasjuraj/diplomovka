<?php
	if (isset($_POST["submit"])){
		usleep(1000000); // Sleep for 1 second - wait until deamon creates info file

		$status = true;
		$t = time();
		$timestamp = date("Y-m-d-H-i-s", $t);
		$startTimeText = date("Y/m/d H:i:s", $t);
		$thisTaskDir = "$tasksDir/$timestamp";


		if (!isset($_POST["threads"]) && ($_POST["threads"] > 4 || $_POST["threads"] < 0)) {
			$status = false;
		}

		$destination = false;
		if ($status && isset($_FILES["inputFile"]["error"]) && $_FILES["inputFile"]["error"]!=4) {
			$filename = clr(pathinfo($_FILES["inputFile"]["name"], PATHINFO_FILENAME));                 // $filename == "file";
			$basename = clr(pathinfo($_FILES["inputFile"]["name"], PATHINFO_BASENAME));                 // $basename == "file.ext";
			$extension = clr(strtolower(pathinfo($_FILES["inputFile"]["name"], PATHINFO_EXTENSION)));   // $extension == "ext";
			$supported_extensions = array("xml", "zip");

			if (!in_array($extension, $supported_extensions)) {
				alertMsg("danger", "File upload failed! Unsupported file format.");
				$status = false;
			} else {

				if (!file_exists($thisTaskDir)) {
					mkdir($thisTaskDir, 0711, true);
				}
				$destination = "$thisTaskDir/$basename";
				$temp_name = $_FILES["inputFile"]["tmp_name"];
				$move = move_uploaded_file($temp_name, $destination);
				if (!$move || $_FILES["inputFile"]["error"] > 0) {
					alertMsg("danger", "File upload failed! Upload error: ".$_FILES["inputFile"]["error"].
								", file move error: ".((!$move) ? "yes" : "no"));
					$status = false;
				}
				else{
					chmod($destination, 0700);	// apache do not have access to the file from now!
				}
			}
		}

		if($status && $destination){
			$text = $_POST["taskName"]."\n"
					."$startTimeText\n"
					."$thisTaskDir\n"
					."$basename\n"
					."# Parameters\n"
					."min-pattern-size: ".$_POST["min-pattern-size"]."\n"
					."threads:".$_POST["threads"]."\n"
					."comparing-method: ".$_POST["comparing-method"]."\n"
					."editdistance-early-stopping: ".$_POST["editdistance-early-stopping"]."\n"
					."qgram-size: ".$_POST["qgram-size"]."\n"
					."clustering-method: ".$_POST["clustering-method"]."\n"
					."clustering-threshold: ".$_POST["clustering-threshold"]."\n";
			$inputInfoFile = fopen("$newTaskDir/$timestamp-inputInfo.txt", "w");
			fwrite($inputInfoFile, $text);
			fclose($inputInfoFile);
			alertMsg("success", "Task successfully submited!");
		}
	}
?>